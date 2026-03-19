package com.berrybyte.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class FlexibleDiscountController {

    @FXML private TextField tier1MinField;
    @FXML private TextField tier1MaxField;
    @FXML private TextField tier1PercentField;

    @FXML private TextField tier2MinField;
    @FXML private TextField tier2MaxField;
    @FXML private TextField tier2PercentField;

    @FXML private TextField tier3MinField;
    @FXML private TextField tier3MaxField;
    @FXML private TextField tier3PercentField;

    @FXML
    private Label messageLabel;

    private final MerchantAccountService merchantAccountService = new MerchantAccountService();

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        try {
            List<DiscountTier> tiers = new ArrayList<>();

            addTierIfFilled(tiers, tier1MinField, tier1MaxField, tier1PercentField);
            addTierIfFilled(tiers, tier2MinField, tier2MaxField, tier2PercentField);
            addTierIfFilled(tiers, tier3MinField, tier3MaxField, tier3PercentField);

            if (tiers.isEmpty()) {
                messageLabel.setText("Enter at least one flexible discount tier");
                return;
            }

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
                    "FLEXIBLE",
                    tiers
            );

            messageLabel.setText("Merchant account created successfully");
            MerchantDraftSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage());
        }
    }

    private void addTierIfFilled(List<DiscountTier> tiers, TextField minField, TextField maxField, TextField percentField) {
        if (!minField.getText().isBlank() && !percentField.getText().isBlank()) {
            double min = Double.parseDouble(minField.getText().trim());
            Double max = maxField.getText().isBlank() ? null : Double.parseDouble(maxField.getText().trim());
            double percent = Double.parseDouble(percentField.getText().trim());

            if (percent < 0 || percent > 100) {
                throw new IllegalArgumentException("Discount percent must be between 0 and 100");
            }

            tiers.add(new DiscountTier(min, max, percent));
        }
    }
}
