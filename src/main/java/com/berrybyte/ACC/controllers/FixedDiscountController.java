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
            messageLabel.setText("");

            String percentText = fixedDiscountPercentField.getText() == null
                    ? ""
                    : fixedDiscountPercentField.getText().trim();

            if (percentText.isEmpty()) {
                messageLabel.setText("Enter discount percentage.");
                return;
            }
            if (!percentText.matches("\\d+(\\.\\d+)?")) {
                messageLabel.setText("Discount percentage must be a number.");
                return;
            }

            double percent = Double.parseDouble(percentText);

            if (percent < 0 || percent > 100) {
                messageLabel.setText("Discount percentage must be between 0 and 100.");
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
                    Double.parseDouble(MerchantDraftSession.getCreditLimit().trim()),
                    "FIXED",
                    tiers
            );
            //messageLabel.setText("Merchant account created successfully");
            MerchantDraftSession.clear();
            RoleBasedNavigator.switchToManageAccounts(event);

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
}
