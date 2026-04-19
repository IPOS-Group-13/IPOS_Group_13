package com.berrybyte.catalogue;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Represents edit product details controller.
 */
public class EditProductDetailsController {

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
            messageLabel.setText("Unable to open logout page.");
        }
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
            messageLabel.setText("Unable to go back.");
        }
    }

/**
 * Handles edit button.
 *
 * @param event event
 */
    @FXML
    private void handleEditButton(ActionEvent event) {
        try {
            CatalogueItemRow selectedItem = catalogueItemsTable.getSelectionModel().getSelectedItem();

            if (selectedItem == null) {
                messageLabel.setText("Please select an item to edit.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/catalogue/updateProduct.fxml"));
            Parent root = loader.load();

            UpdateProductController controller = loader.getController();
            controller.setProduct(selectedItem);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle("Update Product");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open edit form.");
        }
    }
/**
 * Loads items.
 *
 * @param searchText search text
 */

    private void loadItems(String searchText) {
        try {
            catalogueItemsTable.setItems(
                    FXCollections.observableArrayList(
                            catalogueService.searchCatalogueItems(searchText)
                    )
            );
            messageLabel.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load catalogue items.");
        }
    }
}
