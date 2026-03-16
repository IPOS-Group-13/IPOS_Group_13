package com.berrybyte.login;

import com.berrybyte.common.DatabaseConnection;
import com.berrybyte.common.SceneSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginController {

    @FXML
    private Label loginMessageLabel;

    @FXML
    private Hyperlink forgotPassword;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;


    public void loginButtonOnAction(ActionEvent event) {

        if (usernameTextField.getText().isBlank() || passwordField.getText().isBlank()) {
            loginMessageLabel.setText("Enter your username and password");
        }
        else {
            validateLogin(event);
        }
    }


    public void forgotPasswordAction(ActionEvent event) {
        forgotPassword.setDisable(true);
    }


    public void validateLogin(ActionEvent event) {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection conn = connectNow.getConnection();

        String verifyLogin =
                "SELECT count(1) FROM useraccounts WHERE username = '"
                        + usernameTextField.getText()
                        + "' AND password = '"
                        + passwordField.getText()
                        + "'";

        try {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(verifyLogin);

            while (resultSet.next()) {

                if (resultSet.getInt(1) == 1) {

                    loginMessageLabel.setText("Welcome " + usernameTextField.getText());

                    // Switch to account type screen
                    SceneSwitcher.switchScene(
                            event,
                            "/account/accountType.fxml",
                            "Select Account Type"
                    );
                }
                else {
                    loginMessageLabel.setText("Invalid Login. Please try again");
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("Error while connecting to database");
        }
    }
}