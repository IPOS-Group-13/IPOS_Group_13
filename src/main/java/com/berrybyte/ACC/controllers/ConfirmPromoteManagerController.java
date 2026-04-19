package com.berrybyte.ACC.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Represents confirm promote manager controller.
 */
public class ConfirmPromoteManagerController {

    private EditManagerAccountController managerController;
    private EditStaffAccountController staffController;
/**
 * Sets parent controller.
 *
 * @param parentController parent controller
 */

/**
 * Sets parent controller.
 *
 * @param parentController parent controller
 */

    public void setParentController(EditManagerAccountController parentController) {
        this.managerController = parentController;
    }
/**
 * Sets staff controller.
 *
 * @param parentController parent controller
 */

/**
 * Sets staff controller.
 *
 * @param parentController parent controller
 */

    public void setStaffController(EditStaffAccountController parentController) {
        this.staffController = parentController;
    }

/**
 * Handles promote.
 *
 * @param event event
 */
    @FXML
    private void handlePromote(ActionEvent event) {
        if (managerController != null) {
            managerController.confirmPromotion();
        } else if (staffController != null) {
            staffController.confirmPromotion();
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

    private void closePopup(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
