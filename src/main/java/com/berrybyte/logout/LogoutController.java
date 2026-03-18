package com.berrybyte.logout;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/login.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage logoutStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage mainStage = (Stage) logoutStage.getOwner();

            mainStage.setScene(loginScene);
            mainStage.setTitle("Login");
            mainStage.show();

            logoutStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNoStay(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/adminDashboard.fxml"));
            Scene adminScene = new Scene(loader.load());

            Stage logoutStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage mainStage = (Stage) logoutStage.getOwner();

            mainStage.setScene(adminScene);
            mainStage.setTitle("Admin Dashboard");
            mainStage.show();

            logoutStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}