package com.berrybyte.ORD.controllers;

import com.berrybyte.ORD.services.SaOrderService;
import com.berrybyte.common.LoginSession;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

/**
 * Represents record payment popup controller.
 */
public class RecordPaymentPopupController {

    private final SaOrderService orderService = new SaOrderService();

    private Runnable onPaymentRecorded;

    @FXML
    private TextField orderIdField;

    @FXML
    private TextField amountPaidField;

    @FXML
    private ComboBox<String> paymentMethodComboBox;

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        if (orderIdField != null) {
            orderIdField.setEditable(false);
        }

        if (paymentMethodComboBox != null) {
            paymentMethodComboBox.setItems(FXCollections.observableArrayList(
                    "Bank",
                    "Card",
                    "PayPal",
                    "Other"
            ));
        }

        if (amountPaidField != null) {
            UnaryOperator<TextFormatter.Change> numericFilter = change -> {
                String newText = change.getControlNewText();
                return newText.matches("\\d*(\\.\\d{0,2})?") ? change : null;
            };
            amountPaidField.setTextFormatter(new TextFormatter<>(numericFilter));
        }
    }
/**
 * Sets order id.
 *
 * @param orderId order id
 */

    public void setOrderId(int orderId) {
        if (orderIdField != null) {
            orderIdField.setText(String.valueOf(orderId));
        }
    }
/**
 * Sets on payment recorded.
 *
 * @param onPaymentRecorded on payment recorded
 */

    public void setOnPaymentRecorded(Runnable onPaymentRecorded) {
        this.onPaymentRecorded = onPaymentRecorded;
    }

/**
 * Handles confirm payment.
 *
 * @param event event
 */
    @FXML
    private void handleConfirmPayment(ActionEvent event) {
        try {
            int orderId = parseOrderId();
            BigDecimal amountPaid = parseAmountPaid();
            String paymentMethod = paymentMethodComboBox == null ? "" : paymentMethodComboBox.getValue();
            Integer recordedByUserId = LoginSession.getCurrentUserId();

            if (recordedByUserId == null || recordedByUserId <= 0) {
                showAlert(Alert.AlertType.ERROR, "No logged-in user was found.");
                return;
            }

            orderService.recordPayment(orderId, amountPaid, paymentMethod, recordedByUserId);

            if (onPaymentRecorded != null) {
                onPaymentRecorded.run();
            }
            closePopup(event);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unable to record payment.");
        }
    }

/**
 * Handles close.
 *
 * @param event event
 */
    @FXML
    private void handleClose(ActionEvent event) {
        closePopup(event);
    }
/**
 * Performs parse order id.
 *
 * @return result value
 */

    private int parseOrderId() {
        String orderIdText = orderIdField == null || orderIdField.getText() == null ? "" : orderIdField.getText().trim();

        if (orderIdText.isEmpty()) {
            throw new IllegalArgumentException("Select an order first.");
        }
        try {
            return Integer.parseInt(orderIdText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid order ID.");
        }
    }
/**
 * Performs parse amount paid.
 *
 * @return result value
 */

    private BigDecimal parseAmountPaid() {
        String amountText = amountPaidField == null || amountPaidField.getText() == null ? "" : amountPaidField.getText().trim();

        if (amountText.isEmpty()) {
            throw new IllegalArgumentException("Enter a payment amount.");
        }

        try {
            return new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount paid must be numeric.");
        }
    }
/**
 * Performs show alert.
 *
 * @param type type
 * @param message message
 */

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
/**
 * Performs close popup.
 *
 * @param event event
 */

    private void closePopup(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
