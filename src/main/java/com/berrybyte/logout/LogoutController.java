package com.berrybyte.logout;

import com.berrybyte.common.LoginSession;
import com.berrybyte.common.RoleBasedNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LogoutController {

    @FXML
    private void handleYesLogout(ActionEvent event) {
        try {
            LoginSession.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/login.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(loginScene);
            stage.setTitle("Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNoStay(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToDashboard(event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
