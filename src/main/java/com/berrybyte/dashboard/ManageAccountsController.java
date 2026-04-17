package com.berrybyte.dashboard;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class ManageAccountsController {

    @FXML
    private AnchorPane profileMenuPane;

    @FXML
    public void initialize() {
        profileMenuPane.setVisible(false);
        profileMenuPane.setManaged(false);
    }

    @FXML
    private void handleProfileClick() {
        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
        }
    }

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

    @FXML
    private void handleMerchantsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToMerchantMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createNewAccountButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/account/accountType.fxml", "Select Account Type");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteAccountButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/account/deleteAccount.fxml", "Delete Account");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editAccountsButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/account/editAccounts.fxml", "Edit Account Details");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCatalogueClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
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

    @FXML
    private void handleOrdersClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToOrderMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlePendingApplications(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/account/pendingapplications/pendingApplications.fxml", "Pending Applications Page");
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
    public void handleReportsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToReportsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

