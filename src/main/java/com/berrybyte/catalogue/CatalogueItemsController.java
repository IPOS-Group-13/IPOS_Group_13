package com.berrybyte.catalogue;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * Represents catalogue items controller.
 */
public class CatalogueItemsController {
    @FXML
    private TextField searchField;

    @FXML
    private TableView<CatalogueItemRow> catalogueItemsTable;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> itemIdColumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> descriptionColumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> packageTypeColumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> unitColumn;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> unitsInPackColumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> packageCostColumn;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> availabilityPacksColumn;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> stockLimitPacksColumn;

    @FXML
    private Label messageLabel;

    @FXML
    private AnchorPane profileMenuPane;

    private final CatalogueService catalogueService = new CatalogueService();

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }

        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        packageTypeColumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitsInPackColumn.setCellValueFactory(new PropertyValueFactory<>("unitsInPack"));
        packageCostColumn.setCellValueFactory(new PropertyValueFactory<>("packageCost"));
        availabilityPacksColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        stockLimitPacksColumn.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));

        loadItems("");
    }

/**
 * Handles back button.
 *
 * @param event event
 */
    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles search.
 *
 * @param event event
 */
    @FXML
    private void handleSearch(ActionEvent event) {
        loadItems(searchField.getText());
    }

/**
 * Handles profile click.
 *
 */
    @FXML
    private void handleProfileClick() {
        if (profileMenuPane == null) {
            return;
        }

        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
        }
    }

/**
 * Handles logout menu click.
 *
 * @param event event
 */
    @FXML
    private void handleLogoutMenuClick(ActionEvent event) {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }

        try {
            SceneSwitcher.switchScene(event, "/logout/logout.fxml", "Log Out");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
 * Loads items.
 *
 * @param searchText search text
 */

    private void loadItems(String searchText) {
        try {
            catalogueItemsTable.setItems(FXCollections.observableArrayList(catalogueService.searchCatalogueItems(searchText)));
            if (messageLabel != null) {
                messageLabel.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (messageLabel != null) {
                messageLabel.setText("Unable to load catalogue items.");
            }
        }
    }
}
