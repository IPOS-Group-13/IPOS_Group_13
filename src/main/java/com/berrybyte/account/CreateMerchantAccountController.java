package com.berrybyte.account;

import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
    private void handleBackButton(MouseEvent event) {
        try {
            String fxmlPath;
            String title;

            if ("pendingApplications".equals(MerchantDraftSession.getPreviousPage())) {
                fxmlPath = "/pendingapplications/pendingApplications.fxml";
                title = "Pending Applications";
            } else {
                fxmlPath = "/account/accountType.fxml";
                title = "Select Account Type";
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
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

        String name = nameTextField.getText() == null ? "" : nameTextField.getText().trim();
        String company = companyNameTextField.getText() == null ? "" : companyNameTextField.getText().trim();
        String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String phone = phoneNumberTextField.getText() == null ? "" : phoneNumberTextField.getText().trim();
        String email = emailTextField.getText() == null ? "" : emailTextField.getText().trim();
        String address = addressTextArea.getText() == null ? "" : addressTextArea.getText().trim();
        String status = accountStatusField.getText() == null ? "" : accountStatusField.getText().trim().toUpperCase();
        String creditLimitText = creditLimitField.getText() == null ? "" : creditLimitField.getText().trim();

        if (name.isEmpty()) throw new Exception("Full name is required.");
        if (company.isEmpty()) throw new Exception("Company name is required.");
        if (username.isEmpty()) throw new Exception("Username is required.");
        if (password.isEmpty()) throw new Exception("Password is required.");
        if (phone.isEmpty()) throw new Exception("Phone number is required.");
        if (email.isEmpty()) throw new Exception("Email is required.");
        if (address.isEmpty()) throw new Exception("Address is required.");
        if (status.isEmpty()) throw new Exception("Account status is required.");
        if (creditLimitText.isEmpty()) throw new Exception("Credit limit is required.");

        if (!name.matches("[A-Za-z ]+")) {
            throw new Exception("Name must contain only letters and spaces.");
        }
        if (!company.matches("[A-Za-z0-9 ]+")) {
            throw new Exception("Company name can only contain letters, numbers, and spaces.");
        }
        if (!username.matches("[A-Za-z0-9_]+")) {
            throw new Exception("Username can only contain letters, numbers, and underscores.");
        }
        if (password.length() < 6) {
            throw new Exception("Password must be at least 6 characters long.");
        }

        String normalizedPhone = normalizePhoneNumber(phone);
        if (!normalizedPhone.matches("[0-9 ]+")) {
            throw new Exception("Phone number must contain only numbers and spaces.");
        }
        int phoneDigits = normalizedPhone.replace(" ", "").length();
        if (phoneDigits < 7 || phoneDigits > 12) {
            throw new Exception("Enter a valid phone number using 7 to 12 digits.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new Exception("Enter a valid email address.");
        }
        if (!status.equals("NORMAL")
                && !status.equals("SUSPENDED")
                && !status.equals("IN_DEFAULT")) {
            throw new Exception("Status must be NORMAL, SUSPENDED or IN_DEFAULT.");
        }

        try {
            double creditLimit = Double.parseDouble(creditLimitText);
            if (creditLimit < 0) {
                throw new Exception("Credit limit must be 0 or greater.");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Credit limit must be numeric.");
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }

        String normalized = phoneNumber.trim().replaceAll("\\s+", " ");
        if (normalized.startsWith("+")) {
            normalized = normalized.replaceFirst("^\\+\\d{1,3}\\s*", "");
        }
        return normalized.trim();
    }
}
