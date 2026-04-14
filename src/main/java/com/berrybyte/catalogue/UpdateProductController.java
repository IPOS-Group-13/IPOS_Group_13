package com.berrybyte.catalogue;

import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class UpdateProductController {

    @FXML private TextField descriptionField;
    @FXML private TextField packageTypeField;
    @FXML private TextField unitField;
    @FXML private TextField unitsInPackField;
    @FXML private TextField packageCostField;
    @FXML private TextField availabilityField;
    @FXML private TextField stockLimitField;

    @FXML private Label messageLabel;

    private int itemId;

    private final CatalogueService catalogueService = new CatalogueService();

    public void setProduct(CatalogueItemRow item) {
        this.itemId = item.getItemId();

        descriptionField.setText(item.getDescription());
        packageTypeField.setText(item.getPackageType());
        unitField.setText(item.getUnit());
        unitsInPackField.setText(String.valueOf(item.getUnitsInPack()));
        packageCostField.setText(item.getPackageCost());
        availabilityField.setText(String.valueOf(item.getAvailabilityPacks()));
        stockLimitField.setText(String.valueOf(item.getStockLimitPacks()));
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            String description = descriptionField.getText().trim();
            String packageType = packageTypeField.getText().trim();
            String unit = unitField.getText().trim();

            int unitsInPack = Integer.parseInt(unitsInPackField.getText().trim());
            double packageCost = Double.parseDouble(packageCostField.getText().trim());
            int availability = Integer.parseInt(availabilityField.getText().trim());
            int stockLimit = Integer.parseInt(stockLimitField.getText().trim());

            catalogueService.updateProductDetails(
                    itemId,
                    description,
                    packageType,
                    unit,
                    unitsInPack,
                    packageCost,
                    availability,
                    stockLimit
            );

            SceneSwitcher.switchScene(event, "/catalogue/editProductDetails.fxml", "Edit Product Details");

        } catch (NumberFormatException e) {
            messageLabel.setText("Numeric fields must contain valid numbers.");
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Update failed.");
        }
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/catalogue/editProductDetails.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Product Details");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }
}
