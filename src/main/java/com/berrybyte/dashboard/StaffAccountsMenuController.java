package com.berrybyte.dashboard;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class StaffAccountsMenuController {

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
    private void handleDashboardClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToDashboard(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
