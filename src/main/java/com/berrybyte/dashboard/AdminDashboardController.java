package com.berrybyte.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminDashboardController {

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
    private void handleLogoutMenuClick() {
        profileMenuPane.setVisible(false);
        profileMenuPane.setManaged(false);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/logout/logout.fxml"));
            Scene scene = new Scene(loader.load());

            Stage logoutStage = new Stage();
            logoutStage.setTitle("Log Out");
            logoutStage.setScene(scene);
            logoutStage.initModality(Modality.APPLICATION_MODAL);
            logoutStage.initOwner(profileMenuPane.getScene().getWindow());
            logoutStage.setResizable(false);
            logoutStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}