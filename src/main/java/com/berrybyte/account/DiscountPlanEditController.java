package com.berrybyte.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DiscountPlanEditController {

    @FXML
    private RadioButton fixedDiscountRadioButton;

    @FXML
    private RadioButton flexibleDiscountRadioButton;

    @FXML
    private Label messageLabel;

    private final ToggleGroup discountToggleGroup = new ToggleGroup();

    private int merchantId;

    @FXML
    public void initialize() {
        fixedDiscountRadioButton.setToggleGroup(discountToggleGroup);
        flexibleDiscountRadioButton.setToggleGroup(discountToggleGroup);
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    @FXML
    private void handleNext(ActionEvent event) {
        try {
            messageLabel.setText("");

            if (!fixedDiscountRadioButton.isSelected() && !flexibleDiscountRadioButton.isSelected()) {
                messageLabel.setText("Select one discount type.");
                return;
            }
            if (merchantId <= 0) {
                messageLabel.setText("Merchant ID is missing.");
                return;
            }
            if (fixedDiscountRadioButton.isSelected()) {
                openSceneWithMerchantId(
                        event,
                        "/account/editFixedDiscount.fxml",
                        "Update Fixed Discount",
                        merchantId
                );
            } else {
                openSceneWithMerchantId(
                        event,
                        "/account/editFlexibleDiscount.fxml",
                        "Update Flexible Discount",
                        merchantId
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open discount details.");
        }
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard/merchantDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Merchants");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }

    private void openSceneWithMerchantId(ActionEvent event, String fxmlPath, String title, int merchantId) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Object controller = loader.getController();
        controller.getClass().getMethod("setMerchantId", int.class).invoke(controller, merchantId);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}