package com.berrybyte.common;

import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.IOException;

/**
 * Represents role based navigator.
 */
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
    private static final String REPORTS_MENU_PATH = "/RPT/reportsMenu.fxml";
/**
 * Creates a new RoleBasedNavigator instance.
 */

    private RoleBasedNavigator() {
    }
/**
 * Returns dashboard path.
 *
 * @return result value
 */

    public static String getDashboardPath() {
        if (isManager()) {
            return MANAGER_DASHBOARD_PATH;
        }
        if (isStaff()) {
            return STAFF_DASHBOARD_PATH;
        }
        return ADMIN_DASHBOARD_PATH;
    }
/**
 * Returns dashboard title.
 *
 * @return result value
 */

    public static String getDashboardTitle() {
        if (isManager()) {
            return "Manager Dashboard";
        }
        if (isStaff()) {
            return "Staff Dashboard";
        }
        return "Admin Dashboard";
    }
/**
 * Returns merchant menu path.
 *
 * @return result value
 */

    public static String getMerchantMenuPath() {
        return isManager() ? MANAGER_MERCHANTS_PATH : ADMIN_MERCHANTS_PATH;
    }
/**
 * Returns merchant menu title.
 *
 * @return result value
 */

    public static String getMerchantMenuTitle() {
        return "Merchants";
    }
/**
 * Returns manage accounts path.
 *
 * @return result value
 */

    public static String getManageAccountsPath() {
        return MANAGE_ACCOUNTS_PATH;
    }
/**
 * Returns manage accounts title.
 *
 * @return result value
 */

    public static String getManageAccountsTitle() {
        return "Manage Accounts";
    }
/**
 * Returns order menu path.
 *
 * @return result value
 */

    public static String getOrderMenuPath() {
        if (isManager()) {
            return MANAGER_ORDER_MENU_PATH;
        }
        if (isStaff()) {
            return STAFF_ORDER_MENU_PATH;
        }
        return ORDER_MENU_PATH;
    }
/**
 * Returns order menu title.
 *
 * @return result value
 */

    public static String getOrderMenuTitle() {
        return "Orders";
    }
/**
 * Returns catalogue path.
 *
 * @return result value
 */

    public static String getCataloguePath() {
        if (isManager()) {
            return MANAGER_CATALOGUE_PATH;
        }
        if (isStaff()) {
            return STAFF_CATALOGUE_PATH;
        }
        return CATALOGUE_PATH;
    }
/**
 * Returns catalogue title.
 *
 * @return result value
 */

    public static String getCatalogueTitle() {
        return "Catalogue Page";
    }
/**
 * Returns payments menu path.
 *
 * @return result value
 */

    public static String getPaymentsMenuPath() {
        if (isManager()) {
            return MANAGER_PAYMENTS_MENU_PATH;
        }
        if (isStaff()) {
            return STAFF_PAYMENTS_MENU_PATH;
        }
        return PAYMENTS_MENU_PATH;
    }
/**
 * Returns payments menu title.
 *
 * @return result value
 */

    public static String getPaymentsMenuTitle() {
        return "Payments";
    }
/**
 * Returns reports menu path.
 *
 * @return result value
 */

    public static String getReportsMenuPath() {
        return REPORTS_MENU_PATH;
    }
/**
 * Returns reports menu title.
 *
 * @return result value
 */

    public static String getReportsMenuTitle() {
        return "Reports";
    }
/**
 * Executes the switch to dashboard workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws IOException when the operation fails
 */

    public static void switchToDashboard(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getDashboardPath(), getDashboardTitle());
    }
/**
 * Executes the switch to merchant menu workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws IOException when the operation fails
 */

    public static void switchToMerchantMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getMerchantMenuPath(), getMerchantMenuTitle());
    }
/**
 * Executes the switch to manage accounts workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws IOException when the operation fails
 */

    public static void switchToManageAccounts(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getManageAccountsPath(), getManageAccountsTitle());
    }
