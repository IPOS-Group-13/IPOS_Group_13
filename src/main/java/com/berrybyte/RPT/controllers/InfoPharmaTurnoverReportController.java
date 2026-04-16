package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.model.InfoPharmaTurnoverReport;
import com.berrybyte.RPT.export.InfoPharmaTurnoverPdfService;
import com.berrybyte.RPT.repository.ReportRepositoryImpl;
import com.berrybyte.RPT.services.ReportService;
import com.berrybyte.RPT.services.ReportServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.nio.file.Path;

import java.time.LocalDate;

public class InfoPharmaTurnoverReportController {

    private final ReportService reportService = new ReportServiceImpl(new ReportRepositoryImpl());

    private LocalDate afterDate;
    private LocalDate beforeDate;
    private InfoPharmaTurnoverReport currentReport;
    private final InfoPharmaTurnoverPdfService infoPharmaTurnoverPdfService = new InfoPharmaTurnoverPdfService();
    private static final String DEMO_RECIPIENT_EMAIL = "ipos_commercial@yahoo.com";

    @FXML
    private Label filterSummaryLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TextArea turnoverSummaryTextArea;

    @FXML
    private Button refreshButton;

    @FXML
    private Button exportPdfButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
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

            Path pdfPath = infoPharmaTurnoverPdfService.generateInfoPharmaTurnoverPdf(currentReport);
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

            currentReport = reportService.generateInfoPharmaTurnoverReport(startDate, endDate);

            StringBuilder summary = new StringBuilder();
            summary.append("InfoPharma Internal Turnover Summary\n\n");

            summary.append("Period: ")
                    .append(currentReport.getStartDate())
                    .append(" to ")
                    .append(currentReport.getEndDate())
                    .append("\n");

            summary.append("Generated: ")
                    .append(currentReport.getGeneratedAt())
                    .append("\n\n");

            summary.append("Overview\n");
            summary.append("- Total invoices raised: ")
                    .append(currentReport.getTotalInvoices())
                    .append("\n");
            summary.append("- Total invoiced amount: ")
                    .append(currentReport.getTotalInvoicedAmount())
                    .append("\n");
            summary.append("- Total paid amount: ")
                    .append(currentReport.getTotalPaidAmount())
                    .append("\n");
            summary.append("- Total outstanding amount: ")
                    .append(currentReport.getTotalOutstandingAmount())
                    .append("\n\n");

            summary.append("Payment Status Breakdown\n");
            summary.append("- Paid invoices: ")
                    .append(currentReport.getPaidInvoiceCount())
                    .append("\n");
            summary.append("- Unpaid invoices: ")
                    .append(currentReport.getUnpaidInvoiceCount())
                    .append("\n");
            summary.append("- Partial invoices: ")
                    .append(currentReport.getPartialInvoiceCount())
                    .append("\n\n");

            summary.append("Notes\n");
            summary.append("- This report summarises invoice turnover for the selected period.\n");
            summary.append("- Amounts are based on the Invoices table.\n");

            turnoverSummaryTextArea.setText(summary.toString());
            messageLabel.setText("Loaded internal turnover summary.");
        } catch (Exception e) {
            e.printStackTrace();
            turnoverSummaryTextArea.clear();
            messageLabel.setText("Unable to load internal turnover summary.");
        }
    }

    private void updateFilterSummary() {
        String afterText = afterDate == null ? "Any" : afterDate.toString();
        String beforeText = beforeDate == null ? "Any" : beforeDate.toString();

        filterSummaryLabel.setText("After: " + afterText + " | Before: " + beforeText);
    }
}