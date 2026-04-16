package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.email.ReportEmailService;
import com.berrybyte.RPT.email.ReportEmailServiceImpl;
import com.berrybyte.RPT.model.MerchantOrderSummaryReport;
import com.berrybyte.RPT.model.MerchantOrderSummaryRow;
import com.berrybyte.RPT.export.MerchantOrderSummaryPdfService;
import com.berrybyte.RPT.repository.ReportRepositoryImpl;
import com.berrybyte.RPT.services.ReportService;
import com.berrybyte.RPT.services.ReportServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.nio.file.Path;

import java.time.LocalDate;

public class MerchantOrderSummaryReportController {

    private final ReportService reportService = new ReportServiceImpl(new ReportRepositoryImpl());

    private String merchantSearch;
    private LocalDate afterDate;
    private LocalDate beforeDate;
    private MerchantOrderSummaryReport currentReport;
    private final MerchantOrderSummaryPdfService merchantOrderSummaryPdfService = new MerchantOrderSummaryPdfService();
    private static final String DEMO_RECIPIENT_EMAIL = "ipos_commercial@yahoo.com";
    private final ReportEmailService reportEmailService = new ReportEmailServiceImpl();

    @FXML
    private Label filterSummaryLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TableView<MerchantOrderSummaryRow> merchantOrderTable;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, Integer> orderIdColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, LocalDate> orderDateColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, Number> totalAmountColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, String> dispatchColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, String> deliveredColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, String> statusColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, String> paymentStatusColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button exportPdfButton;

    @FXML
    private Button sendEmailButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        dispatchColumn.setCellValueFactory(cellData -> {
            String value = cellData.getValue().getDispatchDateTime() == null
                    ? ""
                    : cellData.getValue().getDispatchDateTime().toString();
            return new javafx.beans.property.SimpleStringProperty(value);
        });

        deliveredColumn.setCellValueFactory(cellData -> {
            String value = cellData.getValue().getDeliveredDateTime() == null
                    ? ""
                    : cellData.getValue().getDeliveredDateTime().toString();
            return new javafx.beans.property.SimpleStringProperty(value);
        });

        updateFilterSummary();
    }

    public void setFilters(String merchantSearch, LocalDate afterDate, LocalDate beforeDate) {
        this.merchantSearch = merchantSearch == null ? "" : merchantSearch.trim();
        this.afterDate = afterDate;
        this.beforeDate = beforeDate;
        updateFilterSummary();
        loadReport();
    }

    @FXML
    private void handleRefresh() {
        loadReport();
    }

    @FXML
    private void handleExportPdf() {
        try {
            if (currentReport == null) {
                messageLabel.setText("No report loaded.");
                return;
            }

            Path pdfPath = merchantOrderSummaryPdfService.generateMerchantOrderSummaryPdf(currentReport);
            messageLabel.setText("PDF saved to: " + pdfPath.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to export PDF.");
        }
    }

    @FXML
    private void handleSendEmail() {
        try {
            if (currentReport == null) {
                messageLabel.setText("No report loaded.");
                return;
            }

            Path pdfPath = merchantOrderSummaryPdfService.generateMerchantOrderSummaryPdf(currentReport);

            String subject = "Merchant Order Report";
            String body = "Please find attached the merchant order report generated by IPOS-SA for demo purposes.";

            reportEmailService.sendReportEmail(DEMO_RECIPIENT_EMAIL, subject, body, pdfPath);

            messageLabel.setText("Email sent to: " + DEMO_RECIPIENT_EMAIL);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to send email.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/RPT/reportsMenu.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Reports");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }

    private void loadReport() {
        try {
            Integer merchantId = parseMerchantId(merchantSearch);
            if (merchantId == null) {
                merchantOrderTable.setItems(FXCollections.observableArrayList());
                messageLabel.setText("Enter a numeric Merchant ID on the reports menu.");
                return;
            }

            LocalDate startDate = afterDate != null ? afterDate : LocalDate.of(2000, 1, 1);
            LocalDate endDate = beforeDate != null ? beforeDate : LocalDate.now();

            currentReport = reportService.generateMerchantOrderSummary(merchantId, startDate, endDate);
            merchantOrderTable.setItems(FXCollections.observableArrayList(currentReport.getRows()));
            messageLabel.setText("Loaded " + currentReport.getRows().size() + " order(s).");
        } catch (Exception e) {
            e.printStackTrace();
            merchantOrderTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("Unable to load merchant order report.");
        }
    }

    private void updateFilterSummary() {
        String merchantText = (merchantSearch == null || merchantSearch.isBlank()) ? "Merchant ID required" : merchantSearch;
        String afterText = afterDate == null ? "Any" : afterDate.toString();
        String beforeText = beforeDate == null ? "Any" : beforeDate.toString();

        filterSummaryLabel.setText("Merchant: " + merchantText + " | After: " + afterText + " | Before: " + beforeText);
    }

    private Integer parseMerchantId(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}