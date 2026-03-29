package com.berrybyte.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ConfirmDeleteAccountController {

    private final DeleteAccountService deleteAccountService = new DeleteAccountService();

    private UserAccountRow selectedUser;
    private DeleteAccountController parentController;

    public void setSelectedUser(UserAccountRow selectedUser) {
        this.selectedUser = selectedUser;
    }

    public void setParentController(DeleteAccountController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedUser == null) {
            return;
        }
        try {
            deleteAccountService.deleteUserAccount(selectedUser.getUserId());
            if (parentController != null) {
                parentController.refreshUsers();
            }
            closePopup(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
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