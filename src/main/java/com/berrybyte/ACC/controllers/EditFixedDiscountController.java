package com.berrybyte.ACC.controllers;

import com.berrybyte.ACC.services.MerchantAccountService;
import com.berrybyte.common.DatabaseConnection;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Represents edit fixed discount controller.
 */
public class EditFixedDiscountController {

    @FXML
    private TextField fixedDiscountPercentField;

    @FXML
    private Label messageLabel;

    private int merchantId;

    private final MerchantAccountService merchantAccountService = new MerchantAccountService();

/**
 * Sets merchant id.
 *
 * @param merchantId merchant id
 */

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
        loadCurrentFixedDiscount();
    }

/**
 * Loads current fixed discount.
 *
 */
    private void loadCurrentFixedDiscount() {
        String sql = """
                SELECT dpt.DiscountPercent
                FROM DiscountPlans dp
                JOIN DiscountPlanTiers dpt ON dp.DiscountPlanId = dpt.DiscountPlanId
                WHERE dp.MerchantId = ?
                  AND dp.IsActive = 1
                  AND dp.PlanType = 'FIXED'
                ORDER BY dp.DiscountPlanId DESC
                LIMIT 1
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, merchantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    fixedDiscountPercentField.setText(String.valueOf(rs.getDouble("DiscountPercent")));
                } else {
                    fixedDiscountPercentField.setText("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load current fixed discount.");
        }
    }

/**
 * Handles save.
 *
 * @param event event
 */
    @FXML
    private void handleSave(ActionEvent event) {
        try {
            messageLabel.setText("");

            String percentText = fixedDiscountPercentField.getText() == null ? "" : fixedDiscountPercentField.getText().trim();

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

            merchantAccountService.updateMerchantFixedDiscountPlan(merchantId, percent);

            RoleBasedNavigator.switchToMerchantMenu(event);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to update fixed discount plan.");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/account/editMerchantDiscountPlan.fxml"));
            Parent root = loader.load();

            DiscountPlanEditController controller = loader.getController();
            controller.setMerchantId(merchantId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle("Edit Discount Plan");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }
}
