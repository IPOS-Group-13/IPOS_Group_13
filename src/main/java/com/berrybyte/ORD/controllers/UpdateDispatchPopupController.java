package com.berrybyte.ORD.controllers;

import com.berrybyte.ORD.services.SaOrderService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents update dispatch popup controller.
 */
public class UpdateDispatchPopupController {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private final SaOrderService orderService = new SaOrderService();

    private int orderId;
    private Runnable onDispatchUpdated;

    @FXML
    private DatePicker expectedDatePicker;

    @FXML
    private TextField expectedTimeField;

    @FXML
    private TextField courierNameField;

    @FXML
    private TextField courierRefField;

    @FXML
    private Label messageLabel;
/**
 * Sets order id.
 *
 * @param orderId order id
 */

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
/**
 * Sets on dispatch updated.
 *
 * @param onDispatchUpdated on dispatch updated
 */

    public void setOnDispatchUpdated(Runnable onDispatchUpdated) {
        this.onDispatchUpdated = onDispatchUpdated;
    }

/**
 * Handles update.
 *
 * @param event event
 */
    @FXML
    private void handleUpdate(ActionEvent event) {
        String courierName = trim(courierNameField);
        String courierRef = trim(courierRefField);

        if (courierName.isBlank() || courierRef.isBlank() || trim(expectedTimeField).isBlank() || expectedDatePicker == null || expectedDatePicker.getValue() == null) {
            messageLabel.setText("All fields are required.");
            return;
        }

        LocalDateTime dispatchedDateTime = LocalDateTime.now();
        LocalDateTime expectedDeliveryDateTime = buildExpectedDeliveryDateTime();

        if (expectedDeliveryDateTime == null) {
            messageLabel.setText("Expected time must be in HH:mm format.");
            return;
        }

        if (!expectedDeliveryDateTime.isAfter(dispatchedDateTime)) {
            messageLabel.setText("Expected delivery date and time must be after dispatch time.");
            return;
        }

        try {
            boolean updated = orderService.updateDispatchDetails(
                    orderId,
                    courierName,
                    courierRef,
                    dispatchedDateTime,
                    expectedDeliveryDateTime,
                    "DISPATCHED");

            if (!updated) {
                messageLabel.setText("Order update failed.");
                return;
            }

            if (onDispatchUpdated != null) {
                onDispatchUpdated.run();
            }
            closePopup(event);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to update dispatch details.");
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
 * Handles cancel.
 *
 * @param event event
 */
    @FXML
    private void handleCancel(ActionEvent event) {
        closePopup(event);
    }
/**
 * Performs parse time.
 *
 * @param value value
 * @return result value
 */

    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
/**
 * Performs build expected delivery date time.
 *
 * @return result value
 */

    private LocalDateTime buildExpectedDeliveryDateTime() {
        LocalDate expectedDate = expectedDatePicker.getValue();
        LocalTime expectedTime = parseTime(trim(expectedTimeField));

        if (expectedDate == null || expectedTime == null) {
            return null;
        }

        return LocalDateTime.of(expectedDate, expectedTime);
    }
/**
 * Performs trim.
 *
 * @param field field
 * @return result value
 */

    private String trim(TextField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
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
