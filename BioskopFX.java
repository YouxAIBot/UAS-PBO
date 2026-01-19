package projekPBO.projek;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import projekPBO.projek.dao.TransactionDAO;
import projekPBO.projek.db.DBConnection;
import projekPBO.projek.util.InvoicePDF;



public class BioskopFX extends Application {

    List<Film> filmList = new ArrayList<>();
    TextArea historyArea = new TextArea();
    
    // Store last transaction data for invoice download
    private static class LastTransaction {
        String film;
        int qty;
        String snacks;
        int total;
        String payment;
        int uang;
        String transactionId;
        
        LastTransaction(String film, int qty, String snacks, int total, String payment, int uang, String transactionId) {
            this.film = film;
            this.qty = qty;
            this.snacks = snacks;
            this.total = total;
            this.payment = payment;
            this.uang = uang;
            this.transactionId = transactionId;
        }
    }
    
    private LastTransaction lastTransaction = null;
    private static int lastTransactionId = 0;

    // Static getter for ReviewPanel to access last transaction ID
    public static int getLastTransactionId() {
        return lastTransactionId;
    }

    @Override
    public void start(Stage stage) {

        // Run DB diagnostics and surface quick feedback
        try {
            DBConnection.logDatabaseDiagnostics();
            java.sql.Connection testConn = DBConnection.getConnection();
            if (testConn == null) {
                historyArea.appendText("❌ Database connection failed: returned null.\n");
                Alert a = new Alert(Alert.AlertType.ERROR, "Cannot connect to PostgreSQL at jdbc:postgresql://localhost:5432/bioskop_db\nPlease ensure the DB is running and credentials in DBConnection.java are correct.", ButtonType.OK);
                a.setHeaderText("Database Connection Error");
                a.showAndWait();
            } else {
                try { testConn.close(); } catch (Exception ignore) {}
                historyArea.appendText("✅ Database connection OK (diagnostics logged).\n");
            }
        } catch (Exception ex) {
            historyArea.appendText("❌ Exception while checking DB: " + ex.getMessage() + "\n");
        }

        // DATA FILM
        filmList.add(new Film("Avengers", 50000, 20));
        filmList.add(new Film("Interstellar", 60000, 15));
        filmList.add(new Film("Spiderman", 55000, 10));

        // ========== TOP NAVBAR ==========
        Label navTitle = new Label("CINEPLEX");
        navTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #e50914;");
        Label navSubtitle = new Label("Online Booking");
        navSubtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #b3b3b3;");
        
        VBox navBrand = new VBox(0, navTitle, navSubtitle);
        navBrand.setPadding(new Insets(12, 20, 12, 20));
        
        HBox navbar = new HBox();
        navbar.setStyle("-fx-background-color: #000000; -fx-border-color: #222222; -fx-border-width: 0 0 1 0;");
        navbar.getChildren().add(navBrand);
        HBox.setHgrow(navBrand, Priority.ALWAYS);

        // ========== MAIN CONTENT AREA ==========
        
        // Film Selection Card
        VBox filmSection = createSection("SELECT YOUR MOVIE");
        ComboBox<Film> cbFilm = new ComboBox<>();
        cbFilm.getItems().addAll(filmList);
        cbFilm.setPromptText("Choose film...");
        cbFilm.getStyleClass().add("combo-box");
        cbFilm.setPrefHeight(40);
        cbFilm.setMaxWidth(Double.MAX_VALUE);
        filmSection.getChildren().addAll(cbFilm);

        // Ticket Quantity Card
        VBox ticketSection = createSection("NUMBER OF TICKETS");
        javafx.scene.control.Spinner<Integer> spTiket = new javafx.scene.control.Spinner<>(1, 20, 1);
        spTiket.setEditable(true);
        spTiket.setPrefHeight(40);
        spTiket.setMaxWidth(Double.MAX_VALUE);
        ticketSection.getChildren().addAll(spTiket);

        // Snacks Card
        VBox snackSection = createSection("ADD SNACKS");
        
        CheckBox cbPopcorn = new CheckBox("Popcorn - Rp 20.000");
        cbPopcorn.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 13px;");
        javafx.scene.control.Spinner<Integer> spPopQty = new javafx.scene.control.Spinner<>(0, 10, 0);
        spPopQty.setEditable(true);
        spPopQty.setPrefHeight(36);
        spPopQty.setMaxWidth(Double.MAX_VALUE);
        
        HBox popBox = new HBox(10, cbPopcorn, spPopQty);
        popBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(spPopQty, Priority.ALWAYS);

        CheckBox cbCola = new CheckBox("Cola - Rp 10.000");
        cbCola.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 13px;");
        javafx.scene.control.Spinner<Integer> spColaQty = new javafx.scene.control.Spinner<>(0, 10, 0);
        spColaQty.setEditable(true);
        spColaQty.setPrefHeight(36);
        spColaQty.setMaxWidth(Double.MAX_VALUE);
        
        HBox colaBox = new HBox(10, cbCola, spColaQty);
        colaBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(spColaQty, Priority.ALWAYS);

        snackSection.getChildren().addAll(popBox, colaBox);

        // Payment Card
        VBox paymentSection = createSection("PAYMENT METHOD");
        ToggleGroup paymentGroup = new ToggleGroup();
        
        RadioButton rbCash = new RadioButton("Cash/Tunai");
        rbCash.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 13px;");
        rbCash.setToggleGroup(paymentGroup);
        
        RadioButton rbGopay = new RadioButton("GoPay");
        rbGopay.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 13px;");
        rbGopay.setToggleGroup(paymentGroup);
        
        RadioButton rbDana = new RadioButton("Dana");
        rbDana.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 13px;");
        rbDana.setToggleGroup(paymentGroup);
        
        RadioButton rbBank = new RadioButton("Bank Transfer");
        rbBank.setStyle("-fx-text-fill: #e5e5e5; -fx-font-size: 13px;");
        rbBank.setToggleGroup(paymentGroup);

        paymentSection.getChildren().addAll(rbCash, rbGopay, rbDana, rbBank);

        // Amount Card
        VBox amountSection = createSection("AMOUNT");
        TextField tfUang = new TextField();
        tfUang.setPromptText("Enter amount (Rp)");
        tfUang.setPrefHeight(40);
        tfUang.setMaxWidth(Double.MAX_VALUE);
        amountSection.getChildren().addAll(tfUang);

        // Button section
        ProgressIndicator pi = new ProgressIndicator();
        pi.setPrefSize(40, 40);
        pi.setVisible(false);
        javafx.beans.property.SimpleBooleanProperty processing = new javafx.beans.property.SimpleBooleanProperty(false);

        Button btnProses = new Button("💳 CHECKOUT");
        btnProses.setPrefHeight(50);
        btnProses.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        btnProses.setMaxWidth(Double.MAX_VALUE);

        HBox buttonBox = new HBox(10, btnProses, pi);
        buttonBox.setAlignment(Pos.CENTER);

        btnProses.disableProperty().bind(
                cbFilm.valueProperty().isNull()
                        .or(paymentGroup.selectedToggleProperty().isNull())
                        .or(processing)
        );

        // History Area
        historyArea.setEditable(false);
        historyArea.setPrefHeight(240);
        historyArea.setMaxWidth(Double.MAX_VALUE);
        historyArea.getStyleClass().add("text-area");

        Label historyLabel = new Label("TRANSACTION HISTORY");
        historyLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-padding: 12 0 8 0;");

        VBox historySection = createSection("");
        historySection.getChildren().addAll(historyLabel, historyArea);
        
        // Download Invoice Button
        Button btnInvoice = new Button("📥 Download Invoice PDF");
        btnInvoice.setPrefHeight(40);
        btnInvoice.setStyle("-fx-font-size: 12px; -fx-padding: 10px;");
        btnInvoice.setMaxWidth(Double.MAX_VALUE);
        btnInvoice.setOnAction(ev -> {
            if (lastTransaction != null) {
                InvoicePDF.generateInvoice(
                    lastTransaction.film,
                    lastTransaction.qty,
                    lastTransaction.snacks,
                    lastTransaction.total,
                    lastTransaction.payment,
                    lastTransaction.uang,
                    lastTransaction.transactionId
                );
                historyArea.appendText("✅ Invoice PDF downloaded: invoice.pdf\n");
            } else {
                historyArea.appendText("❌ No transaction to download. Please complete a booking first.\n");
            }
        });
        
        historySection.getChildren().add(btnInvoice);

        // Button action
        btnProses.setOnAction(e -> {
            processing.set(true);
            pi.setVisible(true);
            PauseTransition wait = new PauseTransition(Duration.millis(700));
            wait.setOnFinished(ev -> {
                try {
                    Film film = cbFilm.getValue();
                    if (film == null) {
                        historyArea.appendText("❌ Film not selected!\n");
                        return;
                    }

                    int qty = spTiket.getValue();
                    int total = film.getPrice() * qty;

                    StringBuilder snackDetail = new StringBuilder();
                    if (cbPopcorn.isSelected()) {
                        int popQty = spPopQty.getValue();
                        total += 20000 * popQty;
                        snackDetail.append("Popcorn x").append(popQty).append(", ");
                    }
                    if (cbCola.isSelected()) {
                        int colaQty = spColaQty.getValue();
                        total += 10000 * colaQty;
                        snackDetail.append("Cola x").append(colaQty);
                    }
                    if (snackDetail.length() == 0) {
                        snackDetail.append("None");
                    } else if (snackDetail.toString().endsWith(", ")) {
                        snackDetail.setLength(snackDetail.length() - 2);
                    }

                    if (paymentGroup.getSelectedToggle() == null) {
                        historyArea.appendText("❌ Select payment method!\n");
                        return;
                    }

                    String method = ((RadioButton) paymentGroup.getSelectedToggle()).getText();
                    int uang = Integer.parseInt(tfUang.getText().replaceAll("[^0-9]", ""));

                    PaymentMethod pm;
                    if (rbCash.isSelected()) {
                        pm = new CashPayment();
                    } else if (rbGopay.isSelected()) {
                        pm = new GopayPayment();
                    } else if (rbDana.isSelected()) {
                        pm = new DanaPayment();
                    } else {
                        pm = new BankPayment();
                    }

                    boolean paid = pm.pay(total, uang);

                    historyArea.appendText(String.format(
                        "🎬 %s\n📊 Total: Rp %,d\n💳 %s\n",
                        film.getName(), total, method
                    ));

                    if (paid) {
                        int change = 0;
                        if (pm instanceof CashPayment) {
                            change = uang - total;
                            historyArea.appendText("✅ Payment successful! Change: Rp " + (change >= 0 ? change : 0) + "\n");
                        } else {
                            historyArea.appendText("✅ Payment successful via digital!\n");
                        }

                        // Save transaction to database
                        int transactionId = TransactionDAO.insertTransaction(
                                film.getName(),
                                qty,
                                snackDetail.toString(),
                                total,
                                method
                        );

                        if (transactionId > 0) {
                            historyArea.appendText("📝 Transaction ID: " + transactionId + "\n");
                            historyArea.appendText("✅ Data saved to database!\n");
                            
                            // Store transaction data for invoice download and reviews
                            lastTransactionId = transactionId;
                            lastTransaction = new LastTransaction(
                                film.getName(),
                                qty,
                                snackDetail.toString(),
                                total,
                                method,
                                uang,
                                String.valueOf(transactionId)
                            );
                        } else {
                            historyArea.appendText("❌ Failed to save transaction to database\n");
                        }
                    } else {
                        historyArea.appendText("❌ Payment failed: incorrect amount!\n");
                    }

                    historyArea.appendText("─────────────────────\n");

                    FadeTransition ft = new FadeTransition(Duration.millis(400), historyArea);
                    ft.setFromValue(0.9);
                    ft.setToValue(1.0);
                    ft.play();

                } catch (NumberFormatException ex) {
                    historyArea.appendText("❌ Invalid input format!\n");
                } catch (NullPointerException ex) {
                    historyArea.appendText("❌ Incomplete data!\n");
                } catch (Exception ex) {
                    historyArea.appendText("❌ Error: " + ex.getMessage() + "\n");
                } finally {
                    processing.set(false);
                    pi.setVisible(false);
                }
            });
            wait.play();
        });

        // Scroll pane for content
        VBox scrollContent = new VBox(16);
        scrollContent.setPadding(new Insets(20));
        scrollContent.setStyle("-fx-background-color: #0f0f0f;");
        scrollContent.getChildren().addAll(
            filmSection, ticketSection, snackSection, paymentSection,
            amountSection, buttonBox, historySection
        );

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0f0f0f;");

        // ========== TAB PANE ==========
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: #e5e5e5;");

        // Booking Tab
        Tab bookingTab = new Tab("🎬 BOOKING", scrollPane);
        bookingTab.setClosable(false);

        // History Tab
        Tab historyTab = new Tab("📜 HISTORY", HistoryPanel.createHistoryPanel(historyArea));
        historyTab.setClosable(false);

        // Reviews Tab
        Tab reviewsTab = new Tab("⭐ REVIEWS", ReviewPanel.createReviewPanel("", historyArea));
        reviewsTab.setClosable(false);

        // Management Tab
        Tab managementTab = new Tab("📊 MANAGEMENT", ManagementPanel.createManagementPanel(historyArea));
        managementTab.setClosable(false);

        tabPane.getTabs().addAll(bookingTab, historyTab, reviewsTab, managementTab);

        // ========== MAIN LAYOUT ==========
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f0f;");
        root.setTop(navbar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("CINEPLEX - Online Booking System");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(16));
        section.setStyle("-fx-border-color: #333333; -fx-border-radius: 8; -fx-background-color: #1a1a1a;");

        if (!title.isEmpty()) {
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e50914;");
            section.getChildren().add(titleLabel);
        }

        return section;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
