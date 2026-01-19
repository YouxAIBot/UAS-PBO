package projekPBO.projek;

import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekPBO.projek.dao.ReviewDAO;
import projekPBO.projek.dao.TransactionDAO;

public class ManagementPanel {

    public static VBox createManagementPanel(TextArea historyArea) {
        VBox mainPanel = new VBox(16);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setStyle("-fx-background-color: #0f0f0f;");

        // ========== TRANSACTION MANAGEMENT SECTION ==========
        VBox transactionSection = createSection("📋 TRANSACTION MANAGEMENT");

        // Transaction List
        ListView<String> transactionList = new ListView<>();
        transactionList.setPrefHeight(300);
        transactionList.setStyle("-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e5e5e5;");
        refreshTransactionList(transactionList);

        Button btnRefreshTrans = new Button("🔄 Refresh");
        btnRefreshTrans.setStyle("-fx-font-size: 11px;");
        btnRefreshTrans.setPrefHeight(32);
        btnRefreshTrans.setOnAction(e -> refreshTransactionList(transactionList));

        Button btnDeleteTrans = new Button("🗑️ Delete Selected");
        btnDeleteTrans.setStyle("-fx-font-size: 11px;");
        btnDeleteTrans.setPrefHeight(32);
        btnDeleteTrans.setOnAction(e -> {
            if (transactionList.getSelectionModel().getSelectedIndex() >= 0) {
                String selected = transactionList.getSelectionModel().getSelectedItem();
                int id = Integer.parseInt(selected.split(" - ")[0].replaceAll("[^0-9]", ""));
                TransactionDAO.deleteTransaction(id);
                historyArea.appendText("✅ Transaction #" + id + " deleted!\n");
                refreshTransactionList(transactionList);
            }
        });

        HBox transBtnBox = new HBox(10);
        transBtnBox.getChildren().addAll(btnRefreshTrans, btnDeleteTrans);
        HBox.setHgrow(btnRefreshTrans, Priority.ALWAYS);
        HBox.setHgrow(btnDeleteTrans, Priority.ALWAYS);
        btnRefreshTrans.setMaxWidth(Double.MAX_VALUE);
        btnDeleteTrans.setMaxWidth(Double.MAX_VALUE);

        transactionSection.getChildren().addAll(
                new Label("Transaction List:"),
                transactionList,
                transBtnBox
        );

        // ========== REVIEW MANAGEMENT SECTION ==========
        VBox reviewSection = createSection("⭐ REVIEW MANAGEMENT");

        // Review List
        ListView<String> reviewList = new ListView<>();
        reviewList.setPrefHeight(300);
        reviewList.setStyle("-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e5e5e5;");
        refreshReviewList(reviewList);

        Button btnRefreshReview = new Button("🔄 Refresh");
        btnRefreshReview.setStyle("-fx-font-size: 11px;");
        btnRefreshReview.setPrefHeight(32);
        btnRefreshReview.setOnAction(e -> refreshReviewList(reviewList));

        Button btnDeleteReview = new Button("🗑️ Delete Selected");
        btnDeleteReview.setStyle("-fx-font-size: 11px;");
        btnDeleteReview.setPrefHeight(32);
        btnDeleteReview.setOnAction(e -> {
            if (reviewList.getSelectionModel().getSelectedIndex() >= 0) {
                String selected = reviewList.getSelectionModel().getSelectedItem();
                int id = Integer.parseInt(selected.split(" - ")[0].replaceAll("[^0-9]", ""));
                ReviewDAO.deleteReview(id);
                historyArea.appendText("✅ Review #" + id + " deleted!\n");
                refreshReviewList(reviewList);
            }
        });

        HBox reviewBtnBox = new HBox(10);
        reviewBtnBox.getChildren().addAll(btnRefreshReview, btnDeleteReview);
        HBox.setHgrow(btnRefreshReview, Priority.ALWAYS);
        HBox.setHgrow(btnDeleteReview, Priority.ALWAYS);
        btnRefreshReview.setMaxWidth(Double.MAX_VALUE);
        btnDeleteReview.setMaxWidth(Double.MAX_VALUE);

        reviewSection.getChildren().addAll(
                new Label("Review List:"),
                reviewList,
                reviewBtnBox
        );

        // ========== STATISTICS SECTION ==========
        VBox statsSection = createSection("📊 STATISTICS");

        Label lblTotalTrans = new Label("Total Transactions: 0");
        lblTotalTrans.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 13px;");

        Label lblTotalReviews = new Label("Total Reviews: 0");
        lblTotalReviews.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 13px;");

        Label lblAvgRating = new Label("Average Rating: 0.00 ⭐");
        lblAvgRating.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 13px;");

        Button btnRefreshStats = new Button("🔄 Refresh Stats");
        btnRefreshStats.setMaxWidth(Double.MAX_VALUE);
        btnRefreshStats.setPrefHeight(36);
        btnRefreshStats.setStyle("-fx-font-size: 12px;");
        btnRefreshStats.setOnAction(e -> {
            List<Map<String, Object>> transactions = TransactionDAO.getAllTransactions();
            List<Review> reviews = ReviewDAO.getAllReviews();

            lblTotalTrans.setText("Total Transactions: " + transactions.size());
            lblTotalReviews.setText("Total Reviews: " + reviews.size());

            // Calculate average rating
            double avgRating = 0;
            if (!reviews.isEmpty()) {
                avgRating = reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);
            }
            lblAvgRating.setText(String.format("Average Rating: %.2f ⭐", avgRating));
        });

        statsSection.getChildren().addAll(
                lblTotalTrans,
                lblTotalReviews,
                lblAvgRating,
                btnRefreshStats
        );

        // Add all sections to main panel
        ScrollPane scrollPane = new ScrollPane(mainPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0f0f0f; -fx-border-color: transparent;");

        VBox wrapper = new VBox();
        wrapper.getChildren().addAll(transactionSection, reviewSection, statsSection);
        wrapper.setPadding(new Insets(20));
        wrapper.setSpacing(16);
        wrapper.setStyle("-fx-background-color: #0f0f0f;");

        // Trigger initial stats update
        btnRefreshStats.fire();

        return wrapper;
    }

    private static void refreshTransactionList(ListView<String> listView) {
        listView.getItems().clear();
        List<Map<String, Object>> transactions = TransactionDAO.getAllTransactions();
        for (Map<String, Object> trans : transactions) {
            String item = String.format("ID: %d - %s | Rp %,d | %s",
                    trans.get("id"),
                    trans.get("film_name"),
                    trans.get("total"),
                    trans.get("payment_method"));
            listView.getItems().add(item);
        }
    }

    private static void refreshReviewList(ListView<String> listView) {
        listView.getItems().clear();
        List<Review> reviews = ReviewDAO.getAllReviews();
        for (Review review : reviews) {
            String stars = "⭐".repeat(review.getRating());
            String item = String.format("ID: %d - %s | %s | %s",
                    review.getId(),
                    review.getFilmName(),
                    stars,
                    review.getComment().substring(0, Math.min(30, review.getComment().length())));
            listView.getItems().add(item);
        }
    }

    private static VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setStyle(
                "-fx-background-color: #513b3b; " +
                "-fx-border-color: #2a2a2a; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-padding: 16;"
        );
        if (!title.isEmpty()) {
            Label sectionTitle = new Label(title);
            sectionTitle.setStyle(
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-text-fill: #ffffff; " +
                    "-fx-padding: 0 0 8 0;"
            );
            section.getChildren().add(sectionTitle);
        }
        return section;
    }
}
