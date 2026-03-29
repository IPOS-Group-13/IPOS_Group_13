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

public class CreateManagerAccountController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField idNumberTextField;

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
    public void createManagerAccount(ActionEvent event) {
        try {
            validateManagerDetails();
            createManager(event);
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    private void validateManagerDetails() throws Exception {
        String name = nameTextField.getText() == null ? "" : nameTextField.getText().trim();
        String idNumber = idNumberTextField.getText() == null ? "" : idNumberTextField.getText().trim();
        String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String email = emailTextField.getText() == null ? "" : emailTextField.getText().trim();
        String phone = phoneNumberTextField.getText() == null ? "" : phoneNumberTextField.getText().trim();

        if (name.isEmpty()) throw new Exception("Full name is required.");
        if (idNumber.isEmpty()) throw new Exception("ID number is required.");
        if (username.isEmpty()) throw new Exception("Username is required.");
        if (password.isEmpty()) throw new Exception("Password is required.");
        if (email.isEmpty()) throw new Exception("Email is required.");
        if (phone.isEmpty()) throw new Exception("Phone number is required.");

        if (!name.matches("[A-Za-z ]+")) {
            throw new Exception("Name must contain only letters and spaces.");
        }

        if (!idNumber.matches("[A-Za-z0-9-]+")) {
            throw new Exception("ID number can only contain letters, numbers, and hyphens.");
        }

        if (!username.matches("[A-Za-z0-9_]+")) {
            throw new Exception("Username can only contain letters, numbers, and underscores.");
        }

        if (password.length() < 6) {
            throw new Exception("Password must be at least 6 characters long.");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new Exception("Enter a valid email address.");
        }

        if (!phone.matches("\\+\\d{1,3}\\s\\d{7,12}")) {
            throw new Exception("Enter a valid phone number with country code (e.g. +44 7123456789).");
        }
    }

    private void createManager(ActionEvent event) {
        String fullName = nameTextField.getText().trim();
        String firstName;
        String lastName = "";

        if (fullName.contains(" ")) {
            String[] parts = fullName.split(" ", 2);
            firstName = parts[0].trim();
            lastName = parts[1].trim();
        } else {
            firstName = fullName;
        }

        String sql = """
                INSERT INTO Users
                (Firstname, Lastname, Username, Password, IdNumber, Email, PhoneNumber, Role)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, usernameTextField.getText().trim());
            preparedStatement.setString(4, passwordField.getText().trim());
            preparedStatement.setString(5, idNumberTextField.getText().trim());
            preparedStatement.setString(6, emailTextField.getText().trim());
            preparedStatement.setString(7, phoneNumberTextField.getText().trim());
            preparedStatement.setString(8, "MANAGER");

            preparedStatement.executeUpdate();

            //messageLabel.setText("Manager account created successfully");
            SceneSwitcher.switchScene(event,
                    "/staffaccounts/staffAccounts.fxml",
                    "Staff Accounts");
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error creating manager account");
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
        }
    }

    private void clearFields() {
        nameTextField.clear();
        idNumberTextField.clear();
        usernameTextField.clear();
        passwordField.clear();
        emailTextField.clear();
        phoneNumberTextField.clear();
    }
}