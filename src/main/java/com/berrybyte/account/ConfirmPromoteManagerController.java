package com.berrybyte.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ConfirmPromoteManagerController {

    private EditManagerAccountController managerController;
    private EditStaffAccountController staffController;

    public void setParentController(EditManagerAccountController parentController) {
        this.managerController = parentController;
    }

    public void setStaffController(EditStaffAccountController parentController) {
        this.staffController = parentController;
    }

    @FXML
    private void handlePromote(ActionEvent event) {
        if (managerController != null) {
            managerController.confirmPromotion();
        } else if (staffController != null) {
            staffController.confirmPromotion();
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