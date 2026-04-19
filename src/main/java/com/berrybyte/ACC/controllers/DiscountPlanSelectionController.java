package com.berrybyte.ACC.controllers;

import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Represents discount plan selection controller.
 */
public class DiscountPlanSelectionController {

    @FXML
    private RadioButton fixedDiscountRadioButton;

    @FXML
    private RadioButton flexibleDiscountRadioButton;

    @FXML
    private Label messageLabel;

/**
 * Handles next.
 *
 * @param event event
 */
    @FXML
    private void handleNext(ActionEvent event) {
        try {
            if (fixedDiscountRadioButton.isSelected()) {
                SceneSwitcher.switchScene(event, "/account/fixedDiscount.fxml", "Fixed Discount");
            } else if (flexibleDiscountRadioButton.isSelected()) {
                SceneSwitcher.switchScene(event, "/account/flexibleDiscount.fxml", "Flexible Discount");
            } else {
                messageLabel.setText("Select one discount type");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open discount details");
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
            Parent root = FXMLLoader.load(getClass().getResource("/account/createMerchantAccount.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle("Create Merchant Account");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
