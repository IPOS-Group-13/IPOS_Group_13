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

/**
 * Represents confirm delete discount plan controller.
 */
public class ConfirmDeleteDiscountPlanController {

    private int merchantId;
    private MerchantMenuController parentController;
    private AnchorPane overlayPane;
/**
 * Sets merchant id.
 *
 * @param merchantId merchant id
 */

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }
/**
 * Sets parent controller.
 *
 * @param parentController parent controller
 */

    public void setParentController(MerchantMenuController parentController) {
        this.parentController = parentController;
    }
/**
 * Sets overlay pane.
 *
 * @param overlayPane overlay pane
 */

    public void setOverlayPane(AnchorPane overlayPane) {
        this.overlayPane = overlayPane;
    }

/**
 * Handles yes.
 *
 * @param event event
 */
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

/**
 * Handles no.
 *
 * @param event event
 */
    @FXML
    private void handleNo(ActionEvent event) {
        closePopup(event);
    }
/**
 * Executes the delete discount plan workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @throws Exception when the operation fails
 */

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
/**
 * Performs close popup.
 *
 * @param event event
 */

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
