package com.berrybyte.dashboard;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 * Represents order menu controller.
 */
public class OrderMenuController {

    @FXML
    private AnchorPane profileMenuPane;

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
 * Executes the open incoming orders button workflow.
 *
 * @param event event
 */
    @FXML
    private void openIncomingOrdersButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/ORD/incomingOrders.fxml", "Incoming Orders");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Performs view summary button.
 *
 * @param event event
 */
    @FXML
    private void viewSummaryButton(ActionEvent event) {
        try{
            SceneSwitcher.switchScene(event, "/ORD/orderSummary.fxml", "Orders Summary");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

