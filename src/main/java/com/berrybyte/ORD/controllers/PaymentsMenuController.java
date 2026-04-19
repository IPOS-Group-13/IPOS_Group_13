package com.berrybyte.ORD.controllers;

import com.berrybyte.ORD.helpers.PaymentRequestRow;
import com.berrybyte.ORD.services.PaymentRequestService;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.util.List;
import java.util.Locale;

/**
 * Represents payments menu controller.
 */
public class PaymentsMenuController {

    private final PaymentRequestService paymentRequestService = new PaymentRequestService();

    @FXML
    private AnchorPane profileMenuPane;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<PaymentRequestRow> paymentsTable;

    @FXML
    private TableColumn<PaymentRequestRow, Long> orderIdColumn;

    @FXML
    private TableColumn<PaymentRequestRow, String> merchantEmailColumn;

    @FXML
    private TableColumn<PaymentRequestRow, String> amountColumn;

    @FXML
    private TableColumn<PaymentRequestRow, String> paymentTypeColumn;

    @FXML
    private TableColumn<PaymentRequestRow, String> statusColumn;

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }

        if (orderIdColumn != null) {
            orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        }
        if (merchantEmailColumn != null) {
            merchantEmailColumn.setCellValueFactory(new PropertyValueFactory<>("merchantEmail"));
        }
        if (amountColumn != null) {
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        }
        if (paymentTypeColumn != null) {
            paymentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }

        loadPayments("");
    }

/**
 * Handles profile click.
 *
 */
    @FXML
    private void handleProfileClick() {
        if (profileMenuPane == null) {
            return;
        }

        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
        }
    }

/**
 * Handles dashboard click.
 *
 * @param event event
 */
    @FXML
    private void handleDashboardClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToDashboard(event);
        } catch (Exception e) {
            e.printStackTrace();
            setMessage("Unable to open dashboard.");
        }
    }

/**
 * Handles catalogue click.
 *
 * @param event event
 */
    @FXML
    private void handleCatalogueClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
        } catch (Exception e) {
            e.printStackTrace();
            setMessage("Unable to open catalogue.");
        }
    }

/**
 * Handles pending applications.
 *
 * @param event event
 */
    @FXML
    public void handlePendingApplications(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/account/pendingapplications/pendingApplications.fxml", "Pending Applications Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles merchants click.
 *
 * @param event event
 */
    @FXML
    private void handleMerchantsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToMerchantMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
            setMessage("Unable to open merchants.");
        }
    }

/**
 * Handles orders click.
 *
 * @param event event
 */
    @FXML
    private void handleOrdersClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToOrderMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
            setMessage("Unable to open orders.");
        }
    }

/**
 * Handles payments click.
 *
 * @param event event
 */
    @FXML
    private void handlePaymentsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToPaymentsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
            setMessage("Unable to open payments.");
        }
    }

/**
 * Handles reports click.
 *
 * @param event event
 */
    @FXML
    private void handleReportsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToReportsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
            setMessage("Unable to open reports.");
        }
    }

/**
 * Handles search.
 *
 * @param event event
 */
    @FXML
    private void handleSearch(ActionEvent event) {
        loadPayments(searchField == null ? "" : searchField.getText());
    }

/**
 * Handles staff accounts click.
 *
 * @param event event
 */
    @FXML
    private void handleStaffAccountsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToManageAccounts(event);
        } catch (Exception e) {
            e.printStackTrace();
            setMessage("Unable to open manage accounts.");
        }
    }

/**
 * Handles logout menu click.
 *
 * @param event event
 */
    @FXML
    private void handleLogoutMenuClick(ActionEvent event) {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }

        try {
            SceneSwitcher.switchScene(event, "/logout/logout.fxml", "Log Out");
        } catch (Exception e) {
            e.printStackTrace();
            setMessage("Unable to log out.");
        }
    }
/**
 * Loads payments.
 *
 * @param searchText search text
 */

    private void loadPayments(String searchText) {
        try {
            paymentsTable.setItems(FXCollections.observableArrayList(
                    filterPayments(paymentRequestService.getPaymentRequests(), searchText)));
            setMessage("");
        } catch (Exception e) {
            e.printStackTrace();
            if (paymentsTable != null) {
                paymentsTable.setItems(FXCollections.observableArrayList());
            }
            setMessage("Unable to load payment requests.");
        }
    }
/**
 * Performs filter payments.
 *
 * @param rows rows
 * @param searchText search text
 * @return result value
 */

    private List<PaymentRequestRow> filterPayments(List<PaymentRequestRow> rows, String searchText) {
        String keyword = searchText == null ? "" : searchText.trim().toLowerCase(Locale.ROOT);
        if (keyword.isEmpty()) {
            return rows;
        }

        return rows.stream()
                .filter(row -> String.valueOf(row.getOrderId()).contains(keyword)
                        || contains(row.getMerchantEmail(), keyword)
                        || contains(row.getAmount(), keyword)
                        || contains(row.getPaymentType(), keyword)
                        || contains(row.getStatus(), keyword)).toList();
    }
/**
 * Performs contains.
 *
 * @param value value
 * @param keyword keyword
 * @return result value
 */

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }
/**
 * Sets message.
 *
 * @param message message
 */

    private void setMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message == null ? "" : message);
        }
    }
}

