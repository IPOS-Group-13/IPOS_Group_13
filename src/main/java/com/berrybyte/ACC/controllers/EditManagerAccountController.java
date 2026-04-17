package com.berrybyte.ACC.controllers;

import com.berrybyte.common.DatabaseConnection;
import com.berrybyte.common.RoleBasedNavigator;
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

public class EditManagerAccountController {

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
    private Button promoteButton;

    private int userId;
    private String pendingRole = "MANAGER";

    public void setUserId(int userId) {
        this.userId = userId;
        loadManagerDetails();
    }

    private void loadManagerDetails() {
        String sql = """
                SELECT Name, Username, Password, Email, PhoneNumber
                FROM Users
                WHERE UserId = ? AND Role = ?
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, "MANAGER");

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    nameTextField.setText(rs.getString("Name"));
                    usernameTextField.setText(rs.getString("Username"));
                    passwordField.setText(rs.getString("Password"));
                    emailTextField.setText(rs.getString("Email"));
                    phoneNumberTextField.setText(rs.getString("PhoneNumber"));

                    pendingRole = "MANAGER";

                    if (promoteButton != null) {
                        promoteButton.setText("Promote");
                        promoteButton.setDisable(false);
                    }

                    messageLabel.setText("");
                } else {
                    messageLabel.setText("Manager account not found.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading manager account.");
        }
    }

    @FXML
    public void updateManagerAccount(ActionEvent event) {
        try {
            validateManagerDetails();
            updateManager(event);
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handlePromoteButton(ActionEvent event) {
        if ("ADMIN".equals(pendingRole)) {
            messageLabel.setText("Promotion already selected. Click Save to apply it.");
            return;
        }

        openPromotePopup();
    }

    private void openPromotePopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/account/confirmPromoteManager.fxml"));
            Parent root = loader.load();

            ConfirmPromoteManagerController controller = loader.getController();
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
            messageLabel.setText("Unable to open promotion confirmation popup.");
        }
    }

    public void confirmPromotion() {
        pendingRole = "ADMIN";

        if (promoteButton != null) {
            promoteButton.setText("Promoted");
            promoteButton.setDisable(true);
        }

        messageLabel.setText("Promotion selected. Click Save to apply the role change.");
    }

    private void validateManagerDetails() throws Exception {
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

    private void updateManager(ActionEvent event) {
        String sql = """
                UPDATE Users
                SET Name = ?, Username = ?, Password = ?, Email = ?, PhoneNumber = ?, Role = ?
                WHERE UserId = ? AND Role = ?
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
            preparedStatement.setString(8, "MANAGER");

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
            RoleBasedNavigator.switchToManageAccounts(event);
            } else {
                messageLabel.setText("No manager account was updated.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error updating manager account.");
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
}
