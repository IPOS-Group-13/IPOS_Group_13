package com.berrybyte.ACC.controllers;

import com.berrybyte.common.DatabaseConnection;
import com.berrybyte.dashboard.MerchantMenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ConfirmDeleteDiscountPlanController {

    private int merchantId;
    private MerchantMenuController parentController;
    private AnchorPane overlayPane;

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public void setParentController(MerchantMenuController parentController) {
        this.parentController = parentController;
    }

    public void setOverlayPane(AnchorPane overlayPane) {
        this.overlayPane = overlayPane;
    }

    @FXML
    private void handleYes(ActionEvent event) {
        if (merchantId <= 0) {
            return;
        }
        try {
            deleteDiscountPlan(merchantId);
            if (parentController != null) {
                parentController.refreshMerchants();
            }
            closePopup(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNo(ActionEvent event) {
        closePopup(event);
    }

    private void deleteDiscountPlan(int merchantId) throws Exception {
        DatabaseConnection connectNow = new DatabaseConnection();

        String deactivateDiscountPlanSql = """
                UPDATE DiscountPlans
                SET IsActive = FALSE
                WHERE MerchantId = ? AND IsActive = TRUE
                """;

        try (Connection conn = connectNow.getConnection();
             PreparedStatement ps = conn.prepareStatement(deactivateDiscountPlanSql)) {

            ps.setInt(1, merchantId);
            ps.executeUpdate();

        }
    }

    private void closePopup(ActionEvent event) {
        if (overlayPane != null && parentController != null) {
            parentController.hideOverlay();
        } else {
            // fallback for old usage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }
}
