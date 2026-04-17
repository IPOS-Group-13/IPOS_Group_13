package com.berrybyte.ACC.controllers;

import com.berrybyte.ACC.model.DiscountTier;
import com.berrybyte.ACC.services.MerchantAccountService;
import com.berrybyte.ACC.session.MerchantDraftSession;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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

    @FXML private Label messageLabel;

    private final MerchantAccountService merchantAccountService = new MerchantAccountService();

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        try {
            messageLabel.setText("");
            validateDraftSession();

            List<DiscountTier> tiers = new ArrayList<>();

            validateAndAddTier(tiers, tier1MinField, tier1MaxField, tier1PercentField, "Tier 1");
            validateAndAddTier(tiers, tier2MinField, tier2MaxField, tier2PercentField, "Tier 2");
            validateAndAddTier(tiers, tier3MinField, tier3MaxField, tier3PercentField, "Tier 3");

            if (tiers.isEmpty()) {
                messageLabel.setText("Enter at least one complete discount tier.");
                return;
            }

            double creditLimit = Double.parseDouble(MerchantDraftSession.getCreditLimit().trim());

            merchantAccountService.createMerchantAccount(
                    MerchantDraftSession.getFullName(),
                    MerchantDraftSession.getCompanyName(),
                    MerchantDraftSession.getUsername(),
                    MerchantDraftSession.getPassword(),
                    MerchantDraftSession.getEmail(),
                    MerchantDraftSession.getPhoneNumber(),
                    MerchantDraftSession.getAddress(),
                    MerchantDraftSession.getAccountStatus(),
                    creditLimit,
                    "FLEXIBLE",
                    tiers
            );
            //messageLabel.setText("Merchant account created successfully.");
            MerchantDraftSession.clear();
            RoleBasedNavigator.switchToManageAccounts(event);

        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to create merchant account.");
        }
    }
    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/account/discountPlanSelection.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle("Select Discount Plan");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateDraftSession() {

        if (!MerchantDraftSession.hasDraft()) {
            throw new IllegalArgumentException("Merchant details are missing. Please go back and re-enter them.");
        }

        if (isBlank(MerchantDraftSession.getFullName())
                || isBlank(MerchantDraftSession.getCompanyName())
                || isBlank(MerchantDraftSession.getUsername())
                || isBlank(MerchantDraftSession.getPassword())
                || isBlank(MerchantDraftSession.getEmail())
                || isBlank(MerchantDraftSession.getPhoneNumber())
                || isBlank(MerchantDraftSession.getAddress())
                || isBlank(MerchantDraftSession.getAccountStatus())
                || isBlank(MerchantDraftSession.getCreditLimit())) {

            throw new IllegalArgumentException("Merchant details are incomplete. Please go back and fill all fields.");
        }

        try {
            double creditLimit = Double.parseDouble(MerchantDraftSession.getCreditLimit().trim());

            if (creditLimit < 0) {
                throw new IllegalArgumentException("Credit limit must be 0 or greater.");
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Credit limit must be a valid number.");
        }
    }

    private void validateAndAddTier(
            List<DiscountTier> tiers,
            TextField minField,
            TextField maxField,
            TextField percentField,
            String tierName
    ) {
        String minText = safeText(minField);
        String maxText = safeText(maxField);
        String percentText = safeText(percentField);

        if (minText.isEmpty() && maxText.isEmpty() && percentText.isEmpty()) {
            return;
        }

        if (minText.isEmpty() || maxText.isEmpty() || percentText.isEmpty()) {
            throw new IllegalArgumentException(
                    tierName + ": All fields (Min, Max, Percentage) are required.");
        }
        double minValue;
        double maxValue;
        double percentValue;

        try {
            minValue = Double.parseDouble(minText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(tierName + ": Min Order Value must be a number.");
        }

        try {
            maxValue = Double.parseDouble(maxText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(tierName + ": Max Order Value must be a number.");
        }

        try {
            percentValue = Double.parseDouble(percentText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(tierName + ": Discount Percentage must be a number.");
        }

        if (minValue < 0) {
            throw new IllegalArgumentException(tierName + ": Min Order Value cannot be negative.");
        }

        if (maxValue < 0) {
            throw new IllegalArgumentException(tierName + ": Max Order Value cannot be negative.");
        }

        if (maxValue < minValue) {
            throw new IllegalArgumentException(
                    tierName + ": Max Order Value must be greater than or equal to Min Order Value."
            );
        }

        if (percentValue < 0 || percentValue > 100) {
            throw new IllegalArgumentException(
                    tierName + ": Discount Percentage must be between 0 and 100."
            );
        }
        tiers.add(new DiscountTier(minValue, maxValue, percentValue));
    }

    private String safeText(TextField field) {
        return (field == null || field.getText() == null) ? "" : field.getText().trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
