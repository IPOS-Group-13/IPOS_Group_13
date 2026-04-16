package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.export.LowStockPdfService;
import com.berrybyte.RPT.model.LowStockItem;
import com.berrybyte.RPT.model.LowStockReport;
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

public class LowStockReportController {

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
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        stockLimitColumn.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));

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
            currentReport = reportService.generateLowStockReport();
            lowStockTable.setItems(FXCollections.observableArrayList(currentReport.getItems()));
            messageLabel.setText("Loaded " + currentReport.getItems().size() + " low stock item(s).");
        } catch (Exception e) {
            e.printStackTrace();
            lowStockTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("Unable to load low stock report.");
        }
    }
}