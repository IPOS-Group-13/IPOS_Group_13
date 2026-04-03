package com.berrybyte.logout;

import com.berrybyte.common.LoginSession;
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
            String role = LoginSession.getCurrentRole();
            String dashboardPath = "/dashboard/adminDashboard.fxml";
            String title = "Admin Dashboard";

            if ("MANAGER".equalsIgnoreCase(role)) {
                dashboardPath = "/dashboard/managerDashboard.fxml";
                title = "Manager Dashboard";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
            Scene dashboardScene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}