package com.berrybyte.ACC.controllers;

import com.berrybyte.ACC.model.DiscountTier;
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
import java.util.ArrayList;
import java.util.List;

public class EditFlexibleDiscountController {

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

    private int merchantId;

    private final MerchantAccountService merchantAccountService = new MerchantAccountService();

    @FXML
    public void initialize() {
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
        loadCurrentFlexibleDiscount();
    }

    private void loadCurrentFlexibleDiscount() {
        if (merchantId <= 0) {
            messageLabel.setText("Invalid merchant ID.");
            return;
        }

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection conn = null;

        try {
            conn = connectNow.getConnection();
            
            if (conn == null) {
                messageLabel.setText("Database connection failed.");
                return;
            }

            // First, verify the merchant exists
            String verifyMerchantSql = "SELECT MerchantId FROM MerchantAccounts WHERE MerchantId = ?";
            try (PreparedStatement verifyPs = conn.prepareStatement(verifyMerchantSql)) {
                verifyPs.setInt(1, merchantId);
                try (ResultSet verifyRs = verifyPs.executeQuery()) {
                    if (!verifyRs.next()) {
                        messageLabel.setText("Merchant not found.");
                        return;
                    }
                }
            }

            String sql = """
                    SELECT dpt.MinOrderValue, dpt.MaxOrderValue, dpt.DiscountPercent
                    FROM DiscountPlans dp
                    JOIN DiscountPlanTiers dpt ON dp.DiscountPlanId = dpt.DiscountPlanId
                    WHERE dp.MerchantId = ?
                      AND dp.IsActive = TRUE
                      AND dp.PlanType = 'FLEXIBLE'
                    ORDER BY dpt.MinOrderValue ASC
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, merchantId);

                try (ResultSet rs = ps.executeQuery()) {
                    List<DiscountTier> tiers = new ArrayList<>();

                    while (rs.next()) {
                        Double maxValue = rs.getObject("MaxOrderValue") == null ? null : rs.getDouble("MaxOrderValue");
                        tiers.add(new DiscountTier(
                                rs.getDouble("MinOrderValue"),
                                maxValue,
                                rs.getDouble("DiscountPercent")));
                    }
                    populateTierFields(tiers);
                }
            }

        } catch (Exception e) {
            messageLabel.setText("Unable to load current flexible discount.");
        }
    }

    private void populateTierFields(List<DiscountTier> tiers) {
        clearAllFields();

        if (tiers == null || tiers.isEmpty()) {
            return;
        }

        if (tiers.size() > 0) {
            tier1MinField.setText(String.valueOf(tiers.get(0).getMinOrderValue()));
            tier1MaxField.setText(tiers.get(0).getMaxOrderValue() == null ? "" : String.valueOf(tiers.get(0).getMaxOrderValue()));
            tier1PercentField.setText(String.valueOf(tiers.get(0).getDiscountPercent()));
        }

        if (tiers.size() > 1) {
            tier2MinField.setText(String.valueOf(tiers.get(1).getMinOrderValue()));
            tier2MaxField.setText(tiers.get(1).getMaxOrderValue() == null ? "" : String.valueOf(tiers.get(1).getMaxOrderValue()));
            tier2PercentField.setText(String.valueOf(tiers.get(1).getDiscountPercent()));
        }

        if (tiers.size() > 2) {
            tier3MinField.setText(String.valueOf(tiers.get(2).getMinOrderValue()));
            tier3MaxField.setText(tiers.get(2).getMaxOrderValue() == null ? "" : String.valueOf(tiers.get(2).getMaxOrderValue()));
            tier3PercentField.setText(String.valueOf(tiers.get(2).getDiscountPercent()));
        }
    }

    private void clearAllFields() {
        tier1MinField.clear();
        tier1MaxField.clear();
        tier1PercentField.clear();

        tier2MinField.clear();
        tier2MaxField.clear();
        tier2PercentField.clear();

        tier3MinField.clear();
        tier3MaxField.clear();
        tier3PercentField.clear();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            messageLabel.setText("");

            List<DiscountTier> tiers = new ArrayList<>();

            validateAndAddTier(tiers, tier1MinField, tier1MaxField, tier1PercentField, "Tier 1");
            validateAndAddTier(tiers, tier2MinField, tier2MaxField, tier2PercentField, "Tier 2");
            validateAndAddTier(tiers, tier3MinField, tier3MaxField, tier3PercentField, "Tier 3");

            if (tiers.isEmpty()) {
                messageLabel.setText("Enter at least one complete discount tier.");
                return;
            }

            merchantAccountService.updateMerchantFlexibleDiscountPlan(merchantId, tiers);

            RoleBasedNavigator.switchToMerchantMenu(event);

        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to update flexible discount plan.");
        }
    }

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
            throw new IllegalArgumentException(tierName + ": All fields (Min, Max, Percentage) are required.");
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
}
