package com.berrybyte.ORD.controllers;

import com.berrybyte.ORD.helpers.OrderDetails;
import com.berrybyte.ORD.helpers.OrderLine;
import com.berrybyte.ORD.Status.AcceptOrderStatus;
import com.berrybyte.ORD.services.SaOrderService;
import com.berrybyte.common.LoginSession;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;

/**
 * Represents order details controller.
 */
public class OrderDetailsController {

    private final SaOrderService orderService = new SaOrderService();

    private int orderId;

    @FXML
    private ImageView backButton;

    @FXML
    private TableView<OrderLine> orderListTable;

    @FXML
    private TableColumn<OrderLine, Integer> productIdColumn;

    @FXML
    private TableColumn<OrderLine, String> descriptionColumn;

    @FXML
    private TableColumn<OrderLine, String> packageTypeColumn;

    @FXML
    private TableColumn<OrderLine, Integer> unitsInPacksColumn;

    @FXML
    private TableColumn<OrderLine, Double> packsCostColumn;

    @FXML
    private TableColumn<OrderLine, Integer> quantityColumn;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Button acceptOrderButton;

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        packageTypeColumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        unitsInPacksColumn.setCellValueFactory(new PropertyValueFactory<>("unitsInPacks"));
        packsCostColumn.setCellValueFactory(new PropertyValueFactory<>("packsCost"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orderListTable.setItems(FXCollections.observableArrayList());
        totalAmountLabel.setText("GBP 0.00");
    }
/**
 * Sets order id.
 *
 * @param orderId order id
 */

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        loadOrder();
    }

/**
 * Handles back button.
 *
 * @param event event
 */
    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ORD/incomingOrders.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle("Incoming Orders");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unable to return to incoming orders.");
        }
    }

/**
 * Handles accept order.
 *
 */
    @FXML
    private void handleAcceptOrder() {
        Integer staffUserId = LoginSession.getCurrentUserId();
        if (staffUserId == null) {
            showAlert(Alert.AlertType.ERROR, "No logged-in staff user was found.");
            return;
        }
        try {
            AcceptOrderStatus status = orderService.acceptOrder(orderId, staffUserId);
            if (status == AcceptOrderStatus.SUCCESS) {
                try {
                    Path pdfPath = orderService.generateInvoicePdfForOrder(orderId);
                    try {
                        String recipientEmail = orderService.queueOrderAcceptedEmailForOrder(orderId, pdfPath);
                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Order accepted.\n\nInvoice PDF generated at:\n"
                                        + pdfPath.toAbsolutePath()
                                        + "\n\nOrder-accepted email queued for:\n"
                                        + recipientEmail
                        );
                    } catch (Exception queueException) {
                        queueException.printStackTrace();
                        String failureReason = extractRootCauseMessage(queueException);
                        showDetailedAlert(
                                Alert.AlertType.WARNING,
                                "Order accepted and invoice PDF generated at:\n"
                                        + pdfPath.toAbsolutePath()
                                        + "\n\nBut the invoice could not be uploaded and queued for email delivery.",
                                failureReason
                        );
                    }
                } catch (Exception pdfException) {
                    pdfException.printStackTrace();
                    showAlert(Alert.AlertType.WARNING, "Order accepted, but the invoice PDF could not be generated, so no queue email was created. You can retry from Orders Summary.");
                }
                loadOrder();
                return;
            }

            showAlert(Alert.AlertType.ERROR, mapAcceptStatus(status));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unable to accept the order.");
        }
    }
/**
 * Loads order.
 *
 */

    private void loadOrder() {
        if (orderId <= 0) {
            return;
        }
        try {
            OrderDetails details = orderService.getOrderDetails(orderId);
            if (details == null) {
                showAlert(Alert.AlertType.ERROR, "Order not found.");
                return;
            }
            List<OrderLine> items = orderService.getOrderItems(orderId);
            orderListTable.setItems(FXCollections.observableArrayList(items));
            totalAmountLabel.setText(String.format("GBP %.2f", details.getTotalAmount()));

            boolean canAccept = "NEW".equalsIgnoreCase(details.getStatus());
            acceptOrderButton.setVisible(canAccept);
            acceptOrderButton.setManaged(canAccept);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unable to load order details.");
        }
    }
/**
 * Performs map accept status.
 *
 * @param status status
 * @return result value
 */

    private String mapAcceptStatus(AcceptOrderStatus status) {
        return switch (status) {
            case ORDER_NOT_FOUND -> "The order no longer exists.";
            case ORDER_ALREADY_ACCEPTED -> "This order is no longer in NEW status.";
            case MERCHANT_SUSPENDED -> "The merchant account is suspended.";
            case MERCHANT_IN_DEFAULT -> "The merchant account is in default.";
            case INSUFFICIENT_STOCK -> "There is not enough stock to accept this order.";
            case INVOICE_GENERATION_FAILED -> "The invoice could not be generated.";
            case ERROR -> "The order could not be accepted.";
            case SUCCESS -> "Order accepted successfully.";
        };
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
 * Performs show detailed alert.
 * This method coordinates the main operation for this action.
 *
 * @param type type
 * @param summary summary
 * @param details details
 */

    private void showDetailedAlert(Alert.AlertType type, String summary, String details) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(summary);
        alert.setResizable(true);

        TextArea detailsArea = new TextArea(details);
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setMaxWidth(Double.MAX_VALUE);
        detailsArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(detailsArea, Priority.ALWAYS);
        GridPane.setHgrow(detailsArea, Priority.ALWAYS);

        GridPane expandableContent = new GridPane();
        expandableContent.setMaxWidth(Double.MAX_VALUE);
        expandableContent.add(detailsArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expandableContent);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }
/**
 * Performs extract root cause message.
 *
 * @param throwable throwable
 * @return result value
 */

    private String extractRootCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }

        String message = current.getMessage();
        if (message == null || message.isBlank()) {
            return current.getClass().getSimpleName();
        }
        return message;
    }
}
