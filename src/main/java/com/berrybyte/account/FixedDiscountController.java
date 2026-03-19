package com.berrybyte.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

public class FixedDiscountController {

    @FXML
    private TextField fixedDiscountPercentField;

    @FXML
    private Label messageLabel;

    private final MerchantAccountService merchantAccountService = new MerchantAccountService();

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        try {
            if (fixedDiscountPercentField.getText().isBlank()) {
                messageLabel.setText("Enter discount percentage");
                return;
            }

            double percent = Double.parseDouble(fixedDiscountPercentField.getText().trim());

            if (percent < 0 || percent > 100) {
                messageLabel.setText("Discount percent must be between 0 and 100");
                return;
            }

            List<DiscountTier> tiers = List.of(
                    new DiscountTier(0.0, null, percent)
            );

            merchantAccountService.createMerchantAccount(
                    MerchantDraftSession.getFullName(),
                    MerchantDraftSession.getCompanyName(),
                    MerchantDraftSession.getUsername(),
                    MerchantDraftSession.getPassword(),
                    MerchantDraftSession.getEmail(),
                    MerchantDraftSession.getPhoneNumber(),
                    MerchantDraftSession.getAddress(),
                    MerchantDraftSession.getAccountStatus(),
                    Double.parseDouble(MerchantDraftSession.getCreditLimit()),
                    "FIXED",
                    tiers
            );

            messageLabel.setText("Merchant account created successfully");
            MerchantDraftSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage());
        }
    }
}