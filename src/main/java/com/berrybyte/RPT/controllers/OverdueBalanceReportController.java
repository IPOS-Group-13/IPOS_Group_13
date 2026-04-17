package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.export.OverdueBalancePdfService;
import com.berrybyte.RPT.model.OverdueBalanceReport;
import com.berrybyte.RPT.model.OverdueBalanceRow;
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

import java.nio.file.Files;
import java.nio.file.Path;

public class OverdueBalanceReportController extends ReportProfileMenuController {

    private final ReportService reportService = new ReportServiceImpl(new ReportRepositoryImpl());
    private final OverdueBalancePdfService overdueBalancePdfService = new OverdueBalancePdfService();

    private Integer merchantId;
    private String merchantName;
    private OverdueBalanceReport currentReport;

    @FXML
    private Label filterSummaryLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TableView<OverdueBalanceRow> overdueTable;

    @FXML
    private TableColumn<OverdueBalanceRow, Integer> merchantIdColumn;

    @FXML
    private TableColumn<OverdueBalanceRow, String> companyNameColumn;

    @FXML
    private TableColumn<OverdueBalanceRow, String> accountNumberColumn;

    @FXML
    private TableColumn<OverdueBalanceRow, String> accountStatusColumn;

    @FXML
    private TableColumn<OverdueBalanceRow, java.time.LocalDate> oldestDueDateColumn;

    @FXML
    private TableColumn<OverdueBalanceRow, java.math.BigDecimal> overdueAmountColumn;

    @FXML
    private TableColumn<OverdueBalanceRow, Integer> overdueInvoiceCountColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button exportPdfButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        initializeProfileMenu();
        merchantIdColumn.setCellValueFactory(new PropertyValueFactory<>("merchantId"));
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("iposAccountNumber"));
        accountStatusColumn.setCellValueFactory(new PropertyValueFactory<>("accountStatus"));
        oldestDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("oldestDueDate"));
        overdueAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalOverdueAmount"));
        overdueInvoiceCountColumn.setCellValueFactory(new PropertyValueFactory<>("overdueInvoiceCount"));
        bindColumnWidths();

        updateFilterSummary();
    }

    public void setFilters(Integer merchantId, String merchantName) {
        this.merchantId = merchantId;
        this.merchantName = merchantName == null ? "" : merchantName.trim();
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

            Path pdfPath = overdueBalancePdfService.generateOverdueBalancePdf(currentReport);

            if (pdfPath == null || !Files.exists(pdfPath)) {
                messageLabel.setText("PDF export failed.");
                return;
            }

            overdueBalancePdfService.openOverdueBalancePdf(pdfPath);
            messageLabel.setText("PDF opened: " + pdfPath.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to export PDF.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/RPT/reportsMenu.fxml", "Reports");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }

    private void loadReport() {
        try {
            currentReport = reportService.generateOverdueBalanceReport(merchantId, merchantName);
            overdueTable.setItems(FXCollections.observableArrayList(currentReport.getRows()));
            messageLabel.setText("Loaded " + currentReport.getRows().size() + " overdue account(s).");
        } catch (Exception e) {
            e.printStackTrace();
            overdueTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("Unable to load overdue balance report.");
        }
    }

    private void updateFilterSummary() {
        String merchantText = (merchantName == null || merchantName.isBlank()) ? "All overdue accounts" : merchantName;
        filterSummaryLabel.setText("Scope: " + merchantText);
    }

    private void bindColumnWidths() {
        merchantIdColumn.prefWidthProperty().bind(overdueTable.widthProperty().multiply(0.11));
        companyNameColumn.prefWidthProperty().bind(overdueTable.widthProperty().multiply(0.26));
        accountNumberColumn.prefWidthProperty().bind(overdueTable.widthProperty().multiply(0.17));
        accountStatusColumn.prefWidthProperty().bind(overdueTable.widthProperty().multiply(0.12));
        oldestDueDateColumn.prefWidthProperty().bind(overdueTable.widthProperty().multiply(0.12));
        overdueAmountColumn.prefWidthProperty().bind(overdueTable.widthProperty().multiply(0.14));
        overdueInvoiceCountColumn.prefWidthProperty().bind(overdueTable.widthProperty().multiply(0.08));
    }
}
