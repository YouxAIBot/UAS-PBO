package projekPBO.projek;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekPBO.projek.dao.ReviewDAO;

import java.util.List;

public class ReviewPanel {

    public static VBox createReviewPanel(String filmName, TextArea historyArea) {
        VBox mainPanel = new VBox(16);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setStyle("-fx-background-color: #0f0f0f;");

        // Film selection ComboBox - allow manual entry of any film name
        VBox filmSelectorSection = new VBox(10);
        filmSelectorSection.setPadding(new Insets(16));
        filmSelectorSection.setStyle("-fx-border-color: #333333; -fx-border-radius: 8; -fx-background-color: #1a1a1a;");
        
        Label selectorTitle = new Label("🎬 SELECT FILM TO REVIEW");
        selectorTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e50914;");
        
        ComboBox<String> filmCombo = new ComboBox<>();
        filmCombo.setPromptText("Type or select a film...");
        filmCombo.setEditable(true);
        filmCombo.setMaxWidth(Double.MAX_VALUE);
        filmCombo.setPrefHeight(40);
        List<String> filmNames = ReviewDAO.getAllFilmNamesWithReviews();
        if (!filmNames.isEmpty()) {
            filmCombo.getItems().addAll(filmNames);
        }
        
        filmSelectorSection.getChildren().addAll(selectorTitle, filmCombo);

        // Review panel that updates based on selection
        VBox reviewPanel = new VBox(12);
        reviewPanel.setStyle(
                "-fx-background-color: #513b3b; " +
                "-fx-border-color: #2a2a2a; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-padding: 16;"
        );

        // Title
        Label titleLabel = new Label("⭐ REVIEWS FOR: " + (filmName.isEmpty() ? "Select a film" : filmName));
        titleLabel.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-padding: 0 0 12 0;"
        );

        // Rating info
        HBox ratingInfoBox = new HBox(15);
        ratingInfoBox.setAlignment(Pos.CENTER_LEFT);

        double avgRating = filmName.isEmpty() ? 0 : ReviewDAO.getAverageRating(filmName);
        int reviewCount = filmName.isEmpty() ? 0 : ReviewDAO.getReviewCount(filmName);

        String starsInfo = avgRating > 0 ? "⭐".repeat((int) Math.round(avgRating)) : "No ratings yet";
        Label ratingLabel = new Label(String.format("Average: %s (%.2f/5)", starsInfo, avgRating));
        ratingLabel.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 12px;");

        Label countLabel = new Label(String.format("Total Reviews: %d", reviewCount));
        countLabel.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");

        ratingInfoBox.getChildren().addAll(ratingLabel, countLabel);

        // Review list area
        ListView<String> reviewListView = new ListView<>();
        reviewListView.setPrefHeight(250);
        reviewListView.setStyle("-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e5e5e5;");
        if (!filmName.isEmpty()) {
            refreshReviewList(reviewListView, filmName);
        }
        
        // Review details
        VBox reviewDetailsBox = new VBox(8);
        reviewDetailsBox.setPadding(new Insets(12));
        reviewDetailsBox.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #444; -fx-border-width: 1; -fx-border-radius: 6;");
        
        Label reviewDetailsTitle = new Label("💬 Review Details:");
        reviewDetailsTitle.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        TextArea reviewDetailsArea = new TextArea();
        reviewDetailsArea.setEditable(false);
        reviewDetailsArea.setWrapText(true);
        reviewDetailsArea.setPrefRowCount(5);
        reviewDetailsArea.setStyle("-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e5e5e5; -fx-font-size: 11px;");
        reviewDetailsArea.setText("Select a review to see details");
        
        reviewDetailsBox.getChildren().addAll(reviewDetailsTitle, reviewDetailsArea);
        VBox.setVgrow(reviewDetailsArea, Priority.ALWAYS);
        
        reviewListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && filmCombo.getValue() != null) {
                showReviewDetailsPanel(newVal, reviewDetailsArea);
            }
        });

        // Add review form
        VBox addReviewBox = new VBox(10);
        addReviewBox.setStyle(
                "-fx-background-color: #2a2a2a; " +
                "-fx-border-color: #444; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-padding: 12;"
        );

        Label addLabel = new Label("➕ Add Your Review:");
        addLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: bold;");

        ComboBox<Integer> ratingCombo = new ComboBox<>();
        ratingCombo.getItems().addAll(1, 2, 3, 4, 5);
        ratingCombo.setPromptText("Select rating (1-5)");
        ratingCombo.setPrefHeight(32);
        ratingCombo.setStyle("-fx-font-size: 11px;");
        ratingCombo.setMaxWidth(Double.MAX_VALUE);

        Label starDisplay = new Label("☆☆☆☆☆");
        starDisplay.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 16px;");

        ratingCombo.setOnAction(e -> {
            if (ratingCombo.getValue() != null) {
                int rating = ratingCombo.getValue();
                String starsRating = "⭐".repeat(rating) + "☆".repeat(5 - rating);
                starDisplay.setText(starsRating);
            }
        });

        TextArea commentArea = new TextArea();
        commentArea.setPrefRowCount(4);
        commentArea.setWrapText(true);
        commentArea.setStyle("-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e5e5e5; -fx-padding: 8;");
        commentArea.setPromptText("Share your thoughts about this film...");

        Button btnSubmit = new Button("💬 Submit Review");
        btnSubmit.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setPrefHeight(36);
        btnSubmit.setOnAction(e -> {
            String filmValue = filmCombo.getValue();
            if (filmValue == null || filmValue.trim().isEmpty() || ratingCombo.getValue() == null || commentArea.getText().trim().isEmpty()) {
                historyArea.appendText("❌ Please select/enter film, rating and write a comment!\n");
                return;
            }

            String selectedFilm = filmValue.trim();
            int rating = ratingCombo.getValue();
            String comment = commentArea.getText().trim();
            int transactionId = BioskopFX.getLastTransactionId();

            if (transactionId <= 0) {
                historyArea.appendText("❌ No recent transaction found. Please make a booking first!\n");
                return;
            }

            try {
                ReviewDAO.insertReview(transactionId, selectedFilm, rating, comment);
                historyArea.appendText("✅ Review added successfully for " + selectedFilm + "!\n");
                ratingCombo.setValue(null);
                starDisplay.setText("☆☆☆☆☆");
                commentArea.clear();
                filmCombo.setValue(null);
                if (!filmNames.contains(selectedFilm)) {
                    filmCombo.getItems().add(selectedFilm);
                }
                refreshReviewList(reviewListView, selectedFilm);
            } catch (Exception ex) {
                historyArea.appendText("❌ Error adding review: " + ex.getMessage() + "\n");
            }
        });

        addReviewBox.getChildren().addAll(
                addLabel,
                new Label("Rating:") {{ setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 11px;"); }},
                ratingCombo,
                starDisplay,
                new Label("Comment:") {{ setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 11px;"); }},
                commentArea,
                btnSubmit
        );

        // Film selector listener
        filmCombo.setOnAction(e -> {
            if (filmCombo.getValue() != null) {
                String selected = filmCombo.getValue();
                titleLabel.setText("⭐ REVIEWS FOR: " + selected);
                refreshReviewList(reviewListView, selected);
                double newAvg = ReviewDAO.getAverageRating(selected);
                int newCount = ReviewDAO.getReviewCount(selected);
                String newStarsInfo = newAvg > 0 ? "⭐".repeat((int) Math.round(newAvg)) : "No ratings yet";
                ratingLabel.setText(String.format("Average: %s (%.2f/5)", newStarsInfo, newAvg));
                countLabel.setText(String.format("Total Reviews: %d", newCount));
                reviewDetailsArea.setText("Select a review to see details");
            }
        });

        // Refresh button
        Button btnRefresh = new Button("🔄 Refresh Reviews");
        btnRefresh.setStyle("-fx-font-size: 11px;");
        btnRefresh.setMaxWidth(Double.MAX_VALUE);
        btnRefresh.setPrefHeight(32);
        btnRefresh.setOnAction(e -> {
            if (filmCombo.getValue() != null) {
                String selected = filmCombo.getValue();
                refreshReviewList(reviewListView, selected);
                double newAvg = ReviewDAO.getAverageRating(selected);
                int newCount = ReviewDAO.getReviewCount(selected);
                String newStarsInfo = newAvg > 0 ? "⭐".repeat((int) Math.round(newAvg)) : "No ratings yet";
                ratingLabel.setText(String.format("Average: %s (%.2f/5)", newStarsInfo, newAvg));
                countLabel.setText(String.format("Total Reviews: %d", newCount));
            }
        });

        reviewPanel.getChildren().addAll(
                titleLabel,
                ratingInfoBox,
                new Label("Recent Reviews:"),
                reviewListView,
                reviewDetailsBox,
                btnRefresh,
                new Separator(),
                addReviewBox
        );

        VBox mainWrapper = new VBox(16);
        mainWrapper.setPadding(new Insets(20));
        mainWrapper.setStyle("-fx-background-color: #0f0f0f;");
        
        ScrollPane scrollPane = new ScrollPane(new VBox(16, filmSelectorSection, reviewPanel));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0f0f0f; -fx-border-color: transparent;");
        
        mainWrapper.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return mainWrapper;
    }

    private static void refreshReviewList(ListView<String> listView, String filmName) {
        listView.getItems().clear();
        List<Review> reviews = ReviewDAO.getReviewsByFilm(filmName);

        if (reviews.isEmpty()) {
            listView.getItems().add("No reviews yet. Be the first to review!");
        } else {
            for (Review review : reviews) {
                String starsReview = "⭐".repeat(review.getRating());
                String item = String.format("%s (%d/5)\n💬 %s\n📅 %s",
                        starsReview,
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt().toLocalDate());
                listView.getItems().add(item);
            }
        }
    }

    private static void showReviewDetailsPanel(String reviewItem, TextArea detailsArea) {
        try {
            String[] lines = reviewItem.split("\n");
            if (lines.length >= 2) {
                String ratingLine = lines[0];
                String rating = ratingLine.replaceAll("[^0-9]", "").substring(0, 1);
                String comment = lines[1].replace("💬 ", "");
                String date = lines[2].replace("📅 ", "");
                
                detailsArea.setText("Rating: " + rating + "/5\nComment: " + comment + "\nDate: " + date);
            }
        } catch (Exception e) {
            detailsArea.setText("Select a review to see details");
        }
    }
}