/**
 * Executes the switch to order menu workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws IOException when the operation fails
 */

    public static void switchToOrderMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getOrderMenuPath(), getOrderMenuTitle());
    }
/**
 * Executes the switch to catalogue workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws IOException when the operation fails
 */

    public static void switchToCatalogue(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getCataloguePath(), getCatalogueTitle());
    }
/**
 * Executes the switch to payments menu workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws IOException when the operation fails
 */

    public static void switchToPaymentsMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getPaymentsMenuPath(), getPaymentsMenuTitle());
    }
/**
 * Executes the switch to reports menu workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @throws IOException when the operation fails
 */

    public static void switchToReportsMenu(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, getReportsMenuPath(), getReportsMenuTitle());
    }
/**
 * Executes the open merchant menu workflow.
 * This method coordinates the main operation for this action.
 *
 * @param sourceNode source node
 * @throws IOException when the operation fails
 */

    public static void openMerchantMenu(Node sourceNode) throws IOException {
        openScene(sourceNode, getMerchantMenuPath(), getMerchantMenuTitle());
    }
/**
 * Executes the open dashboard workflow.
 * This method coordinates the main operation for this action.
 *
 * @param sourceNode source node
 * @throws IOException when the operation fails
 */

    public static void openDashboard(Node sourceNode) throws IOException {
        openScene(sourceNode, getDashboardPath(), getDashboardTitle());
    }
/**
 * Executes the open manage accounts workflow.
 * This method coordinates the main operation for this action.
 *
 * @param sourceNode source node
 * @throws IOException when the operation fails
 */

    public static void openManageAccounts(Node sourceNode) throws IOException {
        openScene(sourceNode, getManageAccountsPath(), getManageAccountsTitle());
    }
/**
 * Executes the open order menu workflow.
 * This method coordinates the main operation for this action.
 *
 * @param sourceNode source node
 * @throws IOException when the operation fails
 */

    public static void openOrderMenu(Node sourceNode) throws IOException {
        openScene(sourceNode, getOrderMenuPath(), getOrderMenuTitle());
    }
/**
 * Executes the open catalogue workflow.
 * This method coordinates the main operation for this action.
 *
 * @param sourceNode source node
 * @throws IOException when the operation fails
 */

    public static void openCatalogue(Node sourceNode) throws IOException {
        openScene(sourceNode, getCataloguePath(), getCatalogueTitle());
    }
/**
 * Executes the open payments menu workflow.
 * This method coordinates the main operation for this action.
 *
 * @param sourceNode source node
 * @throws IOException when the operation fails
 */

    public static void openPaymentsMenu(Node sourceNode) throws IOException {
        openScene(sourceNode, getPaymentsMenuPath(), getPaymentsMenuTitle());
    }
/**
 * Executes the open reports menu workflow.
 * This method coordinates the main operation for this action.
 *
 * @param sourceNode source node
 * @throws IOException when the operation fails
 */

    public static void openReportsMenu(Node sourceNode) throws IOException {
        openScene(sourceNode, getReportsMenuPath(), getReportsMenuTitle());
    }
/**
 * Executes the open scene workflow.
 * This method coordinates the main operation for this action.
 *
 * @param sourceNode source node
 * @param fxmlPath fxml path
 * @param title title
 * @throws IOException when the operation fails
 */

    private static void openScene(Node sourceNode, String fxmlPath, String title) throws IOException {
        SceneSwitcher.switchScene(sourceNode, fxmlPath, title);
    }
/**
 * Returns whether manager.
 *
 * @return true when condition is met
 */

    private static boolean isManager() {
        return MANAGER_ROLE.equalsIgnoreCase(LoginSession.getCurrentRole());
    }
/**
 * Returns whether staff.
 *
 * @return true when condition is met
 */

    private static boolean isStaff() {
        String role = LoginSession.getCurrentRole();
        return role != null
                && !ADMIN_ROLE.equalsIgnoreCase(role)
                && !MANAGER_ROLE.equalsIgnoreCase(role)
                && !MERCHANT_ROLE.equalsIgnoreCase(role);
    }
}
