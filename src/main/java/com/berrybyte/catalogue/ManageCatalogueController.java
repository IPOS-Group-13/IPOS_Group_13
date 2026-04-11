package com.berrybyte.catalogue;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class ManageCatalogueController {

    @FXML
    private AnchorPane catalogueMenuPane;

    @FXML
    public void initialize() {
        catalogueMenuPane.setVisible(true);
        catalogueMenuPane.setManaged(true);
    }

    @FXML
    private void handleProfileClick() {
        boolean isVisible = catalogueMenuPane.isVisible();
        catalogueMenuPane.setVisible(!isVisible);
        catalogueMenuPane.setManaged(!isVisible);
    }


    @FXML
    private void handleMerchantsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToMerchantMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void setQuantityButton(ActionEvent event) {
        System.out.println("Add Product clicked");
    }

    @FXML
    private void addProductButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/addNewProduct.fxml", "Add New Product Page");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void deleteProductButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/deleteProduct.fxml", "Delete Product Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modifyQuantityButton(ActionEvent event) {
        System.out.println("Add Product clicked");
    }
    @FXML
    private void handleCatalogueClick (ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/catalogue.fxml", "Catalogue Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handleStaffAccountsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToManageAccounts(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboardClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToDashboard(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
