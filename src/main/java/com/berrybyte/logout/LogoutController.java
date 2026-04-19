package com.berrybyte.logout;

import com.berrybyte.common.LoginSession;
import com.berrybyte.common.RoleBasedNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Represents logout controller.
 */
public class LogoutController {

/**
 * Handles yes logout.
 *
 * @param event event
 */
    @FXML
    private void handleYesLogout(ActionEvent event) {
        try {
            LoginSession.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/login.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setMaximized(false);
            stage.setMinWidth(0);
            stage.setMinHeight(0);
            stage.setScene(loginScene);
            stage.setTitle("BerryByte");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles no stay.
 *
 * @param event event
 */
    @FXML
    private void handleNoStay(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToDashboard(event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
