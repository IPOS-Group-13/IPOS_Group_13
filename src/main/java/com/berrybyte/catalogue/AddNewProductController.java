package com.berrybyte.catalogue;

import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AddNewProductController {
    private static final String DESCRIPTION_REGEX = "^[A-Za-z0-9 ,.&()'/-]{2,100}$";
    private static final String PACKAGE_TYPE_REGEX = "^[A-Za-z0-9 .&()'/-]{2,50}$";
    private static final String UNIT_REGEX = "^[A-Za-z]{1,20}$";
    private static final String WHOLE_NUMBER_REGEX = "^\\d+$";
    private static final String MONEY_REGEX = "^\\d+(\\.\\d{1,2})?$";

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField companyNameTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField phoneNumberTextField1;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField addressTextArea;

    @FXML
    private Button nextButton;

    @FXML
    private ImageView backButton;

    @FXML
    private Label messageLabel;

    private final CatalogueService catalogueService = new CatalogueService();

    @FXML
    public void initialize() {
        messageLabel.setText("");
    }

    @FXML
    private void handleNext(ActionEvent event) {
        String description = nameTextField.getText() == null ? "" : nameTextField.getText().trim();
        String packageType = companyNameTextField.getText() == null ? "" : companyNameTextField.getText().trim();
        String unit = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
        String unitsInPack = phoneNumberTextField1.getText() == null ? "" : phoneNumberTextField1.getText().trim();
        String packageCost = phoneNumberTextField.getText() == null ? "" : phoneNumberTextField.getText().trim();
        String availability = emailTextField.getText() == null ? "" : emailTextField.getText().trim();
        String stockLimit = addressTextArea.getText() == null ? "" : addressTextArea.getText().trim();

        if (description.isEmpty() || packageType.isEmpty() || unit.isEmpty()
                || unitsInPack.isEmpty() || packageCost.isEmpty()
                || availability.isEmpty() || stockLimit.isEmpty()) {
            messageLabel.setText("Please fill in all product fields.");
            return;
        }
        if (!description.matches(DESCRIPTION_REGEX)) {
            messageLabel.setText("Description contains invalid characters.");
            return;
        }
        if (!packageType.matches(PACKAGE_TYPE_REGEX)) {
            messageLabel.setText("Package type contains invalid characters.");
            return;
        }
        if (!unit.matches(UNIT_REGEX)) {
            messageLabel.setText("Unit must contain letters only.");
            return;
        }
        if (!unitsInPack.matches(WHOLE_NUMBER_REGEX)) {
            messageLabel.setText("Units in a pack must be a whole number.");
            return;
        }
        if (!packageCost.matches(MONEY_REGEX)) {
            messageLabel.setText("Package cost must be a valid amount.");
            return;
        }
        if (!availability.matches(WHOLE_NUMBER_REGEX)) {
            messageLabel.setText("Availability must be a whole number.");
            return;
        }
        if (!stockLimit.matches(WHOLE_NUMBER_REGEX)) {
            messageLabel.setText("Stock limit must be a whole number.");
            return;
        }

        try {
            catalogueService.createProduct(
                    description,
                    packageType,
                    unit,
                    Integer.parseInt(unitsInPack),
                    Double.parseDouble(packageCost),
                    Integer.parseInt(availability),
                    Integer.parseInt(stockLimit)
            );

            SceneSwitcher.switchScene(event, "/catalogue/catalogueItems.fxml", "Catalogue Items Page");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage() == null ? "Unable to add product." : e.getMessage());
        }
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/catalogue/manageCatalogue.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Manage Catalogue Page");
            SceneSwitcher.setStageRoot(stage, root);
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Unable to open Manage Catalogue page.");
            e.printStackTrace();
        }
    }
}
