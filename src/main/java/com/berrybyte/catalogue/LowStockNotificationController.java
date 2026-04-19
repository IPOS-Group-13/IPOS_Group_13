package com.berrybyte.catalogue;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

/**
 * Represents low stock notification controller.
 */
public class LowStockNotificationController {

    @FXML private TableView<LowStockItemRow> lowStockTable;
    @FXML private TableColumn<LowStockItemRow, Integer> itemIdColumn;
    @FXML private TableColumn<LowStockItemRow, String> descriptionColumn;
    @FXML private TableColumn<LowStockItemRow, Integer> availabilityColumn;
    @FXML private TableColumn<LowStockItemRow, Integer> stockLimitColumn;

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        stockLimitColumn.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));
    }
/**
 * Sets low stock items.
 *
 * @param items items
 */

    public void setLowStockItems(List<LowStockItemRow> items) {
        lowStockTable.setItems(FXCollections.observableArrayList(items));
    }

/**
 * Handles ok.
 *
 * @param event event
 */
    @FXML
    private void handleOk(ActionEvent event) {
        Stage stage = (Stage) lowStockTable.getScene().getWindow();
        stage.close();
    }
}
