package com.berrybyte.RPT.controllers;

import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public abstract class ReportProfileMenuController {

    @FXML
    protected AnchorPane profileMenuPane;

    protected void initializeProfileMenu() {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }
    }

    @FXML
    protected void handleProfileClick() {
        if (profileMenuPane == null) {
            return;
        }

        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
        }
    }

    @FXML
    protected void handleLogoutMenuClick(ActionEvent event) {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }

        try {
            SceneSwitcher.switchScene(event, "/logout/logout.fxml", "Log Out");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
