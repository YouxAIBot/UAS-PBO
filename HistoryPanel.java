package projekPBO.projek;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekPBO.projek.dao.ReviewDAO;
import projekPBO.projek.dao.TransactionDAO;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class HistoryPanel {

    public static VBox createHistoryPanel(TextArea historyArea) {
        VBox mainPanel = new VBox(16);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setStyle("-fx-background-color: #0f0f0f;");

        // ========== PURCHASE HISTORY SECTION ==========
        VBox historySection = createSection("🎬 PURCHASE HISTORY");

        // Transaction List with Details
        ListView<String> transactionList = new ListView<>();
        transactionList.setPrefHeight(400);
        transactionList.setStyle("-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e5e5e5; -fx-font-size: 11px;");
        
        // Details Panel
        VBox detailsPanel = new VBox(8);
        detailsPanel.setPadding(new Insets(12));
        detailsPanel.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #444; -fx-border-width: 1; -fx-border-radius: 6;");
        
        Label detailsTitle = new Label("📋 Transaction Details:");
        detailsTitle.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        Label detailsContent = new Label("Select a transaction to view details");
        detailsContent.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 11px;");
        detailsContent.setWrapText(true);
        
        ScrollPane detailsScroll = new ScrollPane(detailsContent);
        detailsScroll.setStyle("-fx-background-color: #1a1a1a;");
        detailsScroll.setFitToWidth(true);
        
        detailsPanel.getChildren().addAll(detailsTitle, detailsScroll);
        VBox.setVgrow(detailsScroll, Priority.ALWAYS);

        // Populate transaction list
        refreshTransactionList(transactionList, detailsContent);

        // Selection listener for details
        transactionList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    String[] parts = newVal.split(" - ");
                    if (parts.length > 0) {
                        String idStr = parts[0].replaceAll("[^0-9]", "");
                        if (!idStr.isEmpty()) {
                            int transId = Integer.parseInt(idStr);
                            showTransactionDetails(transId, detailsContent);
                        }
                    }
                } catch (Exception e) {
                    detailsContent.setText("Error loading details");
                }
            }
        });

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle("-fx-font-size: 11px;");
        btnRefresh.setPrefHeight(36);
        btnRefresh.setPrefWidth(150);
        btnRefresh.setOnAction(e -> {
            refreshTransactionList(transactionList, detailsContent);
            detailsContent.setText("Select a transaction to view details");
        });

        Button btnDelete = new Button("🗑️ Delete");
        btnDelete.setStyle("-fx-font-size: 11px;");
        btnDelete.setPrefHeight(36);
        btnDelete.setPrefWidth(150);
        btnDelete.setOnAction(e -> {
            String selected = transactionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    String[] parts = selected.split(" - ");
                    String idStr = parts[0].replaceAll("[^0-9]", "");
                    if (!idStr.isEmpty()) {
                        int transId = Integer.parseInt(idStr);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm Delete");
                        alert.setHeaderText("Delete Transaction?");
                        alert.setContentText("Are you sure want to delete this transaction?");
                        if (alert.showAndWait().isPresent() && alert.getResult() == ButtonType.OK) {
                            TransactionDAO.deleteTransaction(transId);
                            historyArea.appendText("✅ Transaction #" + transId + " deleted!\n");
                            refreshTransactionList(transactionList, detailsContent);
                            detailsContent.setText("Select a transaction to view details");
                        }
                    }
                } catch (Exception ex) {
                    historyArea.appendText("❌ Error deleting transaction: " + ex.getMessage() + "\n");
                }
            } else {
                historyArea.appendText("❌ Please select a transaction to delete\n");
            }
        });

        buttonBox.getChildren().addAll(btnRefresh, btnDelete);

        historySection.getChildren().addAll(
                new Label("Your Purchases:"),
                transactionList,
                detailsPanel,
                buttonBox
        );

        // ========== REVIEWS SECTION ==========
        VBox reviewsSection = createSection("⭐ YOUR REVIEWS");

        ListView<String> reviewsList = new ListView<>();
        reviewsList.setPrefHeight(250);
        reviewsList.setStyle("-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e5e5e5; -fx-font-size: 11px;");

        VBox reviewDetailsPanel = new VBox(8);
        reviewDetailsPanel.setPadding(new Insets(12));
        reviewDetailsPanel.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #444; -fx-border-width: 1; -fx-border-radius: 6;");
        
        Label reviewDetailsTitle = new Label("📝 Review Details:");
        reviewDetailsTitle.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        TextArea reviewDetailsContent = new TextArea();
        reviewDetailsContent.setEditable(false);
        reviewDetailsContent.setWrapText(true);
        reviewDetailsContent.setPrefRowCount(4);
        reviewDetailsContent.setStyle("-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e5e5e5;");
        
        reviewDetailsPanel.getChildren().addAll(reviewDetailsTitle, reviewDetailsContent);
        VBox.setVgrow(reviewDetailsContent, Priority.ALWAYS);

        refreshReviewsList(reviewsList, reviewDetailsContent);

        reviewsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    String[] parts = newVal.split(" - ");
                    if (parts.length > 0) {
                        String idStr = parts[0].replaceAll("[^0-9]", "");
                        if (!idStr.isEmpty()) {
                            int reviewId = Integer.parseInt(idStr);
                            showReviewDetails(reviewId, reviewDetailsContent);
                        }
                    }
                } catch (Exception e) {
                    reviewDetailsContent.setText("Error loading review details");
                }
            }
        });

        Button btnRefreshReviews = new Button("🔄 Refresh");
        btnRefreshReviews.setStyle("-fx-font-size: 11px;");
        btnRefreshReviews.setPrefHeight(36);
        btnRefreshReviews.setPrefWidth(150);
        btnRefreshReviews.setOnAction(e -> {
            refreshReviewsList(reviewsList, reviewDetailsContent);
            reviewDetailsContent.clear();
        });

        Button btnDeleteReview = new Button("🗑️ Delete");
        btnDeleteReview.setStyle("-fx-font-size: 11px;");
        btnDeleteReview.setPrefHeight(36);
        btnDeleteReview.setPrefWidth(150);
        btnDeleteReview.setOnAction(e -> {
            String selected = reviewsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    String[] parts = selected.split(" - ");
                    String idStr = parts[0].replaceAll("[^0-9]", "");
                    if (!idStr.isEmpty()) {
                        int reviewId = Integer.parseInt(idStr);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm Delete");
                        alert.setHeaderText("Delete Review?");
                        alert.setContentText("Are you sure want to delete this review?");
                        if (alert.showAndWait().isPresent() && alert.getResult() == ButtonType.OK) {
                            ReviewDAO.deleteReview(reviewId);
                            historyArea.appendText("✅ Review #" + reviewId + " deleted!\n");
                            refreshReviewsList(reviewsList, reviewDetailsContent);
                            reviewDetailsContent.clear();
                        }
                    }
                } catch (Exception ex) {
                    historyArea.appendText("❌ Error deleting review: " + ex.getMessage() + "\n");
                }
            } else {
                historyArea.appendText("❌ Please select a review to delete\n");
            }
        });

        HBox reviewButtonBox = new HBox(10);
        reviewButtonBox.setAlignment(Pos.CENTER);
        reviewButtonBox.getChildren().addAll(btnRefreshReviews, btnDeleteReview);

        reviewsSection.getChildren().addAll(
                new Label("Your Reviews:"),
                reviewsList,
                reviewDetailsPanel,
                reviewButtonBox
        );

        // ========== STATISTICS ==========
        VBox statsSection = createSection("📊 YOUR STATISTICS");

        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        Label statsTransCount = new Label("📋 Total Purchases: 0");
        statsTransCount.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 12px; -fx-font-weight: bold;");

        Label statsReviewCount = new Label("⭐ Total Reviews: 0");
        statsReviewCount.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 12px; -fx-font-weight: bold;");

        Label statsTotalSpent = new Label("💰 Total Spent: Rp 0");
        statsTotalSpent.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 12px; -fx-font-weight: bold;");

        statsBox.getChildren().addAll(statsTransCount, statsReviewCount, statsTotalSpent);

        Button btnUpdateStats = new Button("🔄 Update Stats");
        btnUpdateStats.setStyle("-fx-font-size: 11px;");
        btnUpdateStats.setMaxWidth(Double.MAX_VALUE);
        btnUpdateStats.setPrefHeight(36);
        btnUpdateStats.setOnAction(e -> updateStats(statsTransCount, statsReviewCount, statsTotalSpent));

        statsSection.getChildren().addAll(statsBox, btnUpdateStats);

        // Initial stats update
        updateStats(statsTransCount, statsReviewCount, statsTotalSpent);

        // Main scroll pane
        VBox wrapper = new VBox(16);
        wrapper.getChildren().addAll(historySection, reviewsSection, statsSection);
        wrapper.setPadding(new Insets(0));
        wrapper.setSpacing(16);

        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0f0f0f; -fx-border-color: transparent;");

        mainPanel.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return mainPanel;
    }

    private static void refreshTransactionList(ListView<String> list, Label detailsContent) {
        list.getItems().clear();
        List<Map<String, Object>> transactions = TransactionDAO.getAllTransactions();

        if (transactions.isEmpty()) {
            list.getItems().add("No transactions yet");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (Map<String, Object> trans : transactions) {
                int id = (Integer) trans.get("id");
                String filmName = (String) trans.get("film_name");
                int total = (Integer) trans.get("total");
                String paymentMethod = (String) trans.get("payment_method");
                java.sql.Timestamp ts = (java.sql.Timestamp) trans.get("created_at");
                String date = sdf.format(ts);

                String item = String.format("ID #%d - %s | Rp %,d | %s | %s",
                        id, filmName, total, paymentMethod, date);
                list.getItems().add(item);
            }
        }
    }

    private static void showTransactionDetails(int transactionId, Label detailsContent) {
        List<Map<String, Object>> transactions = TransactionDAO.getAllTransactions();
        for (Map<String, Object> trans : transactions) {
            if ((Integer) trans.get("id") == transactionId) {
                int id = (Integer) trans.get("id");
                String filmName = (String) trans.get("film_name");
                int ticketQty = (Integer) trans.get("ticket_qty");
                String snackDetail = (String) trans.get("snack_detail");
                int total = (Integer) trans.get("total");
                String paymentMethod = (String) trans.get("payment_method");
                java.sql.Timestamp ts = (java.sql.Timestamp) trans.get("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String date = sdf.format(ts);

                String details = String.format(
                        "Transaction ID: #%d\n" +
                        "Film: %s\n" +
                        "Tickets: %d\n" +
                        "Snacks: %s\n" +
                        "Payment Method: %s\n" +
                        "Total: Rp %,d\n" +
                        "Date: %s",
                        id, filmName, ticketQty, snackDetail, paymentMethod, total, date);
                detailsContent.setText(details);
                return;
            }
        }
        detailsContent.setText("Transaction not found");
    }

    private static void refreshReviewsList(ListView<String> list, TextArea detailsContent) {
        list.getItems().clear();
        List<Review> reviews = ReviewDAO.getAllReviews();

        if (reviews.isEmpty()) {
            list.getItems().add("No reviews yet");
        } else {
            for (Review review : reviews) {
                int id = review.getId();
                String filmName = review.getFilmName();
                int rating = review.getRating();
                String stars = "⭐".repeat(rating) + "☆".repeat(5 - rating);

                String item = String.format("ID #%d - %s | %s (%d/5)", id, filmName, stars, rating);
                list.getItems().add(item);
            }
        }
    }

    private static void showReviewDetails(int reviewId, TextArea detailsContent) {
        List<Review> reviews = ReviewDAO.getAllReviews();
        for (Review review : reviews) {
            if (review.getId() == reviewId) {
                String stars = "⭐".repeat(review.getRating()) + "☆".repeat(5 - review.getRating());
                String details = String.format(
                        "Review ID: #%d\n" +
                        "Film: %s\n" +
                        "Rating: %s (%d/5)\n" +
                        "Date: %s\n\n" +
                        "Comment:\n%s",
                        review.getId(), review.getFilmName(), stars, review.getRating(),
                        review.getCreatedAt(), review.getComment());
                detailsContent.setText(details);
                return;
            }
        }
        detailsContent.setText("Review not found");
    }

    private static void updateStats(Label transCount, Label reviewCount, Label totalSpent) {
        List<Map<String, Object>> transactions = TransactionDAO.getAllTransactions();
        List<Review> reviews = ReviewDAO.getAllReviews();

        transCount.setText("📋 Total Purchases: " + transactions.size());
        reviewCount.setText("⭐ Total Reviews: " + reviews.size());

        int total = 0;
        for (Map<String, Object> trans : transactions) {
            total += (Integer) trans.get("total");
        }
        totalSpent.setText("💰 Total Spent: Rp " + String.format("%,d", total));
    }

    private static VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(16));
        section.setStyle("-fx-border-color: #333333; -fx-border-radius: 8; -fx-background-color: #1a1a1a;");

        if (!title.isEmpty()) {
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #e50914;");
            section.getChildren().add(titleLabel);
        }

        return section;
    }
}
