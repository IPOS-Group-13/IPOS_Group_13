package com.berrybyte.account;

import com.berrybyte.common.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.Statement;

public class CreateAdminAccountController {

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
    public void createAdminAccount(ActionEvent event) {

        if (nameTextField.getText().isBlank()
                || idNumberTextField.getText().isBlank()
                || usernameTextField.getText().isBlank()
                || passwordField.getText().isBlank()
                || emailTextField.getText().isBlank()
                || phoneNumberTextField.getText().isBlank()) {

            messageLabel.setText("Fill in all required fields");
        }
        else {
            createAdmin();
        }
    }

    public void createAdmin() {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection conn = connectNow.getConnection();

        String fullName = nameTextField.getText().trim();
        String firstName = "";
        String lastName = "";

        if (fullName.contains(" ")) {
            String[] parts = fullName.split(" ", 2);
            firstName = parts[0];
            lastName = parts[1];
        } else {
            firstName = fullName;
        }

        String insertFields = "INSERT INTO useraccounts (Firstname, Lastname, Username, Password, IdNumber, Email, PhoneNumber, Role) VALUES ('"
                + firstName + "', '"
                + lastName + "', '"
                + usernameTextField.getText() + "', '"
                + passwordField.getText() + "', '"
                + idNumberTextField.getText() + "', '"
                + emailTextField.getText() + "', '"
                + phoneNumberTextField.getText() + "', '"
                + "ADMIN" + "')";

        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(insertFields);

            messageLabel.setText("Administrator account created successfully");
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error creating administrator account");
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
