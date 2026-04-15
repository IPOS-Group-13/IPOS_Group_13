package com.berrybyte.common;

import javafx.event.ActionEvent;
import javafx.scene.Node;

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
    private static final String MANAGE_ACCOUNTS_PATH = "/dashboard/manageAccounts.fxml";
    private static final String ORDER_MENU_PATH = "/dashboard/orderMenu.fxml";
    private static final String MANAGER_ORDER_MENU_PATH = "/dashboard/managerOrderMenu.fxml";
    private static final String STAFF_ORDER_MENU_PATH = "/dashboard/staffOrderMenu.fxml";
    private static final String CATALOGUE_PATH = "/catalogue/catalogue.fxml";
    private static final String MANAGER_CATALOGUE_PATH = "/catalogue/managerCatalogue.fxml";
    private static final String STAFF_CATALOGUE_PATH = "/catalogue/staffCatalogue.fxml";
    private static final String PAYMENTS_MENU_PATH = "/ORD/paymentsMenu.fxml";
    private static final String MANAGER_PAYMENTS_MENU_PATH = "/ORD/managerPaymentsMenu.fxml";
    private static final String STAFF_PAYMENTS_MENU_PATH = "/ORD/staffPaymentsMenu.fxml";

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

    public static String getManageAccountsPath() {
        return MANAGE_ACCOUNTS_PATH;
    }

    public static String getManageAccountsTitle() {
        return "Manage Accounts";
    }

    public static String getOrderMenuPath() {
        if (isManager()) {
            return MANAGER_ORDER_MENU_PATH;
        }
        if (isStaff()) {
            return STAFF_ORDER_MENU_PATH;
        }
        return ORDER_MENU_PATH;
    }

    public static String getOrderMenuTitle() {
        return "Orders";
    }

    public static String getCataloguePath() {
        if (isManager()) {
            return MANAGER_CATALOGUE_PATH;
        }
        if (isStaff()) {
            return STAFF_CATALOGUE_PATH;
        }
        return CATALOGUE_PATH;
    }

    public static String getCatalogueTitle() {
        return "Catalogue Page";
    }

    public static String getPaymentsMenuPath() {
        if (isManager()) {
            return MANAGER_PAYMENTS_MENU_PATH;
        }
        if (isStaff()) {
            return STAFF_PAYMENTS_MENU_PATH;
        }
        return PAYMENTS_MENU_PATH;
    }

    public static String getPaymentsMenuTitle() {
        return "Payments";
    }

    public static void switchToDashboard(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getDashboardPath(), getDashboardTitle());
    }

    public static void switchToMerchantMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getMerchantMenuPath(), getMerchantMenuTitle());
    }

    public static void switchToManageAccounts(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getManageAccountsPath(), getManageAccountsTitle());
    }

    public static void switchToOrderMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getOrderMenuPath(), getOrderMenuTitle());
    }

    public static void switchToCatalogue(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getCataloguePath(), getCatalogueTitle());
    }

    public static void switchToPaymentsMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getPaymentsMenuPath(), getPaymentsMenuTitle());
    }

    public static void openMerchantMenu(Node sourceNode) throws IOException {
        openScene(sourceNode, getMerchantMenuPath(), getMerchantMenuTitle());
    }

    public static void openDashboard(Node sourceNode) throws IOException {
        openScene(sourceNode, getDashboardPath(), getDashboardTitle());
    }

    public static void openManageAccounts(Node sourceNode) throws IOException {
        openScene(sourceNode, getManageAccountsPath(), getManageAccountsTitle());
    }

    public static void openOrderMenu(Node sourceNode) throws IOException {
        openScene(sourceNode, getOrderMenuPath(), getOrderMenuTitle());
    }

    public static void openCatalogue(Node sourceNode) throws IOException {
        openScene(sourceNode, getCataloguePath(), getCatalogueTitle());
    }

    public static void openPaymentsMenu(Node sourceNode) throws IOException {
        openScene(sourceNode, getPaymentsMenuPath(), getPaymentsMenuTitle());
    }

    private static void openScene(Node sourceNode, String fxmlPath, String title) throws IOException {
        SceneSwitcher.switchScene(sourceNode, fxmlPath, title);
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
