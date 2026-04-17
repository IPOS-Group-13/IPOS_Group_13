package com.berrybyte.ACC.controllers;

import com.berrybyte.ACC.model.UserAccountRow;
import com.berrybyte.common.DatabaseConnection;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EditAccountsController {

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
    private void handleEditAccount(ActionEvent event) {
        if (selectedUser == null) {
            messageLabel.setText("Select a user first.");
            return;
        }

        try {
            String role = selectedUser.getRole() == null ? "" : selectedUser.getRole().trim().toUpperCase();
            String fxmlPath;
            String title;

            switch (role) {
                case "ADMIN":
                    fxmlPath = "/account/editAdminAccount.fxml";
                    title = "Edit Administrator Details";
                    break;

                case "MANAGER":
                    fxmlPath = "/account/editManagerAccount.fxml";
                    title = "Edit Manager Details";
                    break;


                default:
                    fxmlPath = "/account/editStaffAccount.fxml";
                    title = "Edit Staff Details";
                    break;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            Method setUserIdMethod = controller.getClass().getMethod("setUserId", int.class);
            setUserIdMethod.invoke(controller, selectedUser.getUserId());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle(title);
            stage.show();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open edit page.");
        }
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            RoleBasedNavigator.openManageAccounts((Node) event.getSource());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }

    private void loadUsers(String searchText) {
        try {
            usersTable.setItems(FXCollections.observableArrayList(searchUsers(searchText)));
            messageLabel.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load users.");
        }
    }

    private List<UserAccountRow> searchUsers(String searchText) throws Exception {
        List<UserAccountRow> users = new ArrayList<>();

        String sql = """
                SELECT UserId, Name, Username, Role
                FROM Users
                WHERE (Name LIKE ? OR Username LIKE ?)
                  AND Role <> 'MERCHANT'
                ORDER BY Name, UserId
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String keyword = "%" + (searchText == null ? "" : searchText.trim()) + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(new UserAccountRow(
                            rs.getInt("UserId"),
                            rs.getString("Name"),
                            rs.getString("Username"),
                            rs.getString("Role")));
                }
            }
        }
        return users;
    }
}
