package com.berrybyte.catalogue;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class LowStockNotificationController {

    @FXML private TableView<LowStockItemRow> lowStockTable;
    @FXML private TableColumn<LowStockItemRow, Integer> itemIdColumn;
    @FXML private TableColumn<LowStockItemRow, String> descriptionColumn;
    @FXML private TableColumn<LowStockItemRow, Integer> availabilityColumn;
    @FXML private TableColumn<LowStockItemRow, Integer> stockLimitColumn;

    @FXML
    public void initialize() {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        stockLimitColumn.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));
    }

    public void setLowStockItems(List<LowStockItemRow> items) {
        lowStockTable.setItems(FXCollections.observableArrayList(items));
    }

    @FXML
    private void handleOk(ActionEvent event) {
        Stage stage = (Stage) lowStockTable.getScene().getWindow();
        stage.close();
    }
}
