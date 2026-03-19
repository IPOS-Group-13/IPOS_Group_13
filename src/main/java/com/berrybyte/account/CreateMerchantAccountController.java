package com.berrybyte.account;

import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CreateMerchantAccountController {

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
    private TextField addressTextArea;

    @FXML
    private TextField accountStatusField;

    @FXML
    private TextField creditLimitField;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        if (MerchantDraftSession.hasDraft()) {
            nameTextField.setText(MerchantDraftSession.getFullName());
            companyNameTextField.setText(MerchantDraftSession.getCompanyName());
            usernameTextField.setText(MerchantDraftSession.getUsername());
            passwordField.setText(MerchantDraftSession.getPassword());
            phoneNumberTextField.setText(MerchantDraftSession.getPhoneNumber());
            emailTextField.setText(MerchantDraftSession.getEmail());
            addressTextArea.setText(MerchantDraftSession.getAddress());
            accountStatusField.setText(MerchantDraftSession.getAccountStatus());
            creditLimitField.setText(MerchantDraftSession.getCreditLimit());
        } else {
            accountStatusField.setText("NORMAL");
        }
    }

    @FXML
    private void handleNext(ActionEvent event) {
        try {
            validateMerchantDetails();

            MerchantDraftSession.saveDraft(
                    nameTextField.getText().trim(),
                    companyNameTextField.getText().trim(),
                    usernameTextField.getText().trim(),
                    passwordField.getText().trim(),
                    phoneNumberTextField.getText().trim(),
                    emailTextField.getText().trim(),
                    addressTextArea.getText().trim(),
                    accountStatusField.getText().trim().toUpperCase(),
                    creditLimitField.getText().trim()
            );

            SceneSwitcher.switchScene(event, "/account/discountPlanSelection.fxml", "Select Discount Plan");

        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    private void validateMerchantDetails() throws Exception {
        if (nameTextField.getText().isBlank()
                || companyNameTextField.getText().isBlank()
                || usernameTextField.getText().isBlank()
                || passwordField.getText().isBlank()
                || phoneNumberTextField.getText().isBlank()
                || emailTextField.getText().isBlank()
                || addressTextArea.getText().isBlank()
                || creditLimitField.getText().isBlank()
                || accountStatusField.getText().isBlank()) {
            throw new Exception("Fill in all required fields");
        }

        String status = accountStatusField.getText().trim().toUpperCase();

        if (!status.equals("NORMAL")
                && !status.equals("SUSPENDED")
                && !status.equals("IN_DEFAULT")) {
            throw new Exception("Status must be NORMAL, SUSPENDED or IN_DEFAULT");
        }

        String email = emailTextField.getText().trim();
        if (!email.matches(".*@.*\\..{2,}")) {
            throw new Exception("Enter a valid email address");
        }

        try {
            Double.parseDouble(creditLimitField.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Credit limit must be numeric");
        }
    }
}