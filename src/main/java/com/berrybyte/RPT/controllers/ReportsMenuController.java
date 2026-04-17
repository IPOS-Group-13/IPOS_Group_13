package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.model.MerchantOption;
import com.berrybyte.RPT.repository.ReportRepository;
import com.berrybyte.RPT.repository.ReportRepositoryImpl;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ReportsMenuController {

    private final ReportRepository reportRepository = new ReportRepositoryImpl();

    @FXML
    private ComboBox<MerchantOption> merchantComboBox;

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

        loadMerchantOptions();

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
            SceneSwitcher.switchScene(event, "/account/pendingapplications/pendingApplications.fxml", "Pending Applications");
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
                    selectedMerchantId(),
                    selectedMerchantName(),
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
                    selectedMerchantId(),
                    selectedMerchantName(),
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
                    selectedMerchantId(),
                    selectedMerchantName(),
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

            InvoicesByInfoPharmaController controller = loader.getController();
            controller.setFilters(
                    safeDate(afterDatePicker),
                    safeDate(beforeDatePicker)
            );

            openLoadedRoot(event, root, "Invoices Raised By InfoPharma");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openOverdueBalanceReport(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RPT/overdueBalanceReport.fxml"));
            Parent root = loader.load();

            OverdueBalanceReportController controller = loader.getController();
            controller.setFilters(
                    selectedMerchantId(),
                    selectedMerchantName()
            );

            openLoadedRoot(event, root, "Overdue Balance Report");
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

    private LocalDate safeDate(DatePicker picker) {
        return picker == null ? null : picker.getValue();
    }

    private Integer selectedMerchantId() {
        MerchantOption selected = merchantComboBox == null ? null : merchantComboBox.getValue();
        return selected == null ? null : selected.getMerchantId();
    }

    private String selectedMerchantName() {
        MerchantOption selected = merchantComboBox == null ? null : merchantComboBox.getValue();
        return selected == null ? "" : selected.getCompanyName();
    }

    private void loadMerchantOptions() {
        try {
            if (merchantComboBox == null) {
                return;
            }

            List<MerchantOption> merchants = reportRepository.findMerchantOptions();
            merchantComboBox.setItems(FXCollections.observableArrayList(merchants));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
