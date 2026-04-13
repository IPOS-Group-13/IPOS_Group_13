package com.berrybyte.catalogue;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class CatalogueController {

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
    private void handleCatalogueClick (ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/catalogue.fxml", "Catalogue Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void manageCatalogueButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/manageCatalogue.fxml", "Manage Catalogue Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOrdersClick(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/dashboard/orderMenu.fxml", "Orders");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewCatalogueButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/catalogueItems.fxml", "Catalogue Items Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleStaffAccountsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToStaffAccounts(event);
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
