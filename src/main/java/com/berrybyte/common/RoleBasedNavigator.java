package com.berrybyte.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class RoleBasedNavigator {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String MANAGER_ROLE = "MANAGER";
    private static final String MERCHANT_ROLE = "MERCHANT";

    private static final String ADMIN_DASHBOARD_PATH = "/dashboard/adminDashboard.fxml";
    private static final String MANAGER_DASHBOARD_PATH = "/dashboard/managerDashboard.fxml";
    private static final String STAFF_DASHBOARD_PATH = "/dashboard/staffDashboard.fxml";
    private static final String ADMIN_MERCHANTS_PATH = "/dashboard/merchantMenu.fxml";
    private static final String MANAGER_MERCHANTS_PATH = "/dashboard/managerMerchantMenu.fxml";
    private static final String STAFF_ACCOUNTS_PATH = "/dashboard/staffAccountsMenu.fxml";
    private static final String ORDER_MENU_PATH = "/dashboard/orderMenu.fxml";

    private RoleBasedNavigator() {
    }

    public static String getDashboardPath() {
        if (isManager()) {
            return MANAGER_DASHBOARD_PATH;
        }
        if (isStaff()) {
            return STAFF_DASHBOARD_PATH;
        }
        return ADMIN_DASHBOARD_PATH;
    }

    public static String getDashboardTitle() {
        if (isManager()) {
            return "Manager Dashboard";
        }
        if (isStaff()) {
            return "Staff Dashboard";
        }
        return "Admin Dashboard";
    }

    public static String getMerchantMenuPath() {
        return isManager() ? MANAGER_MERCHANTS_PATH : ADMIN_MERCHANTS_PATH;
    }

    public static String getMerchantMenuTitle() {
        return "Merchants";
    }

    public static String getStaffAccountsPath() {
        return STAFF_ACCOUNTS_PATH;
    }

    public static String getStaffAccountsTitle() {
        return "Staff Accounts";
    }

    public static String getOrderMenuPath() {
        return ORDER_MENU_PATH;
    }

    public static String getOrderMenuTitle() {
        return "Orders";
    }

    public static void switchToDashboard(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getDashboardPath(), getDashboardTitle());
    }

    public static void switchToMerchantMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getMerchantMenuPath(), getMerchantMenuTitle());
    }

    public static void switchToStaffAccounts(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getStaffAccountsPath(), getStaffAccountsTitle());
    }

    public static void switchToOrderMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getOrderMenuPath(), getOrderMenuTitle());
    }

    public static void openMerchantMenu(Node sourceNode) throws IOException {
        openScene(sourceNode, getMerchantMenuPath(), getMerchantMenuTitle());
    }

    public static void openDashboard(Node sourceNode) throws IOException {
        openScene(sourceNode, getDashboardPath(), getDashboardTitle());
    }

    public static void openStaffAccounts(Node sourceNode) throws IOException {
        openScene(sourceNode, getStaffAccountsPath(), getStaffAccountsTitle());
    }

    public static void openOrderMenu(Node sourceNode) throws IOException {
        openScene(sourceNode, getOrderMenuPath(), getOrderMenuTitle());
    }

    private static void openScene(Node sourceNode, String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(RoleBasedNavigator.class.getResource(fxmlPath));

        Stage stage = (Stage) sourceNode.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    private static boolean isManager() {
        return MANAGER_ROLE.equalsIgnoreCase(LoginSession.getCurrentRole());
    }

    private static boolean isStaff() {
        String role = LoginSession.getCurrentRole();
        return role != null
                && !ADMIN_ROLE.equalsIgnoreCase(role)
                && !MANAGER_ROLE.equalsIgnoreCase(role)
                && !MERCHANT_ROLE.equalsIgnoreCase(role);
    }
}
