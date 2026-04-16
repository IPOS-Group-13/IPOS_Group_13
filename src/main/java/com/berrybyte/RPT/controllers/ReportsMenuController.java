package com.berrybyte.RPT.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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
    private void handleDashboardClick(ActionEvent event) {
        openSimplePage(event, "/dashboard/managerDashboard.fxml", "Dashboard");
    }

    @FXML
    private void handleCatalougeClick(ActionEvent event) {
        openSimplePage(event, "/catalogue/catalogue.fxml", "Catalogue");
    }

    @FXML
    private void handleMerchantsClick(ActionEvent event) {
        openSimplePage(event, "/dashboard/merchantMenu.fxml", "Merchants");
    }

    @FXML
    private void handleOrderClick(ActionEvent event) {
        openSimplePage(event, "/dashbord/orderMenu.fxml", "Orders");
    }

    @FXML
    private void handleStaffAccountsClick(ActionEvent event) {
        openSimplePage(event, "/dashboard/staffAccounts.fxml", "Manage Accounts");
    }

    @FXML
    private void handlePendingClick(ActionEvent event) {
        openSimplePage(event, "/account/pendingApplications.fxml", "Pending Applications");
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        System.out.println("Profile clicked.");
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

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Stock Turnover Report");
            stage.show();
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

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Merchant Activity Report");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openLowStockReport(MouseEvent event) {
        openSimpleMousePage(event, "/RPT/lowStockReport.fxml", "Low Stock Report");
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

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("InfoPharma Turnover Report");
            stage.show();
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

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Merchant Order Report");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openInvoicesRaisedAgainstMerchant(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RPT/invoiceListingReport.fxml"));
            Parent root = loader.load();

            InvoicesAgainstMerchantController controller = loader.getController();
            controller.setFilters(
                    safeTrim(merchantSearchField),
                    safeDate(afterDatePicker),
                    safeDate(beforeDatePicker)
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Invoices Raised Against Merchant");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openInvoicesRaisedByInfoPharma(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RPT/invoiceListingReport.fxml"));
            Parent root = loader.load();

            InvoicesByInfoPharmaController  controller = loader.getController();
            controller.setFilters(
                    safeDate(afterDatePicker),
                    safeDate(beforeDatePicker)
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Invoices Raised By InfoPharma");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openSimplePage(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openSimpleMousePage(MouseEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String safeTrim(TextField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    private LocalDate safeDate(DatePicker picker) {
        return picker == null ? null : picker.getValue();
    }
}