package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.model.StockTurnoverReport;
import com.berrybyte.RPT.model.StockTurnoverRow;
import com.berrybyte.RPT.export.StockTurnoverPdfService;
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

public class StockTurnoverReportController extends ReportProfileMenuController {

    private final ReportService reportService = new ReportServiceImpl(new ReportRepositoryImpl());

    private LocalDate afterDate;
    private LocalDate beforeDate;
    private StockTurnoverReport currentReport;
    private final StockTurnoverPdfService stockTurnoverPdfService = new StockTurnoverPdfService();
    private static final String DEMO_RECIPIENT_EMAIL = "ipos_commercial@yahoo.com";

    @FXML
    private Label filterSummaryLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TableView<StockTurnoverRow> stockTurnoverTable;

    @FXML
    private TableColumn<StockTurnoverRow, Integer> itemIdColumn;

    @FXML
    private TableColumn<StockTurnoverRow, String> descriptionColumn;

    @FXML
    private TableColumn<StockTurnoverRow, Integer> quantitySoldColumn;

    @FXML
    private TableColumn<StockTurnoverRow, Number> salesValueColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button exportPdfButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        initializeProfileMenu();
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        quantitySoldColumn.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        salesValueColumn.setCellValueFactory(new PropertyValueFactory<>("salesValue"));
        bindColumnWidths();

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

            Path pdfPath = stockTurnoverPdfService.generateStockTurnoverPdf(currentReport);
            messageLabel.setText("PDF saved to: " + pdfPath.toAbsolutePath());
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
            LocalDate startDate = afterDate != null ? afterDate : LocalDate.of(2000, 1, 1);
            LocalDate endDate = beforeDate != null ? beforeDate : LocalDate.now();

            currentReport = reportService.generateStockTurnover(startDate, endDate);
            stockTurnoverTable.setItems(FXCollections.observableArrayList(currentReport.getRows()));
            messageLabel.setText("Loaded " + currentReport.getRows().size() + " stock turnover row(s).");
        } catch (Exception e) {
            e.printStackTrace();
            stockTurnoverTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("Unable to load stock turnover report.");
        }
    }

    private void updateFilterSummary() {
        String afterText = afterDate == null ? "Any" : afterDate.toString();
        String beforeText = beforeDate == null ? "Any" : beforeDate.toString();

        filterSummaryLabel.setText("After: " + afterText + " | Before: " + beforeText);
    }

    private void bindColumnWidths() {
        itemIdColumn.prefWidthProperty().bind(stockTurnoverTable.widthProperty().multiply(0.12));
        descriptionColumn.prefWidthProperty().bind(stockTurnoverTable.widthProperty().multiply(0.50));
        quantitySoldColumn.prefWidthProperty().bind(stockTurnoverTable.widthProperty().multiply(0.19));
        salesValueColumn.prefWidthProperty().bind(stockTurnoverTable.widthProperty().multiply(0.19));
    }
}
