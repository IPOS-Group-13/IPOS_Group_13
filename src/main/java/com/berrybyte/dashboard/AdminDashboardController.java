package com.berrybyte.dashboard;

import com.berrybyte.catalogue.CatalogueService;
import com.berrybyte.catalogue.LowStockItemRow;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents admin dashboard controller.
 */
public class AdminDashboardController {

    @FXML
    private AnchorPane profileMenuPane;

    @FXML
    private AnchorPane lowStockPane;

    @FXML
    private TableView<LowStockItemRow> lowStockTable;

    @FXML
    private TableColumn<LowStockItemRow, Integer> itemIdColumn;

    @FXML
    private TableColumn<LowStockItemRow, String> descriptionColumn;

    @FXML
    private TableColumn<LowStockItemRow, Integer> availabilityColumn;

    @FXML
    private TableColumn<LowStockItemRow, Integer> stockLimitColumn;

    @FXML
    private AnchorPane overdueAccountsPane;

    @FXML
    private TableView<OverdueAccountAlertRow> overdueAccountsTable;

    @FXML
    private TableColumn<OverdueAccountAlertRow, Integer> overdueMerchantIdColumn;

    @FXML
    private TableColumn<OverdueAccountAlertRow, String> overdueCompanyColumn;

    @FXML
    private TableColumn<OverdueAccountAlertRow, String> overdueAccountNumberColumn;

    @FXML
    private TableColumn<OverdueAccountAlertRow, LocalDate> overdueDueDateColumn;

    @FXML
    private TableColumn<OverdueAccountAlertRow, Integer> overdueDaysColumn;

    @FXML
    private TableColumn<OverdueAccountAlertRow, BigDecimal> overdueAmountColumn;

    @FXML
    private TableColumn<OverdueAccountAlertRow, String> overdueStatusColumn;

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        profileMenuPane.setVisible(false);
        profileMenuPane.setManaged(false);
        lowStockPane.setVisible(false);
        lowStockPane.setManaged(false);
        overdueAccountsPane.setVisible(false);
        overdueAccountsPane.setManaged(false);

        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        stockLimitColumn.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));

        overdueMerchantIdColumn.setCellValueFactory(new PropertyValueFactory<>("merchantId"));
        overdueCompanyColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        overdueAccountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("iposAccountNumber"));
        overdueDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("oldestDueDate"));
        overdueDaysColumn.setCellValueFactory(new PropertyValueFactory<>("daysOverdue"));
        overdueAmountColumn.setCellValueFactory(new PropertyValueFactory<>("overdueAmount"));
        overdueStatusColumn.setCellValueFactory(new PropertyValueFactory<>("accountState"));

        configureOverdueRowHighlighting();
        loadLowStockAlert();
        loadOverdueAccountAlerts();
    }
/**
 * Loads low stock alert.
 *
 */

    private void loadLowStockAlert() {
        try {
            CatalogueService catalogueService = new CatalogueService();
            List<LowStockItemRow> lowStockItems = catalogueService.getLowStockItems();

            if (lowStockItems.isEmpty()) {
                return;
            }

            lowStockTable.setItems(FXCollections.observableArrayList(lowStockItems));
            lowStockPane.setVisible(true);
            lowStockPane.setManaged(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
 * Loads overdue account alerts.
 *
 */

    private void loadOverdueAccountAlerts() {
        try {
            OverdueAccountAlertService overdueService = new OverdueAccountAlertService();
            List<OverdueAccountAlertRow> overdueRows = overdueService.loadAlertsAndQueueReminders(LocalDate.now());

            if (overdueRows.isEmpty()) {
                return;
            }

            overdueAccountsTable.setItems(FXCollections.observableArrayList(overdueRows));
            overdueAccountsPane.setVisible(true);
            overdueAccountsPane.setManaged(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
 * Performs configure overdue row highlighting.
 *
 */

    private void configureOverdueRowHighlighting() {
        overdueAccountsTable.setRowFactory(table -> new TableRow<>() {
/**
 * Executes the update item workflow.
 * This method coordinates the main operation for this action.
 *
 * @param item item
 * @param empty empty
 */
            @Override
            protected void updateItem(OverdueAccountAlertRow item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("");

                if (empty || item == null) {
                    return;
                }

                if ("DEFAULT".equalsIgnoreCase(item.getAlertLevel())) {
                    setStyle("-fx-background-color: #F6C9C9;");
                } else if ("REMINDER".equalsIgnoreCase(item.getAlertLevel())) {
                    setStyle("-fx-background-color: #FFE1B8;");
                }
            }
        });
    }

/**
 * Handles dismiss low stock alert.
 *
 * @param event event
 */
    @FXML
    private void handleDismissLowStockAlert(ActionEvent event) {
        lowStockPane.setVisible(false);
        lowStockPane.setManaged(false);
    }

/**
 * Handles profile click.
 *
 */
    @FXML
    private void handleProfileClick() {
        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
        }
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
        }
    }

/**
 * Handles pending applications.
 *
 * @param event event
 */
    @FXML
    private void handlePendingApplications(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/account/pendingapplications/pendingApplications.fxml", "Manage Catalogue Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
 * Handles payments click.
 *
 * @param event event
 */


    public void handlePaymentsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToPaymentsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles reports click.
 *
 * @param event event
 */
    @FXML
    public void handleReportsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToReportsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles logout menu click.
 *
 * @param event event
 */
    @FXML
    private void handleLogoutMenuClick(ActionEvent event) {
        profileMenuPane.setVisible(false);
        profileMenuPane.setManaged(false);

        try {
            SceneSwitcher.switchScene(event, "/logout/logout.fxml", "Log Out");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

