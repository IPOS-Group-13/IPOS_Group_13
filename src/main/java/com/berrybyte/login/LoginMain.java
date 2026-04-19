package com.berrybyte.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Represents login main.
 */
public class LoginMain extends Application {

/**
 * Performs start.
 * This method coordinates the main operation for this action.
 *
 * @param stage stage
 * @throws IOException when the operation fails
 */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginMain.class.getResource("/login/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("BerryByte");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
/**
 * Performs main.
 *
 * @param args args
 */

    public static void main(String[] args) {
        launch();
    }
}
