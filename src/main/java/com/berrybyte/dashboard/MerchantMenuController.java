package com.berrybyte.dashboard;

import com.berrybyte.ACC.controllers.ConfirmDeleteAccountController;
import com.berrybyte.ACC.controllers.ConfirmDeleteDiscountPlanController;
import com.berrybyte.ACC.services.DeleteAccountService;
import com.berrybyte.ACC.controllers.DiscountPlanEditController;
import com.berrybyte.ACC.services.MerchantStatusService;
import com.berrybyte.common.DatabaseConnection;
import com.berrybyte.common.LoginSession;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents merchant menu controller.
 */
public class MerchantMenuController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<MerchantMenuRow> merchantsTable;

    @FXML
    private TableColumn<MerchantMenuRow, String> nameColoumn;

    @FXML
    private TableColumn<MerchantMenuRow, String> companyColoumn;

    @FXML
    private TableColumn<MerchantMenuRow, String> iposIdColoumn;

    @FXML
    private TableColumn<MerchantMenuRow, String> creditLimitColoumn;

    @FXML
    private TableColumn<MerchantMenuRow, String> discountPlanColoumn;

    @FXML
    private TableColumn<MerchantMenuRow, String> outstandingColoumn;

    @FXML
    private TableColumn<MerchantMenuRow, String> accountStatusColoumn;

    @FXML
    private Button editMerchantDetailsButton;

    @FXML
    private Button updateDiscountPlanButton;

    @FXML
    private Button deleteDiscountPlanButton;

    @FXML
    private Button deleteAccount;

    @FXML
    private Button restoreStateButton;

    @FXML
    private Label messageLabel;

    @FXML
    private AnchorPane profileMenuPane;

    @FXML
    private AnchorPane overlayPane;

    private MerchantMenuRow selectedMerchant;
    private final DeleteAccountService deleteAccountService = new DeleteAccountService();
    private final MerchantStatusService merchantStatusService = new MerchantStatusService();

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        nameColoumn.setCellValueFactory(new PropertyValueFactory<>("merchantName"));
        companyColoumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        iposIdColoumn.setCellValueFactory(new PropertyValueFactory<>("iposAccountNumber"));
        creditLimitColoumn.setCellValueFactory(new PropertyValueFactory<>("creditLimit"));
        discountPlanColoumn.setCellValueFactory(new PropertyValueFactory<>("discountPlan"));
        if (outstandingColoumn != null) {
            outstandingColoumn.setCellValueFactory(new PropertyValueFactory<>("outstandingBalance"));
        }
        if (accountStatusColoumn != null) {
            accountStatusColoumn.setCellValueFactory(new PropertyValueFactory<>("accountStatus"));
        }

        profileMenuPane.setVisible(false);
        profileMenuPane.setManaged(false);

        if (overlayPane != null) {
            overlayPane.setVisible(false);
            overlayPane.setManaged(true);
        }

        merchantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedMerchant = newSelection;
            updateButtonState();
        });

        configureDeleteAccountButton();
        configureRestoreStateButton();
        setActionButtonsDisabled(true);
        loadMerchants("");
    }

/**
 * Handles search.
 *
 * @param event event
 */
    @FXML
    protected void handleSearch(ActionEvent event) {
        loadMerchants(searchField.getText());
    }

/**
 * Handles edit merchant details.
 *
 * @param event event
 */
    @FXML
    protected void handleEditMerchantDetails(ActionEvent event) {
        if (!ensureMerchantSelected()) {
            return;
        }

        openSceneForSelectedMerchant(
                event,
                "/account/editMerchantAccount.fxml",
                "Edit Merchant Details",
                "setUserId",
                selectedMerchant.getUserId()
        );
    }

