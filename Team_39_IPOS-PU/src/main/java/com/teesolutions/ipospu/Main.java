package com.teesolutions.ipospu;

import com.teesolutions.ipospu.utils.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        scene.getStylesheets().add(Main.class.getResource("views/app.css").toExternalForm());
        stage.setTitle("IPOS-PU: Public Portal Storefront");
        stage.setScene(scene);
        stage.setMinWidth(880);
        stage.setMinHeight(560);
        stage.show();
    }

    @Override
    public void stop() {

        DatabaseManager.closeConnection();
    }

    public static void main(String[] args) {

        try (Connection ignored = DatabaseManager.getConnection()) {

        } catch (SQLException e) {
            throw new IllegalStateException("Database bootstrap failed", e);
        }
        launch();
    }
}