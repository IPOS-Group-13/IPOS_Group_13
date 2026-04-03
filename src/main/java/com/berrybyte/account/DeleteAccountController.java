package com.berrybyte.account;

import com.berrybyte.common.RoleBasedNavigator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DeleteAccountController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<UserAccountRow> usersTable;

    @FXML
    private TableColumn<UserAccountRow, String> nameColoumn;

    @FXML
    private TableColumn<UserAccountRow, String> usernameColoumn;

    @FXML
    private TableColumn<UserAccountRow, String> roleColoumn;

    @FXML
    private Label messageLabel;

    private final DeleteAccountService deleteAccountService = new DeleteAccountService();
    private UserAccountRow selectedUser;

    @FXML
    public void initialize() {
        nameColoumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        usernameColoumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColoumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedUser = newSelection;
        });

        loadUsers("");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        loadUsers(searchField.getText());
    }

    @FXML
    private void handleDeleteAccount(ActionEvent event) {
        if (selectedUser == null) {
            messageLabel.setText("Select a user first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/account/confirmDeleteAccount.fxml"));
            Parent root = loader.load();

            ConfirmDeleteAccountController controller = loader.getController();
            controller.setSelectedUser(selectedUser);
            controller.setParentController(this);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open confirmation popup.");
        }
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            RoleBasedNavigator.openStaffAccounts((Node) event.getSource());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }

    public void refreshUsers() {
        loadUsers(searchField.getText());
        usersTable.getSelectionModel().clearSelection();
        selectedUser = null;
        messageLabel.setText("Account deleted successfully.");
    }

    private void loadUsers(String searchText) {
        try {
            usersTable.setItems(FXCollections.observableArrayList(
                    deleteAccountService.searchUsers(searchText)
            ));
            messageLabel.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load users.");
        }
    }
}
