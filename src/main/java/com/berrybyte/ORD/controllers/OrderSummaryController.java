package com.berrybyte.ORD.controllers;

import com.berrybyte.ORD.helpers.OrderSummaryRow;
import com.berrybyte.ORD.services.SaOrderService;
import com.berrybyte.common.RoleBasedNavigator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDateTime;

/**
 * Represents order summary controller.
 */
public class OrderSummaryController {

    private final SaOrderService orderService = new SaOrderService();

    @FXML
    private ImageView backButton;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<OrderSummaryRow> ordersSummaryTable;

    @FXML
    private TableColumn<OrderSummaryRow, Integer> orderIdColumn;

    @FXML
    private TableColumn<OrderSummaryRow, String> merchantNameColumn;

    @FXML
    private TableColumn<OrderSummaryRow, String> dispatchedDateColumn;

    @FXML
    private TableColumn<OrderSummaryRow, Double> amountColumn;

    @FXML
    private TableColumn<OrderSummaryRow, String> statusColoumn;

    @FXML
    private TableColumn<OrderSummaryRow, String> paidColumn;

    @FXML
    private TableColumn<OrderSummaryRow, String> courierNameColumn;

    @FXML
    private TableColumn<OrderSummaryRow, String> courierRefColumn;

    @FXML
    private TableColumn<OrderSummaryRow, String> expectedDeliveryColumn;

    @FXML
    private TableColumn<OrderSummaryRow, String> deliveryDateColumn;

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        merchantNameColumn.setCellValueFactory(new PropertyValueFactory<>("merchantName"));
        dispatchedDateColumn.setCellValueFactory(new PropertyValueFactory<>("dispatchedDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColoumn.setCellValueFactory(new PropertyValueFactory<>("deliveredStatus"));
        paidColumn.setCellValueFactory(new PropertyValueFactory<>("paidStatus"));
        if (courierNameColumn != null) {
            courierNameColumn.setCellValueFactory(new PropertyValueFactory<>("courierName"));
        }
        courierRefColumn.setCellValueFactory(new PropertyValueFactory<>("courierRef"));
        expectedDeliveryColumn.setCellValueFactory(new PropertyValueFactory<>("expectedDelivery"));
        deliveryDateColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        refreshOrdersSummary();
    }

/**
 * Handles search.
 *
 * @param event event
 */
    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField == null ? "" : searchField.getText();
        loadOrdersSummary(keyword);
    }

/**
 * Executes the update dispatch details workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 */
    @FXML
    private void updateDispatchDetails(ActionEvent event) {
        OrderSummaryRow selectedOrder = ordersSummaryTable == null ? null : ordersSummaryTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            messageLabel.setText("Select an order first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ORD/updateOrderDeliveryDetails.fxml"));
            Parent root = loader.load();

            UpdateDispatchPopupController controller = loader.getController();
            controller.setOrderId(selectedOrder.getOrderId());
            controller.setOnDispatchUpdated(this::refreshOrdersSummary);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.initStyle(StageStyle.UTILITY);
            popupStage.setTitle("Update Dispatch Details");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open update dispatch details popup.");
        }
    }

/**
 * Executes the update delivery status workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 */
    @FXML
    private void updateDeliveryStatus(ActionEvent event) {
        OrderSummaryRow selectedOrder = ordersSummaryTable == null ? null : ordersSummaryTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            messageLabel.setText("Select an order first.");
            return;
        }

        String currentStatus = selectedOrder.getDeliveredStatus() == null ? "" : selectedOrder.getDeliveredStatus().trim().toUpperCase();

        if ("DELIVERED".equals(currentStatus)) {
            messageLabel.setText("This order has already been marked as delivered.");
            return;
        }

        if (!"DISPATCHED".equals(currentStatus)) {
            messageLabel.setText("Only dispatched orders can be marked as delivered.");
            return;
        }

        try {
            boolean updated = orderService.markOrderAsDelivered(selectedOrder.getOrderId(), LocalDateTime.now());

            if (!updated) {
                messageLabel.setText("Unable to mark the order as delivered.");
                return;
            }

            refreshOrdersSummary();
            messageLabel.setText("Order marked as delivered.");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to mark the order as delivered.");
        }
    }

/**
 * Performs view invoices button.
 *
 * @param event event
 */
    @FXML
    private void viewInvoicesButton(ActionEvent event) {
        OrderSummaryRow selectedOrder = ordersSummaryTable == null ? null : ordersSummaryTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            messageLabel.setText("Select an order first.");
            return;
        }

        try {
            orderService.openInvoicePdfForOrder(selectedOrder.getOrderId());
            messageLabel.setText("");
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage() == null || e.getMessage().isBlank()
                    ? "Unable to open invoice PDF."
                    : e.getMessage());
        }
    }

/**
 * Executes the record payment workflow.
 *
 * @param event event
 */
    @FXML
    private void recordPayment(ActionEvent event) {
        OrderSummaryRow selectedOrder = ordersSummaryTable == null ? null : ordersSummaryTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            messageLabel.setText("Select an order first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ORD/recordPaymentPopUp.fxml"));
            Parent root = loader.load();

            RecordPaymentPopupController controller = loader.getController();
            controller.setOrderId(selectedOrder.getOrderId());
            controller.setOnPaymentRecorded(() -> {
                refreshOrdersSummary();
                messageLabel.setText("Payment recorded successfully.");
            });

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.initStyle(StageStyle.UTILITY);
            popupStage.setTitle("Record Payment");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open record payment popup.");
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
            RoleBasedNavigator.openOrderMenu((Node) event.getSource());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }
/**
 * Executes the refresh orders summary workflow.
 * This method coordinates the main operation for this action.
 *
 */

    public void refreshOrdersSummary() {
        loadOrdersSummary(searchField == null ? "" : searchField.getText());
    }
/**
 * Loads orders summary.
 *
 * @param keyword keyword
 */

    private void loadOrdersSummary(String keyword) {
        try {
            String searchText = keyword == null ? "" : keyword.trim();
            if (searchText.isEmpty()) {
                ordersSummaryTable.setItems(FXCollections.observableArrayList(orderService.getOrdersSummary()));
            } else {
                ordersSummaryTable.setItems(FXCollections.observableArrayList(orderService.searchOrdersSummary(searchText)));
            }
            messageLabel.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            ordersSummaryTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("Unable to load orders summary.");
        }
    }
}
