package com.berrybyte.account;

import com.berrybyte.common.DatabaseConnection;
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
    private TableColumn<UserAccountRow, String> idNumberColoumn;

    @FXML
    private TableColumn<UserAccountRow, String> roleColoumn;

    @FXML
    private Label messageLabel;

    private UserAccountRow selectedUser;

    @FXML
    public void initialize() {
        nameColoumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        usernameColoumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        idNumberColoumn.setCellValueFactory(new PropertyValueFactory<>("idNumber"));
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
            String role = selectedUser.getRole();
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

                case "MERCHANT":
                    fxmlPath = "/account/editMerchantAccount.fxml";
                    title = "Edit Merchant Details";
                    break;

                default:
                    messageLabel.setText("Unsupported account role.");
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();
            Method setUserIdMethod = controller.getClass().getMethod("setUserId", int.class);
            setUserIdMethod.invoke(controller, selectedUser.getUserId());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            messageLabel.setText("Next edit controller must contain setUserId(int userId).");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open edit page.");
        }
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/staffaccounts/staffAccounts.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Accounts");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUsers(String searchText) {
        try {
            usersTable.setItems(FXCollections.observableArrayList(
                    searchUsers(searchText)
            ));
            messageLabel.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load users.");
        }
    }

    private List<UserAccountRow> searchUsers(String searchText) throws Exception {
        List<UserAccountRow> users = new ArrayList<>();

        String sql = """
        SELECT UserId, Firstname, Lastname, Username, IdNumber, Role
        FROM Users
        WHERE Firstname LIKE ? OR Lastname LIKE ? OR Username LIKE ?
        ORDER BY Firstname, Lastname, UserId
        """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String keyword = "%" + (searchText == null ? "" : searchText.trim()) + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setString(3, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String firstName = rs.getString("Firstname");
                    String lastName = rs.getString("Lastname");

                    String fullName;
                    if (lastName == null || lastName.trim().isEmpty()) {
                        fullName = firstName;
                    } else {
                        fullName = firstName + " " + lastName;
                    }

                    users.add(new UserAccountRow(
                            rs.getInt("UserId"),
                            fullName,
                            rs.getString("Username"),
                            rs.getString("IdNumber"),
                            rs.getString("Role")
                    ));
                }
            }
        }
        return users;
    }
}