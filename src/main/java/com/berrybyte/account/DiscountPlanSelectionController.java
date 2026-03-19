package com.berrybyte.account;

import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

public class DiscountPlanSelectionController {

    @FXML
    private RadioButton fixedDiscountRadioButton;

    @FXML
    private RadioButton flexibleDiscountRadioButton;

    @FXML
    private Label messageLabel;

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
}
