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
    private AnchorPane profileMenuPane;

    @FXML
    public void initialize() {
        if (catalogueMenuPane != null) {
            catalogueMenuPane.setVisible(true);
            catalogueMenuPane.setManaged(true);
        }
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }
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
        }
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
    private void handleBackButton(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void modifyQuantityButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/editProductDetails.fxml", "Edit Product Details Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCatalogueClick (ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOrdersClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToOrderMenu(event);
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

    public void handlePaymentsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToPaymentsMenu(event);
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
