package com.berrybyte.ORD.controllers;

import com.berrybyte.ORD.DTO.OrderSummaryRow;
import com.berrybyte.ORD.services.SaOrderService;
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
    private TableColumn<OrderSummaryRow, String> orderedDateColumn;

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
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderedDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderedDate"));
        dispatchedDateColumn.setCellValueFactory(new PropertyValueFactory<>("dispatchedDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColoumn.setCellValueFactory(new PropertyValueFactory<>("deliveredStatus"));
        paidColumn.setCellValueFactory(new PropertyValueFactory<>("paidStatus"));
        courierNameColumn.setCellValueFactory(new PropertyValueFactory<>("courierName"));
        courierRefColumn.setCellValueFactory(new PropertyValueFactory<>("courierRef"));
        expectedDeliveryColumn.setCellValueFactory(new PropertyValueFactory<>("expectedDelivery"));
        refreshOrdersSummary();
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField == null ? "" : searchField.getText();
        loadOrdersSummary(keyword);
    }

    @FXML
    private void updateDispatchDetails(ActionEvent event) {
        OrderSummaryRow selectedOrder = ordersSummaryTable == null
                ? null
                : ordersSummaryTable.getSelectionModel().getSelectedItem();

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

    @FXML
    private void recordPayment(ActionEvent event) {
        messageLabel.setText("Record payment is not wired up yet.");
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard/orderMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Orders");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }

    public void refreshOrdersSummary() {
        loadOrdersSummary(searchField == null ? "" : searchField.getText());
    }

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
