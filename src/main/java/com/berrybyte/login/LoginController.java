package com.berrybyte.login;

import com.berrybyte.common.DatabaseConnection;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private Label loginMessageLabel;

    @FXML
    private Hyperlink forgotPassword;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void loginButtonOnAction(ActionEvent event) {
        if (usernameTextField.getText().isBlank() || passwordField.getText().isBlank()) {
            loginMessageLabel.setText("Enter your username and password");
            return;
        }

        validateLogin(event);
    }

    @FXML
    public void forgotPasswordAction(ActionEvent event) {
        forgotPassword.setDisable(true);
    }

    public void validateLogin(ActionEvent event) {
        String sql = "SELECT Role FROM Users WHERE Username = ? AND Password = ?";

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, usernameTextField.getText().trim());
            preparedStatement.setString(2, passwordField.getText().trim());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String role = resultSet.getString("Role");

                    if ("ADMIN".equalsIgnoreCase(role)) {
                        SceneSwitcher.switchScene(event, "/dashboard/adminDashboard.fxml", "Admin Dashboard");
                    } else if ("MANAGER".equalsIgnoreCase(role)) {
                        SceneSwitcher.switchScene(event, "/dashboard/managerDashboard.fxml", "Manager Dashboard");
                    } else if ("MERCHANT".equalsIgnoreCase(role)) {
                        loginMessageLabel.setText("Merchants must log in through IPOS-CA");
                    } else {
                        loginMessageLabel.setText("Unknown account role");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("Error while connecting to database");
        }
    }
}