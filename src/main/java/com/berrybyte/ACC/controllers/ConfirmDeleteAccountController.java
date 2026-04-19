package com.berrybyte.ACC.controllers;

import com.berrybyte.ACC.model.UserAccountRow;
import com.berrybyte.ACC.services.DeleteAccountService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Represents confirm delete account controller.
 */
public class ConfirmDeleteAccountController {

    private final DeleteAccountService deleteAccountService = new DeleteAccountService();

    private UserAccountRow selectedUser;
    private DeleteAccountController parentController;
    private Runnable onDeleteSuccess;
/**
 * Sets selected user.
 *
 * @param selectedUser selected user
 */

/**
 * Sets selected user.
 *
 * @param selectedUser selected user
 */

    public void setSelectedUser(UserAccountRow selectedUser) {
        this.selectedUser = selectedUser;
    }
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

    public void setParentController(DeleteAccountController parentController) {
        this.parentController = parentController;
    }
/**
 * Sets on delete success.
 *
 * @param onDeleteSuccess on delete success
 */

/**
 * Sets on delete success.
 *
 * @param onDeleteSuccess on delete success
 */

    public void setOnDeleteSuccess(Runnable onDeleteSuccess) {
        this.onDeleteSuccess = onDeleteSuccess;
    }

/**
 * Handles delete.
 *
 * @param event event
 */
    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedUser == null) {
            return;
        }
        try {
            deleteAccountService.deleteUserAccount(selectedUser.getUserId());
            if (onDeleteSuccess != null) {
                onDeleteSuccess.run();
            } else if (parentController != null) {
                parentController.refreshUsers();
            }
            closePopup(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
