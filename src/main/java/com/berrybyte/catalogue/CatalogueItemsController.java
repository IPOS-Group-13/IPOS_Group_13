package com.berrybyte.catalogue;

import com.berrybyte.common.RoleBasedNavigator;
import com.berrybyte.common.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class CatalogueItemsController {


    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/catalogue/Catalogue.fxml", "Catalogue Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        System.out.println("Search Button Clicked");
    }

    @FXML
    private void handleProfileClick() {
        System.out.println("Profile clicked");
    }

}
