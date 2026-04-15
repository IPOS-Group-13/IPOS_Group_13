package com.berrybyte.catalogue;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EditProductDetailsController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<CatalogueItemRow> merchantsTable;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> nameColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> companyColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> iposIdColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> creditLimitColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> discountPlanColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, Double> discountPlanColoumn1;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> discountPlanColoumn2;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> discountPlanColoumn3;

    @FXML
    private Label messageLabel;

    @FXML
    private AnchorPane profileMenuPane;

    private final CatalogueService catalogueService = new CatalogueService();

    @FXML
    public void initialize() {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }

        nameColoumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        companyColoumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        iposIdColoumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        creditLimitColoumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        discountPlanColoumn.setCellValueFactory(new PropertyValueFactory<>("unitsInPack"));
        discountPlanColoumn1.setCellValueFactory(new PropertyValueFactory<>("packageCost"));
        discountPlanColoumn2.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        discountPlanColoumn3.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));

        loadItems("");
    }


    @FXML
    private void handleSearch(ActionEvent event) {
        loadItems(searchField.getText());
    }

    @FXML
    private void handleProfileClick() {
        if (profileMenuPane == null) {
            return;
        }

        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
    }

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


    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }

    // ✏️ EDIT PRODUCT
    @FXML
    private void handleEditButton(ActionEvent event) {
        try {
            CatalogueItemRow selectedItem = merchantsTable.getSelectionModel().getSelectedItem();

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


    private void loadItems(String searchText) {
        try {
            merchantsTable.setItems(
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
