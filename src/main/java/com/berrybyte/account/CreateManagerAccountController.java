package com.berrybyte.account;

import com.berrybyte.common.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        if (nameTextField.getText().isBlank()
                || idNumberTextField.getText().isBlank()
                || usernameTextField.getText().isBlank()
                || passwordField.getText().isBlank()
                || emailTextField.getText().isBlank()
                || phoneNumberTextField.getText().isBlank()) {

            messageLabel.setText("Fill in all required fields");
            return;
        }

        createManager();
    }

    public void createManager() {
        String fullName = nameTextField.getText().trim();
        String firstName = "";
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

            messageLabel.setText("Manager account created successfully");
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error creating manager account");
        }
    }

    public void clearFields() {
        nameTextField.clear();
        idNumberTextField.clear();
        usernameTextField.clear();
        passwordField.clear();
        emailTextField.clear();
        phoneNumberTextField.clear();
    }
}