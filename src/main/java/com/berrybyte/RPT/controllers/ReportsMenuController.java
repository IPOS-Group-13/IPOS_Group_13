package com.berrybyte.RPT.controllers;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ReportsMenuController {

    @FXML
    private TextField merchantSearchField;

    @FXML
    private DatePicker beforeDatePicker;

    @FXML
    private DatePicker afterDatePicker;

    @FXML
    private AnchorPane profileMenuPane;

    @FXML
    private TilePane reportsTilePane;

    @FXML
    public void initialize() {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }

        if (reportsTilePane != null) {
            reportsTilePane.widthProperty().addListener((observable, oldValue, newValue) ->
                    updateReportTileWidth(newValue.doubleValue()));
            Platform.runLater(() -> updateReportTileWidth(reportsTilePane.getWidth()));
        }
    }

    @FXML
    private void handleDashboardClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToDashboard(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCatalougeClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
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
    private void handleOrderClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToOrderMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePaymentsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToPaymentsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
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
    private void handlePendingClick(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/pendingapplications/pendingApplications.fxml", "Pending Applications");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReportsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToReportsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
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

    @FXML
    private void openStockTurnoverReport(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RPT/stockTurnoverReport.fxml"));
            Parent root = loader.load();

            StockTurnoverReportController controller = loader.getController();
            controller.setFilters(
                    safeDate(afterDatePicker),
                    safeDate(beforeDatePicker)
            );

            openLoadedRoot(event, root, "Stock Turnover Report");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openMerchantActivityReport(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RPT/merchantActivityReport.fxml"));
            Parent root = loader.load();

            MerchantActivityReportController controller = loader.getController();
            controller.setFilters(
                    safeTrim(merchantSearchField),
                    safeDate(afterDatePicker),
                    safeDate(beforeDatePicker)
            );

            openLoadedRoot(event, root, "Merchant Activity Report");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openLowStockReport(MouseEvent event) {
        openMousePage(event, "/RPT/lowStockReport.fxml", "Low Stock Report");
    }

    @FXML
    private void openInfoPharmaTurnoverReport(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RPT/infoPharmaTurnoverReport.fxml"));
            Parent root = loader.load();

            InfoPharmaTurnoverReportController controller = loader.getController();
            controller.setFilters(
                    safeDate(afterDatePicker),
                    safeDate(beforeDatePicker)
            );

            openLoadedRoot(event, root, "InfoPharma Turnover Report");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openMerchantOrderReport(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RPT/merchantOrderSummaryReport.fxml"));
            Parent root = loader.load();

            MerchantOrderSummaryReportController controller = loader.getController();
            controller.setFilters(
                    safeTrim(merchantSearchField),
                    safeDate(afterDatePicker),
                    safeDate(beforeDatePicker)
            );

            openLoadedRoot(event, root, "Merchant Order Report");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openInvoicesRaisedAgainstMerchant(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RPT/invoicesAgainstMerchant.fxml"));
            Parent root = loader.load();

            InvoicesAgainstMerchantController controller = loader.getController();
            controller.setFilters(
                    safeTrim(merchantSearchField),
                    safeDate(afterDatePicker),
                    safeDate(beforeDatePicker)
            );

            openLoadedRoot(event, root, "Invoices Raised Against Merchant");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openInvoicesRaisedByInfoPharma(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RPT/invoicesByInfoPharma.fxml"));
            Parent root = loader.load();

            InvoicesByInfoPharmaController  controller = loader.getController();
            controller.setFilters(
                    safeDate(afterDatePicker),
                    safeDate(beforeDatePicker)
            );

            openLoadedRoot(event, root, "Invoices Raised By InfoPharma");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMousePage(MouseEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            openLoadedRoot(event, root, title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openLoadedRoot(MouseEvent event, Parent root, String title) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneSwitcher.setStageRoot(stage, root, title);
    }

    private String safeTrim(TextField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    private LocalDate safeDate(DatePicker picker) {
        return picker == null ? null : picker.getValue();
    }

    private void updateReportTileWidth(double availableWidth) {
        if (availableWidth <= 0 || reportsTilePane == null) {
            return;
        }

        int columns = availableWidth >= 920 ? 3 : 2;
        reportsTilePane.setPrefColumns(columns);
        reportsTilePane.setPrefTileWidth(286.0);
    }
}