/**
 * Handles staff accounts click.
 *
 * @param event event
 */
    @FXML
    protected void handleStaffAccountsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToManageAccounts(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles dashboard click.
 *
 * @param event event
 */
    @FXML
    protected void handleDashboardClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToDashboard(event);
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
    protected void handleCatalogueClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
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
    protected void handleMerchantsClick(ActionEvent event) {
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
    protected void handleOrdersClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToOrderMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Handles payments click.
 *
 * @param event event
 */
    @FXML
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
 * Handles update discount plan.
 *
 * @param event event
 */
    @FXML
    protected void handleUpdateDiscountPlan(ActionEvent event) {
        if (!ensureMerchantSelected()) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/account/editMerchantDiscountPlan.fxml"));
            Parent root = loader.load();

            DiscountPlanEditController controller = loader.getController();
            controller.setMerchantId(selectedMerchant.getMerchantId());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle("Edit Discount Plan");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open edit discount plan page.");
        }
    }

/**
 * Handles delete discount plan.
 *
 * @param event event
 */
    @FXML
    protected void handleDeleteDiscountPlan(ActionEvent event) {
        if (!ensureMerchantSelected()) {
            return;
        }
        if (!hasActiveDiscountPlan(selectedMerchant)) {
            messageLabel.setText("Discount plan is already deleted.");
            updateButtonState();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/account/confirmDeleteDiscountPlan.fxml"));
            Parent root = loader.load();

            ConfirmDeleteDiscountPlanController controller = loader.getController();
            controller.setMerchantId(selectedMerchant.getMerchantId());
            controller.setParentController(this);

            if (overlayPane != null) {
                controller.setOverlayPane(overlayPane);

                overlayPane.getChildren().clear();
                overlayPane.getChildren().add(root);
                root.setLayoutX((overlayPane.getWidth() - 324) / 2);
                root.setLayoutY((overlayPane.getHeight() - 200) / 2);
                overlayPane.setVisible(true);
            } else {
                Stage popupStage = new Stage();
                popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
                popupStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
                popupStage.setScene(new Scene(root));
                popupStage.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open delete discount plan confirmation.");
        }
    }

/**
 * Handles delete account.
 *
 * @param event event
 */
    @FXML
    protected void handleDeleteAccount(ActionEvent event) {
        if (!canDeleteMerchantAccounts()) {
            messageLabel.setText("Only admins can delete merchant accounts.");
            return;
        }

        if (!ensureMerchantSelected()) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/account/confirmDeleteAccount.fxml"));
            Parent root = loader.load();

            ConfirmDeleteAccountController controller = loader.getController();
            controller.setSelectedUser(deleteAccountService.getUserAccount(selectedMerchant.getUserId()));
            controller.setOnDeleteSuccess(this::refreshAfterAccountDelete);

            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open delete account confirmation.");
        }
    }

/**
 * Handles restore state.
 *
 * @param event event
 */
    @FXML
    protected void handleRestoreState(ActionEvent event) {
        if (!canRestoreMerchantState()) {
            messageLabel.setText("Only admins and managers can restore merchant account state.");
            return;
        }

        if (!ensureMerchantSelected()) {
            return;
        }

        if (!"IN_DEFAULT".equalsIgnoreCase(selectedMerchant.getAccountStatus())) {
            messageLabel.setText("Only accounts in default can be restored.");
            updateButtonState();
            return;
        }

        try {
            boolean restored = merchantStatusService.restoreDefaultState(selectedMerchant.getMerchantId());
            loadMerchants(searchField.getText());
            messageLabel.setText(restored
                    ? "Merchant account state restored to NORMAL."
                    : "Only accounts in default can be restored.");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to restore merchant account state.");
        }
    }


/**
 * Handles profile click.
 *
 */
    @FXML
    protected void handleProfileClick() {
        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
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
 * Handles logout menu click.
 *
 * @param event event
 */
    @FXML
    protected void handleLogoutMenuClick(ActionEvent event) {
        profileMenuPane.setVisible(false);
        profileMenuPane.setManaged(false);

        try {
            SceneSwitcher.switchScene(event, "/logout/logout.fxml", "Log Out");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
 * Loads merchants.
 *
 * @param searchText search text
 */

    private void loadMerchants(String searchText) {
        try {
            merchantsTable.setItems(FXCollections.observableArrayList(searchMerchants(searchText)));
            messageLabel.setText("");
            selectedMerchant = null;
            merchantsTable.getSelectionModel().clearSelection();
            updateButtonState();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load merchants.");
        }
    }
/**
 * Executes the refresh merchants workflow.
 * This method coordinates the main operation for this action.
 *
 */

    public void refreshMerchants() {
        loadMerchants(searchField.getText());
        messageLabel.setText("Discount plan deleted successfully.");
    }
/**
 * Executes the refresh after account delete workflow.
 * This method coordinates the main operation for this action.
 *
 */

    public void refreshAfterAccountDelete() {
        loadMerchants(searchField.getText());
        messageLabel.setText("Merchant account deleted successfully.");
    }
/**
 * Performs hide overlay.
 *
 */

    public void hideOverlay() {
        if (overlayPane != null) {
            overlayPane.setVisible(false);
            overlayPane.getChildren().clear();
        }
    }
/**
 * Executes the search merchants workflow.
 * This method coordinates the main operation for this action.
 *
 * @param searchText search text
 * @return result value
 * @throws Exception when the operation fails
 */

    private List<MerchantMenuRow> searchMerchants(String searchText) throws Exception {
        List<MerchantMenuRow> merchants = new ArrayList<>();

        String sql = """
                SELECT
                    ma.MerchantId,
                    ma.UserId,
                    u.Name AS MerchantName,
                    ma.CompanyName,
                    ma.IPOSAccountNumber,
                    ma.CreditLimit,
                    ma.OutstandingBalance,
                    COALESCE(ma.AccountStatus, 'NORMAL') AS AccountStatus,
                    COALESCE((
                        SELECT dp.PlanType
                        FROM DiscountPlans dp
                        WHERE dp.MerchantId = ma.MerchantId
                          AND dp.IsActive = 1
                        ORDER BY dp.DiscountPlanId DESC
                        LIMIT 1
                    ), 'NONE') AS PlanType
                FROM MerchantAccounts ma
                JOIN Users u ON ma.UserId = u.UserId
                WHERE u.Role = 'MERCHANT'
                  AND (u.Name LIKE ? OR ma.CompanyName LIKE ?)
                ORDER BY ma.CompanyName, ma.MerchantId
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String keyword = "%" + (searchText == null ? "" : searchText.trim()) + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    merchants.add(new MerchantMenuRow(
                            rs.getInt("MerchantId"),
                            rs.getInt("UserId"),
                            rs.getString("MerchantName"),
                            rs.getString("CompanyName"),
                            rs.getString("IPOSAccountNumber"),
                            String.format("%.2f", rs.getDouble("CreditLimit")),
                            rs.getString("PlanType"),
                            String.format("%.2f", rs.getDouble("OutstandingBalance")),
                            rs.getString("AccountStatus")
                    ));
                }
            }
        }

        return merchants;
    }
/**
 * Performs ensure merchant selected.
 *
 * @return result value
 */

    private boolean ensureMerchantSelected() {
        if (selectedMerchant == null) {
            messageLabel.setText("Select a merchant first.");
            return false;
        }
        return true;
    }
/**
 * Executes the update button state workflow.
 * This method coordinates the main operation for this action.
 *
 */

    private void updateButtonState() {
        boolean disabled = (selectedMerchant == null);
        setActionButtonsDisabled(disabled);
    }
/**
 * Sets action buttons disabled.
 *
 * @param disabled disabled
 */

    private void setActionButtonsDisabled(boolean disabled) {
        if (editMerchantDetailsButton != null) {
            editMerchantDetailsButton.setDisable(disabled);
        }
        if (updateDiscountPlanButton != null) {
            updateDiscountPlanButton.setDisable(disabled);
        }
        if (deleteDiscountPlanButton != null) {
            deleteDiscountPlanButton.setDisable(disabled
                    || selectedMerchant == null
                    || !hasActiveDiscountPlan(selectedMerchant));
        }
        if (deleteAccount != null) {
            deleteAccount.setDisable(disabled || !canDeleteMerchantAccounts());
        }
        if (restoreStateButton != null) {
            restoreStateButton.setDisable(disabled
                    || !canRestoreMerchantState()
                    || selectedMerchant == null
                    || !"IN_DEFAULT".equalsIgnoreCase(selectedMerchant.getAccountStatus()));
        }
    }
/**
 * Performs can delete merchant accounts.
 *
 * @return result value
 */

    protected boolean canDeleteMerchantAccounts() {
        return "ADMIN".equalsIgnoreCase(LoginSession.getCurrentRole());
    }
/**
 * Performs can restore merchant state.
 *
 * @return result value
 */

    protected boolean canRestoreMerchantState() {
        String role = LoginSession.getCurrentRole();
        return "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role);
    }
/**
 * Performs has active discount plan.
 *
 * @param merchant merchant
 * @return result value
 */

    private boolean hasActiveDiscountPlan(MerchantMenuRow merchant) {
        return merchant != null && !"NONE".equalsIgnoreCase(merchant.getDiscountPlan());
    }
/**
 * Performs configure delete account button.
 *
 */

    private void configureDeleteAccountButton() {
        if (deleteAccount == null) {
            return;
        }

        boolean canDeleteMerchantAccounts = canDeleteMerchantAccounts();
        deleteAccount.setVisible(canDeleteMerchantAccounts);
        deleteAccount.setManaged(canDeleteMerchantAccounts);
        deleteAccount.setDisable(true);
    }
/**
 * Performs configure restore state button.
 *
 */

    private void configureRestoreStateButton() {
        if (restoreStateButton == null) {
            return;
        }

        boolean canRestoreMerchantState = canRestoreMerchantState();
        restoreStateButton.setVisible(canRestoreMerchantState);
        restoreStateButton.setManaged(canRestoreMerchantState);
        restoreStateButton.setDisable(true);
    }
/**
 * Executes the open scene for selected merchant workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @param fxmlPath fxml path
 * @param title title
 * @param setterName setter name
 * @param value value
 */

    private void openSceneForSelectedMerchant(ActionEvent event,
                                              String fxmlPath,
                                              String title,
                                              String setterName,
                                              int value) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            Method setterMethod = controller.getClass().getMethod(setterName, int.class);
            setterMethod.invoke(controller, value);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.setStageRoot(stage, root);
            stage.setTitle(title);
            stage.show();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            messageLabel.setText("Target page must contain " + setterName + "(int).");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open " + title + ".");
        }
    }
}






