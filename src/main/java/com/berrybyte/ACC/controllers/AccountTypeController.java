package com.berrybyte.ACC.controllers;

import com.berrybyte.ACC.session.MerchantDraftSession;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * Represents account type controller.
 */
public class AccountTypeController {

    @FXML
    private Button adminButton;

    @FXML
    private Button managerButton;

    @FXML
    private Button merchantButton;

    @FXML
    private Button staffButton;

/**
 * Handles admin button.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws Exception when the operation fails
 */
    @FXML
    private void handleAdminButton(ActionEvent event) throws Exception {
        SceneSwitcher.switchScene(event, "/account/createAdminAccount.fxml", "Create Admin Account");
    }

/**
 * Handles manager button.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws Exception when the operation fails
 */
    @FXML
    private void handleManagerButton(ActionEvent event) throws Exception {
        SceneSwitcher.switchScene(event, "/account/createManagerAccount.fxml", "Create Manager Account");
    }

/**
 * Handles staff button.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws Exception when the operation fails
 */
    @FXML
    private void handleStaffButton(ActionEvent event) throws Exception {
        SceneSwitcher.switchScene(event, "/account/createStaffAccount.fxml", "Create Staff Account");
    }

/**
 * Handles merchant button.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws Exception when the operation fails
 */
    @FXML
    private void handleMerchantButton(ActionEvent event) throws Exception {
        MerchantDraftSession.setPreviousPage(null);
        SceneSwitcher.switchScene(event, "/account/createMerchantAccount.fxml", "Create Merchant Account");
    }

/**
 * Handles back button.
 *
 * @param event event
 */
    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            RoleBasedNavigator.openManageAccounts((Node) event.getSource());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
