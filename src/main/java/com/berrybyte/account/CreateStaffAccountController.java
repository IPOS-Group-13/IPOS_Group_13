package com.berrybyte.account;

import com.berrybyte.common.DatabaseConnection;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateStaffAccountController {

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
    private TextField roleTextField;

    @FXML
    private Label messageLabel;

    @FXML
    public void createStaffAccount(ActionEvent event) {
        try {
            validateStaffDetails();
            createStaff(event);
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    private void validateStaffDetails() throws Exception {
        String name = nameTextField.getText() == null ? "" : nameTextField.getText().trim();
        String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String email = emailTextField.getText() == null ? "" : emailTextField.getText().trim();
        String phone = phoneNumberTextField.getText() == null ? "" : phoneNumberTextField.getText().trim();
        String role = roleTextField.getText() == null ? "" : roleTextField.getText().trim();

        if (name.isEmpty()) throw new Exception("Name is required.");
        if (username.isEmpty()) throw new Exception("Username is required.");
        if (password.isEmpty()) throw new Exception("Password is required.");
        if (email.isEmpty()) throw new Exception("Email is required.");
        if (phone.isEmpty()) throw new Exception("Phone number is required.");
        if (role.isEmpty()) throw new Exception("Role is required.");

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

    private void createStaff(ActionEvent event) {
        String sql = """
                INSERT INTO Users
                (Name, Username, Password, Email, PhoneNumber, Role)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, nameTextField.getText().trim());
            preparedStatement.setString(2, usernameTextField.getText().trim());
            preparedStatement.setString(3, passwordField.getText().trim());
            preparedStatement.setString(4, emailTextField.getText().trim());
            preparedStatement.setString(5, phoneNumberTextField.getText().trim());
            preparedStatement.setString(6, roleTextField.getText().trim());

            preparedStatement.executeUpdate();
            clearFields();
            SceneSwitcher.switchScene(event, "/dashboard/staffAccountsMenu.fxml", "Staff Accounts");

        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Error creating staff account.");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unexpected error creating staff account.");
        }
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/account/accountType.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Select Account Type");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error going back.");
        }
    }

    private void clearFields() {
        nameTextField.clear();
        usernameTextField.clear();
        passwordField.clear();
        emailTextField.clear();
        phoneNumberTextField.clear();
        roleTextField.clear();
    }
}