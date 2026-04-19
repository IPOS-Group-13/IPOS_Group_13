package com.berrybyte.ACC.controllers;

import com.berrybyte.ACC.services.MerchantAccountService;
import com.berrybyte.ACC.util.PhoneNumberRules;
import com.berrybyte.common.DatabaseConnection;
import com.berrybyte.common.RoleBasedNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Represents edit merchant account controller.
 */
public class EditMerchantAccountController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField companyNameTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField accountStatusField;

    @FXML
    private TextField creditLimitField;

    @FXML
    private Label messageLabel;

    private int userId;

    private final MerchantAccountService merchantAccountService = new MerchantAccountService();
/**
 * Sets user id.
 *
 * @param userId user id
 */

/**
 * Sets user id.
 *
 * @param userId user id
 */
    public void setUserId(int userId) {
        this.userId = userId;
        loadMerchantDetails();
    }
/**
 * Loads merchant details.
 *
 */

/**
 * Loads merchant details.
 *
 */
    private void loadMerchantDetails() {
        String sql = """
                SELECT
                    u.Name,
                    u.Username,
                    u.Password,
                    u.Email,
                    u.PhoneNumber,
                    ma.CompanyName,
                    ma.Address,
                    ma.AccountStatus,
                    ma.CreditLimit
                FROM Users u
                JOIN MerchantAccounts ma ON u.UserId = ma.UserId
                WHERE u.UserId = ? AND u.Role = 'MERCHANT'
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    nameTextField.setText(rs.getString("Name"));
                    companyNameTextField.setText(rs.getString("CompanyName"));
                    usernameTextField.setText(rs.getString("Username"));
                    passwordField.setText(rs.getString("Password"));
                    phoneNumberTextField.setText(PhoneNumberRules.normalize(rs.getString("PhoneNumber")));
                    emailTextField.setText(rs.getString("Email"));
                    addressTextField.setText(rs.getString("Address"));
                    accountStatusField.setText(rs.getString("AccountStatus"));
                    creditLimitField.setText(String.valueOf(rs.getDouble("CreditLimit")));
                    messageLabel.setText("");
                } else {
                    messageLabel.setText("Merchant account not found.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading merchant account.");
        }
    }

/**
 * Executes the update merchant account workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 */
    @FXML
    public void updateMerchantAccount(ActionEvent event) {
        try {
            String fullName = safeText(nameTextField);
            String companyName = safeText(companyNameTextField);
            String username = safeText(usernameTextField);
            String password = safeText(passwordField);
            String phoneNumber = safeText(phoneNumberTextField);
            String email = safeText(emailTextField);
            String address = safeText(addressTextField);
            String accountStatus = safeText(accountStatusField).toUpperCase();
            String creditLimitText = safeText(creditLimitField);

            if (creditLimitText.isEmpty()) {
                messageLabel.setText("Credit limit is required.");
                return;
            }

            double creditLimit;
            try {
                creditLimit = Double.parseDouble(creditLimitText);
            } catch (NumberFormatException e) {
                messageLabel.setText("Credit limit must be numeric.");
                return;
            }

            merchantAccountService.updateMerchantAccountDetails(userId, fullName, companyName, username, password, email,
                    phoneNumber, address, accountStatus, creditLimit);

            RoleBasedNavigator.switchToMerchantMenu(event);

        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to update merchant account.");
        }
    }

/**
 * Handles back button.
 *
 * @param event event
 */
    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            RoleBasedNavigator.openMerchantMenu((Node) event.getSource());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }
/**
 * Performs safe text.
 *
 * @param field field
 * @return result value
 */

/**
 * Performs safe text.
 *
 * @param field field
 * @return result value
 */
    private String safeText(TextField field) {
        return (field == null || field.getText() == null)
                ? ""
                : field.getText().trim();
    }
/**
 * Performs safe text.
 *
 * @param field field
 * @return result value
 */

/**
 * Performs safe text.
 *
 * @param field field
 * @return result value
 */
    private String safeText(PasswordField field) {
        return (field == null || field.getText() == null)
                ? ""
                : field.getText().trim();
    }
}
