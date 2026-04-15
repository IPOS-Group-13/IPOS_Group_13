package com.berrybyte.pendingapplications;

import com.berrybyte.account.MerchantDraftSession;
import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class PendingApplicationsController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<PendingApplicationRow> merchantsTable;

    @FXML
    private TableColumn<PendingApplicationRow, String> nameColoumn;

    @FXML
    private TableColumn<PendingApplicationRow, String> companyColoumn;

    @FXML
    private TableColumn<PendingApplicationRow, String> iposIdColoumn;

    @FXML
    private TableColumn<PendingApplicationRow, String> discountPlanColoumn;

    @FXML
    private TableColumn<PendingApplicationRow, String> discountPlanColoumn1;

    @FXML
    private TableColumn<PendingApplicationRow, String> discountPlanColoumn2;

    @FXML
    private Label messageLabel;

    @FXML
    private AnchorPane profileMenuPane;

    private final PendingApplicationsService service = new PendingApplicationsService();

    @FXML
    public void initialize() {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }

        nameColoumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        companyColoumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        iposIdColoumn.setCellValueFactory(new PropertyValueFactory<>("companyRegistrationNumber"));
        discountPlanColoumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        discountPlanColoumn1.setCellValueFactory(new PropertyValueFactory<>("email"));
        discountPlanColoumn2.setCellValueFactory(new PropertyValueFactory<>("submissionDate"));

        loadApplications("");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        loadApplications(searchField.getText());
    }

    @FXML
    private void handleRejectApplication(ActionEvent event) {
        PendingApplicationRow selected = merchantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Please select an application to reject.");
            return;
        }

        try {
            service.rejectApplication(selected.getId());
            loadApplications(searchField.getText());
        } catch (Exception e) {
            messageLabel.setText("Failed to reject application: " + e.getMessage());
        }
    }

    @FXML
    private void handleAcceptApplication(ActionEvent event) {
        PendingApplicationRow selected = merchantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Please select an application to accept.");
            return;
        }

        MerchantDraftSession.saveDraft(
                selected.getName(),
                selected.getCompanyName(),
                "",
                "",
                selected.getPhoneNumber(),
                selected.getEmail(),
                selected.getAddress(),
                "NORMAL",
                "0"
        );
        MerchantDraftSession.setPreviousPage("pendingApplications");

        try {
            SceneSwitcher.switchScene(event, "/account/createMerchantAccount.fxml", "Create Merchant Account");
        } catch (Exception e) {
            messageLabel.setText("Failed to open merchant account form.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileClick() {
        if (profileMenuPane == null) {
            return;
        }

        boolean isVisible = profileMenuPane.isVisible();
        profileMenuPane.setVisible(!isVisible);
        profileMenuPane.setManaged(!isVisible);
        if (!isVisible) {
            profileMenuPane.toFront();
        }
    }

    @FXML
    private void handleLogoutMenuClick(ActionEvent event) {
        if (profileMenuPane != null) {
            profileMenuPane.setVisible(false);
            profileMenuPane.setManaged(false);
        }

        try {
            SceneSwitcher.switchScene(event, "/logout/logout.fxml", "Log Out");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open logout page.");
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
    private void handleCatalogueClick(ActionEvent event) {
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
    private void handleOrdersClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToOrderMenu(event);
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
    private void handlePaymentsClick(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToPaymentsMenu(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadApplications(String searchText) {
        try {
            merchantsTable.setItems(FXCollections.observableArrayList(service.getPendingApplications(searchText)));
            if (messageLabel != null) {
                messageLabel.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (messageLabel != null) {
                messageLabel.setText("Unable to load pending applications.");
            }
        }
    }
}
