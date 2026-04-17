package com.berrybyte.ORD.controllers;

import com.berrybyte.ORD.helpers.IncomingOrderRow;
import com.berrybyte.ORD.services.SaOrderService;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class IncomingOrdersController {

    private final SaOrderService orderService = new SaOrderService();

    @FXML
    private Label messageLabel;

    @FXML
    private ImageView backButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<IncomingOrderRow> incomingOrdersTable;

    @FXML
    private TableColumn<IncomingOrderRow, Integer> OrderID;

    @FXML
    private TableColumn<IncomingOrderRow, String> AccountHolder;

    @FXML
    private TableColumn<IncomingOrderRow, String> Date;

    @FXML
    private TableColumn<IncomingOrderRow, Double> Amount;

    @FXML
    private TableColumn<IncomingOrderRow, String> Status;

    @FXML
    public void initialize() {
        OrderID.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        AccountHolder.setCellValueFactory(new PropertyValueFactory<>("accountHolder"));
        Date.setCellValueFactory(new PropertyValueFactory<>("date"));
        Amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        Status.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadOrders("");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String query = searchField == null ? "" : searchField.getText();
        loadOrders(query);
    }

    @FXML
    private void viewDetailsButton(ActionEvent event) {
        IncomingOrderRow selectedOrder = incomingOrdersTable == null ? null : incomingOrdersTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            messageLabel.setText("Select an order first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ORD/orderDetails.fxml"));
            Parent root = loader.load();

            OrderDetailsController controller = loader.getController();
            controller.setOrderId(selectedOrder.getOrderId());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle("Order Details");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open order details.");
        }
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            RoleBasedNavigator.openOrderMenu((Node) event.getSource());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOrders(String query) {
        try {
            String keyword = query == null ? "" : query.trim();
            if (keyword.isEmpty()) {
                incomingOrdersTable.setItems(FXCollections.observableArrayList(orderService.getOrdersForReview()));
            } else {
                incomingOrdersTable.setItems(FXCollections.observableArrayList(orderService.searchOrdersForReview(keyword)));
            }
            messageLabel.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            incomingOrdersTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("Unable to load incoming orders.");
        }
    }
}
