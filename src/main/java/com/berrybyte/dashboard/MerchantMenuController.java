package com.berrybyte.dashboard;

import com.berrybyte.account.ConfirmDeleteAccountController;
import com.berrybyte.account.ConfirmDeleteDiscountPlanController;
import com.berrybyte.account.DeleteAccountService;
import com.berrybyte.account.DiscountPlanEditController;
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
    private Button editMerchantDetailsButton;

    @FXML
    private Button updateDiscountPlanButton;

    @FXML
    private Button deleteDiscountPlanButton;

    @FXML
    private Button deleteAccount;

    @FXML
    private Label messageLabel;

    @FXML
    private AnchorPane profileMenuPane;

    @FXML
    private AnchorPane overlayPane;

    private MerchantMenuRow selectedMerchant;
    private final DeleteAccountService deleteAccountService = new DeleteAccountService();

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
        setActionButtonsDisabled(true);
        loadMerchants("");
    }

    @FXML
    protected void handleSearch(ActionEvent event) {
        loadMerchants(searchField.getText());
    }

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

    @FXML
    protected void handleStaffAccountsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToManageAccounts(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleDashboardClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToDashboard(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleCatalogueClick(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/Catalogue.fxml", "Catalogue Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleMerchantsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToMerchantMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleOrdersClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToOrderMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Discount Plan");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open edit discount plan page.");
        }
    }

    @FXML
    protected void handleDeleteDiscountPlan(ActionEvent event) {
        if (!ensureMerchantSelected()) {
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


    @FXML
    protected void handleProfileClick() {
        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
    }

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

    public void refreshMerchants() {
        loadMerchants(searchField.getText());
        messageLabel.setText("Discount plan deleted successfully.");
    }

    public void refreshAfterAccountDelete() {
        loadMerchants(searchField.getText());
        messageLabel.setText("Merchant account deleted successfully.");
    }

    public void hideOverlay() {
        if (overlayPane != null) {
            overlayPane.setVisible(false);
            overlayPane.getChildren().clear();
        }
    }

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
                            String.format("%.2f", rs.getDouble("OutstandingBalance"))
                    ));
                }
            }
        }

        return merchants;
    }

    private boolean ensureMerchantSelected() {
        if (selectedMerchant == null) {
            messageLabel.setText("Select a merchant first.");
            return false;
        }
        return true;
    }

    private void updateButtonState() {
        boolean disabled = (selectedMerchant == null);
        setActionButtonsDisabled(disabled);
    }

    private void setActionButtonsDisabled(boolean disabled) {
        if (editMerchantDetailsButton != null) {
            editMerchantDetailsButton.setDisable(disabled);
        }
        if (updateDiscountPlanButton != null) {
            updateDiscountPlanButton.setDisable(disabled);
        }
        if (deleteDiscountPlanButton != null) {
            deleteDiscountPlanButton.setDisable(disabled);
        }
        if (deleteAccount != null) {
            deleteAccount.setDisable(disabled || !canDeleteMerchantAccounts());
        }
    }

    protected boolean canDeleteMerchantAccounts() {
        return "ADMIN".equalsIgnoreCase(LoginSession.getCurrentRole());
    }

    private void configureDeleteAccountButton() {
        if (deleteAccount == null) {
            return;
        }

        boolean canDeleteMerchantAccounts = canDeleteMerchantAccounts();
        deleteAccount.setVisible(canDeleteMerchantAccounts);
        deleteAccount.setManaged(canDeleteMerchantAccounts);
        deleteAccount.setDisable(true);
    }

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
            stage.setScene(new Scene(root));
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
