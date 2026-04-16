package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.export.LowStockPdfService;
import com.berrybyte.RPT.model.LowStockItem;
import com.berrybyte.RPT.model.LowStockReport;
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

public class LowStockReportController extends ReportProfileMenuController {

    private final ReportService reportService = new ReportServiceImpl(new ReportRepositoryImpl());
    private final LowStockPdfService lowStockPdfService = new LowStockPdfService();

    private LowStockReport currentReport;
    private static final String DEMO_RECIPIENT_EMAIL = "ipos_commercial@yahoo.com";

    @FXML
    private Label messageLabel;

    @FXML
    private TableView<LowStockItem> lowStockTable;

    @FXML
    private TableColumn<LowStockItem, Integer> itemIdColumn;

    @FXML
    private TableColumn<LowStockItem, String> descriptionColumn;

    @FXML
    private TableColumn<LowStockItem, Integer> availabilityColumn;

    @FXML
    private TableColumn<LowStockItem, Integer> stockLimitColumn;

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
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        stockLimitColumn.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));
        bindColumnWidths();

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

            Path pdfPath = lowStockPdfService.generateLowStockPdf(currentReport);
            if (pdfPath == null || !java.nio.file.Files.exists(pdfPath)) {
                messageLabel.setText("PDF export failed.");
                return;
            }
            lowStockPdfService.openLowStockPdf(pdfPath);
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
            currentReport = reportService.generateLowStockReport();
            lowStockTable.setItems(FXCollections.observableArrayList(currentReport.getItems()));
            messageLabel.setText("Loaded " + currentReport.getItems().size() + " low stock item(s).");
        } catch (Exception e) {
            e.printStackTrace();
            lowStockTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("Unable to load low stock report.");
        }
    }

    private void bindColumnWidths() {
        itemIdColumn.prefWidthProperty().bind(lowStockTable.widthProperty().multiply(0.16));
        descriptionColumn.prefWidthProperty().bind(lowStockTable.widthProperty().multiply(0.44));
        availabilityColumn.prefWidthProperty().bind(lowStockTable.widthProperty().multiply(0.22));
        stockLimitColumn.prefWidthProperty().bind(lowStockTable.widthProperty().multiply(0.18));
    }
}
