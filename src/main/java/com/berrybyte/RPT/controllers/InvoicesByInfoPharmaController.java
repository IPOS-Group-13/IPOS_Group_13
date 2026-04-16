package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.model.InvoiceListingReport;
import com.berrybyte.RPT.model.InvoiceListingRow;
import com.berrybyte.RPT.export.InvoiceListingPdfService;
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

public class InvoicesByInfoPharmaController {

    private final ReportService reportService = new ReportServiceImpl(new ReportRepositoryImpl());

    private LocalDate afterDate;
    private LocalDate beforeDate;
    private InvoiceListingReport currentReport;
    private final InvoiceListingPdfService invoiceListingPdfService = new InvoiceListingPdfService();
    private static final String DEMO_RECIPIENT_EMAIL = "ipos_commercial@yahoo.com";

    @FXML
    private Label filterSummaryLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TableView<InvoiceListingRow> invoiceTable;

    @FXML
    private TableColumn<InvoiceListingRow, Integer> invoiceIdColumn;

    @FXML
    private TableColumn<InvoiceListingRow, Integer> orderIdColumn;

    @FXML
    private TableColumn<InvoiceListingRow, Integer> merchantIdColumn;

    @FXML
    private TableColumn<InvoiceListingRow, LocalDate> invoiceDateColumn;

    @FXML
    private TableColumn<InvoiceListingRow, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<InvoiceListingRow, Number> totalAmountColumn;

    @FXML
    private TableColumn<InvoiceListingRow, Number> paidAmountColumn;

    @FXML
    private TableColumn<InvoiceListingRow, String> paymentStatusColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button exportPdfButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        merchantIdColumn.setCellValueFactory(new PropertyValueFactory<>("merchantId"));
        invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        paidAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        updateFilterSummary();
    }

    public void setFilters(LocalDate afterDate, LocalDate beforeDate) {
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

            Path pdfPath = invoiceListingPdfService.generateInvoiceListingPdf(currentReport);
            messageLabel.setText("PDF saved to: " + pdfPath.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to export PDF.");
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
            LocalDate startDate = afterDate != null ? afterDate : LocalDate.of(2000, 1, 1);
            LocalDate endDate = beforeDate != null ? beforeDate : LocalDate.now();

            currentReport = reportService.generateInvoiceListing(null, startDate, endDate);
            invoiceTable.setItems(FXCollections.observableArrayList(currentReport.getRows()));
            messageLabel.setText("Loaded " + currentReport.getRows().size() + " invoice(s).");
        } catch (Exception e) {
            e.printStackTrace();
            invoiceTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("Unable to load invoice report.");
        }
    }

    private void updateFilterSummary() {
        String afterText = afterDate == null ? "Any" : afterDate.toString();
        String beforeText = beforeDate == null ? "Any" : beforeDate.toString();

        filterSummaryLabel.setText("After: " + afterText + " | Before: " + beforeText);
    }
}