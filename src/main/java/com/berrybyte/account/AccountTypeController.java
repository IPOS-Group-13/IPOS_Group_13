package com.berrybyte.account;

import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard/staffAccountsMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Accounts");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}