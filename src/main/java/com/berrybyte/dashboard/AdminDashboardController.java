package com.berrybyte.dashboard;

import com.berrybyte.catalogue.CatalogueService;
import com.berrybyte.catalogue.LowStockItemRow;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.util.List;

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
    public void initialize() {
        profileMenuPane.setVisible(false);
        profileMenuPane.setManaged(false);

        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        stockLimitColumn.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));

        loadLowStockAlert();
    }

    private void loadLowStockAlert() {
        try {
            CatalogueService catalogueService = new CatalogueService();
            List<LowStockItemRow> lowStockItems = catalogueService.getLowStockItems();

            if (lowStockItems.isEmpty()) {
                return;
            }

            lowStockTable.setItems(FXCollections.observableArrayList(lowStockItems));
            lowStockPane.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDismissLowStockAlert(ActionEvent event) {
        lowStockPane.setVisible(false);
    }

    @FXML
    private void handleProfileClick() {
        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
        }
    }

    @FXML
    private void handleStaffAccountsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToManageAccounts(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMerchantsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToMerchantMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOrdersClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToOrderMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCatalogueClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePaymentsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToPaymentsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
