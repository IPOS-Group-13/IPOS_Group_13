package com.berrybyte.ACC.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Represents confirm demote admin controller.
 */
public class ConfirmDemoteAdminController {

    private EditAdminAccountController parentController;
/**
 * Sets parent controller.
 *
 * @param parentController parent controller
 */

    public void setParentController(EditAdminAccountController parentController) {
        this.parentController = parentController;
    }

/**
 * Handles demote.
 *
 * @param event event
 */
    @FXML
    private void handleDemote(ActionEvent event) {
        if (parentController != null) {
            parentController.confirmDemotion();
        }
        closePopup(event);
    }

/**
 * Handles cancel.
 *
 * @param event event
 */
    @FXML
    private void handleCancel(ActionEvent event) {
        closePopup(event);
    }
/**
 * Performs close popup.
 *
 * @param event event
 */

/**
 * Performs close popup.
 *
 * @param event event
 */

    private void closePopup(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
