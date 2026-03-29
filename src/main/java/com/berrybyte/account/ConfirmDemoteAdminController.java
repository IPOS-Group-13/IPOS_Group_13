package com.berrybyte.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ConfirmDemoteAdminController {

    private EditAdminAccountController parentController;

    public void setParentController(EditAdminAccountController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleDemote(ActionEvent event) {
        if (parentController != null) {
            parentController.confirmDemotion();
        }
        closePopup(event);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closePopup(event);
    }

    private void closePopup(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}