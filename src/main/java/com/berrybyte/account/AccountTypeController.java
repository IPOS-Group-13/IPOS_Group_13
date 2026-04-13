package com.berrybyte.account;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class AccountTypeController {

    @FXML
    private Button adminButton;

    @FXML
    private Button managerButton;

    @FXML
    private Button merchantButton;

    @FXML
    private Button staffButton;

    @FXML
    private void handleAdminButton(ActionEvent event) throws Exception {
        SceneSwitcher.switchScene(event, "/account/createAdminAccount.fxml", "Create Admin Account");
    }

    @FXML
    private void handleManagerButton(ActionEvent event) throws Exception {
        SceneSwitcher.switchScene(event, "/account/createManagerAccount.fxml", "Create Manager Account");
    }

    @FXML
    private void handleStaffButton(ActionEvent event) throws Exception {
        SceneSwitcher.switchScene(event, "/account/createStaffAccount.fxml", "Create Staff Account");
    }

    @FXML
    private void handleMerchantButton(ActionEvent event) throws Exception {
        SceneSwitcher.switchScene(event, "/account/createMerchantAccount.fxml", "Create Merchant Account");
    }


    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            RoleBasedNavigator.openManageAccounts((Node) event.getSource());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
