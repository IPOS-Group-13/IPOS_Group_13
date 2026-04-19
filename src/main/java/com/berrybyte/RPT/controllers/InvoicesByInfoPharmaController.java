package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.model.InvoiceListingReport;
import com.berrybyte.RPT.model.InvoiceListingRow;
import com.berrybyte.RPT.export.InvoiceListingPdfService;
import com.berrybyte.RPT.repository.ReportRepositoryImpl;
import com.berrybyte.RPT.services.ReportService;
import com.berrybyte.RPT.services.ReportServiceImpl;
import com.berrybyte.common.SceneSwitcher;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.nio.file.Path;

import java.time.LocalDate;

/**
 * Represents invoices by info pharma controller.
 */
public class InvoicesByInfoPharmaController extends ReportProfileMenuController {

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

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        initializeProfileMenu();
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        merchantIdColumn.setCellValueFactory(new PropertyValueFactory<>("merchantId"));
        invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        paidAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        bindColumnWidths();

        updateFilterSummary();
    }
/**
 * Performs set filters.
 *
 * @param afterDate after date
 * @param beforeDate before date
 */

    public void setFilters(LocalDate afterDate, LocalDate beforeDate) {
        this.afterDate = afterDate;
        this.beforeDate = beforeDate;
        updateFilterSummary();
        loadReport();
    }

/**
 * Handles refresh.
 *
 */
    @FXML
    private void handleRefresh() {
        loadReport();
    }

/**
 * Handles export pdf.
 *
 */
    @FXML
    private void handleExportPdf() {
        try {
            if (currentReport == null) {
                messageLabel.setText("No report loaded.");
                return;
            }

            Path pdfPath = invoiceListingPdfService.generateInvoiceListingPdf(currentReport);
            if (pdfPath == null || !java.nio.file.Files.exists(pdfPath)) {
                messageLabel.setText("PDF export failed.");
                return;
            }
            invoiceListingPdfService.openInvoiceListingPdf(pdfPath);
            messageLabel.setText("PDF opened: " + pdfPath.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to export PDF.");
        }
    }

/**
 * Handles back.
 *
 * @param event event
 */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/RPT/reportsMenu.fxml", "Reports");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }
/**
 * Loads report.
 *
 */

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
/**
 * Executes the update filter summary workflow.
 * This method coordinates the main operation for this action.
 *
 */

    private void updateFilterSummary() {
        String afterText = afterDate == null ? "Any" : afterDate.toString();
        String beforeText = beforeDate == null ? "Any" : beforeDate.toString();

        filterSummaryLabel.setText("After: " + afterText + " | Before: " + beforeText);
    }
/**
 * Performs bind column widths.
 *
 */

    private void bindColumnWidths() {
        invoiceIdColumn.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.10));
        orderIdColumn.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.09));
        merchantIdColumn.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.18));
        invoiceDateColumn.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.12));
        dueDateColumn.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.12));
        totalAmountColumn.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.13));
        paidAmountColumn.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.13));
        paymentStatusColumn.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.13));
    }
}
