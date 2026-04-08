package com.berrybyte.catalogue;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DeleteProductController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<CatalogueItemRow> productsTable;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> itemIdColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> descriptionColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> packageTypeColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> unitColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> unitsInPackColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, String> packageCostColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> availabilityColoumn;

    @FXML
    private TableColumn<CatalogueItemRow, Integer> stockLimitColoumn;

    @FXML
    private Label messageLabel;

    private final DeleteProductService deleteProductService = new DeleteProductService();
    private CatalogueItemRow selectedProduct;

    @FXML
    public void initialize() {
        itemIdColoumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        descriptionColoumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        packageTypeColoumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        unitColoumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitsInPackColoumn.setCellValueFactory(new PropertyValueFactory<>("unitsInPack"));
        packageCostColoumn.setCellValueFactory(new PropertyValueFactory<>("packageCost"));
        availabilityColoumn.setCellValueFactory(new PropertyValueFactory<>("availabilityPacks"));
        stockLimitColoumn.setCellValueFactory(new PropertyValueFactory<>("stockLimitPacks"));

        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedProduct = newSelection;
        });

        loadProducts("");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        loadProducts(searchField.getText());
    }

    @FXML
    private void handleDeleteProduct(ActionEvent event) {
        if (selectedProduct == null) {
            messageLabel.setText("Select a product first.");
            return;
        }

        try {
            deleteProductService.deleteProduct(selectedProduct.getItemId());
            refreshProducts();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage() == null ? "Unable to delete product." : e.getMessage());
        }
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/catalogue/manageCatalogue.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Catalogue Page");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }

    public void refreshProducts() {
        loadProducts(searchField.getText());
        productsTable.getSelectionModel().clearSelection();
        selectedProduct = null;
        messageLabel.setText("Product deleted successfully.");
    }

    private void loadProducts(String searchText) {
        try {
            productsTable.setItems(FXCollections.observableArrayList(
                    deleteProductService.searchProducts(searchText)
            ));
            messageLabel.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load products.");
        }
    }
}
