package com.berrybyte.catalogue;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 * Represents manage catalogue controller.
 */
public class ManageCatalogueController {

    @FXML
    private AnchorPane catalogueMenuPane;

    @FXML
    private AnchorPane profileMenuPane;

/**
 * Initializes controller state and bindings.
 *
 */
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
 * Handles merchants click.
 *
 * @param event event
 */
    @FXML
    private void handleMerchantsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToMerchantMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Sets quantity button.
 *
 * @param event event
 */
    @FXML
    private void setQuantityButton(ActionEvent event) {
        System.out.println("Add Product clicked");
    }

/**
 * Performs add product button.
 *
 * @param event event
 */
    @FXML
    private void addProductButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/addNewProduct.fxml", "Add New Product Page");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

/**
 * Executes the delete product button workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 */
    @FXML
    private void deleteProductButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/deleteProduct.fxml", "Delete Product Page");
        } catch (Exception e) {
            e.printStackTrace();
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
        }
    }


/**
 * Performs modify quantity button.
 *
 * @param event event
 */
    @FXML
    private void modifyQuantityButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/editProductDetails.fxml", "Edit Product Details Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles catalogue click.
 *
 * @param event event
 */
    @FXML
    private void handleCatalogueClick (ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles orders click.
 *
 * @param event event
 */
    @FXML
    private void handleOrdersClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToOrderMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles staff accounts click.
 *
 * @param event event
 */
    @FXML
    private void handleStaffAccountsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToManageAccounts(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
 * Handles payments click.
 *
 * @param event event
 */

    public void handlePaymentsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToPaymentsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles reports click.
 *
 * @param event event
 */
    @FXML
    public void handleReportsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToReportsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles pending applications.
 *
 * @param event event
 */
    @FXML
    public void handlePendingApplications(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/account/pendingapplications/pendingApplications.fxml", "Pending Applications Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles dashboard click.
 *
 * @param event event
 */
    @FXML
    private void handleDashboardClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToDashboard(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

