package com.berrybyte.dashboard;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 * Represents manager dashboard controller.
 */
public class ManagerDashboardController {

    @FXML
    private AnchorPane profileMenuPane;

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        profileMenuPane.setVisible(false);
        profileMenuPane.setManaged(false);
    }

/**
 * Handles profile click.
 *
 */
    @FXML
    private void handleProfileClick() {
        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
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
 * Handles catalogue click.
 *
 * @param event event
 */
    @FXML
    private void handleCatalogueClick(ActionEvent event) {
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
 * Handles payments click.
 *
 * @param event event
 */
    @FXML
    private void handlePaymentsClick(ActionEvent event) {
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
    private void handleReportsClick(ActionEvent event) {
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
        profileMenuPane.setVisible(false);
        profileMenuPane.setManaged(false);

        try {
            SceneSwitcher.switchScene(event, "/logout/logout.fxml", "Log Out");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
