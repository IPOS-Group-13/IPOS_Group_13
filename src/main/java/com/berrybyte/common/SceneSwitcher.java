package com.berrybyte.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static void switchScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}