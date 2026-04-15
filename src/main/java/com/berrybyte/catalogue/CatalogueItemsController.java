package com.berrybyte.catalogue;

import com.berrybyte.common.RoleBasedNavigator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CatalogueItemsController {
    @FXML
    private TextField searchField;

    @FXML
    private TableView<CatalogueItemRow> merchantsTable;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> nameColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> companyColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> iposIdColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> creditLimitColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> discountPlanColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> discountPlanColoumn1;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> discountPlanColoumn2;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> discountPlanColoumn3;

    @FXML
    private Label messageLabel;

    private final CatalogueService catalogueService = new CatalogueService();

    @FXML
    public void initialize() {
        nameColoumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        companyColoumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        iposIdColoumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        creditLimitColoumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        discountPlanColoumn.setCellValueFactory(new PropertyValueFactory<>("unitsInPack"));
        discountPlanColoumn1.setCellValueFactory(new PropertyValueFactory<>("packageCost"));
        discountPlanColoumn2.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        discountPlanColoumn3.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));

        loadItems("");
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            RoleBasedNavigator.switchToCatalogue(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        loadItems(searchField.getText());
    }

    @FXML
    private void handleProfileClick() {
        System.out.println("Profile clicked");
    }

    private void loadItems(String searchText) {
        try {
            merchantsTable.setItems(FXCollections.observableArrayList(catalogueService.searchCatalogueItems(searchText)));
            if (messageLabel != null) {
                messageLabel.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (messageLabel != null) {
                messageLabel.setText("Unable to load catalogue items.");
            }
        }
    }
}
