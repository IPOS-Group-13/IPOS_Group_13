package com.berrybyte.account;

import com.berrybyte.common.DatabaseConnection;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditAdminAccountController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button demoteButton;

    private int userId;
    private String pendingRole = "ADMIN";

    public void setUserId(int userId) {
        this.userId = userId;
        loadAdminDetails();
    }

    private void loadAdminDetails() {
        String sql = """
                SELECT Name, Username, Password, Email, PhoneNumber
                FROM Users
                WHERE UserId = ? AND Role = ?
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, "ADMIN");

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    nameTextField.setText(rs.getString("Name"));
                    usernameTextField.setText(rs.getString("Username"));
                    passwordField.setText(rs.getString("Password"));
                    emailTextField.setText(rs.getString("Email"));
                    phoneNumberTextField.setText(rs.getString("PhoneNumber"));

                    pendingRole = "ADMIN";
                    if (demoteButton != null) {
                        demoteButton.setText("Demote");
                        demoteButton.setDisable(false);
                    }
                    messageLabel.setText("");
                } else {
                    messageLabel.setText("Administrator account not found.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading administrator account.");
        }
    }

    @FXML
    public void updateAdminAccount(ActionEvent event) {
        try {
            validateAdminDetails();
            if ("MANAGER".equals(pendingRole) && !canDemoteAdmin()) {
                messageLabel.setText("At least one administrator must remain in the system.");
                return;
            }
            updateAdmin(event);
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDemoteButton(ActionEvent event) {
        if ("MANAGER".equals(pendingRole)) {
            messageLabel.setText("Demotion already selected. Click Save to apply it.");
            return;
        }
        if (!canDemoteAdmin()) {
            messageLabel.setText("At least one administrator must remain in the system.");
            return;
        }
        openDemotePopup();
    }

    private boolean canDemoteAdmin() {
        String sql = "SELECT COUNT(*) AS adminCount FROM Users WHERE Role = ?";

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, "ADMIN");

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("adminCount") > 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error checking administrator count.");
        }

        return false;
    }

    private void openDemotePopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/account/confirmDemoteAdmin.fxml"));
            Parent root = loader.load();

            ConfirmDemoteAdminController controller = loader.getController();
            controller.setParentController(this);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);

            Stage ownerStage = (Stage) messageLabel.getScene().getWindow();
            popupStage.initOwner(ownerStage);

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open demotion confirmation popup.");
        }
    }

    public void confirmDemotion() {
        pendingRole = "MANAGER";
        if (demoteButton != null) {
            demoteButton.setDisable(true);
        }
        messageLabel.setText("Demotion selected. Click Save to apply the role change.");
    }

    private void validateAdminDetails() throws Exception {
        String name = nameTextField.getText() == null ? "" : nameTextField.getText().trim();
        String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String email = emailTextField.getText() == null ? "" : emailTextField.getText().trim();
        String phone = phoneNumberTextField.getText() == null ? "" : phoneNumberTextField.getText().trim();

        if (name.isEmpty()) throw new Exception("Name is required.");
        if (username.isEmpty()) throw new Exception("Username is required.");
        if (password.isEmpty()) throw new Exception("Password is required.");
        if (email.isEmpty()) throw new Exception("Email is required.");
        if (phone.isEmpty()) throw new Exception("Phone number is required.");

        if (!name.matches("[A-Za-z ]+")) {
            throw new Exception("Name must contain only letters and spaces.");
        }
        if (!username.matches("[A-Za-z0-9_]+")) {
            throw new Exception("Username can only contain letters, numbers, and underscores.");
        }
        if (password.length() < 6) {
            throw new Exception("Password must be at least 6 characters long.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new Exception("Enter a valid email address.");
        }
        if (!phone.matches("\\+\\d{1,3}\\s\\d{7,12}")) {
            throw new Exception("Enter a valid phone number with country code (e.g. +44 7123456789).");
        }
    }

    private void updateAdmin(ActionEvent event) {
        String sql = """
                UPDATE Users
                SET Name = ?, Username = ?, Password = ?, Email = ?, PhoneNumber = ?, Role = ?
                WHERE UserId = ?
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, nameTextField.getText().trim());
            preparedStatement.setString(2, usernameTextField.getText().trim());
            preparedStatement.setString(3, passwordField.getText().trim());
            preparedStatement.setString(4, emailTextField.getText().trim());
            preparedStatement.setString(5, phoneNumberTextField.getText().trim());
            preparedStatement.setString(6, pendingRole);
            preparedStatement.setInt(7, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                RoleBasedNavigator.switchToStaffAccounts(event);
            } else {
                messageLabel.setText("No administrator account was updated.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error updating administrator account.");
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
}
