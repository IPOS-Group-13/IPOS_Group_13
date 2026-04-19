package com.berrybyte.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Represents scene switcher.
 */
public class SceneSwitcher {
/**
 * Executes the switch scene workflow.
 * This method coordinates the main operation for this action.
 *
 * @param event event
 * @param fxmlPath fxml path
 * @param title title
 * @throws IOException when the operation fails
 */

    public static void switchScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        setStageRoot(stage, root, title);
    }
/**
 * Executes the switch scene workflow.
 * This method coordinates the main operation for this action.
 *
 * @param sourceNode source node
 * @param fxmlPath fxml path
 * @param title title
 * @throws IOException when the operation fails
 */

    public static void switchScene(Node sourceNode, String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));

        Stage stage = (Stage) sourceNode.getScene().getWindow();
        setStageRoot(stage, root, title);
    }
/**
 * Performs set stage root.
 * This method coordinates the main operation for this action.
 *
 * @param stage stage
 * @param root root
 * @param title title
 */

    public static void setStageRoot(Stage stage, Parent root, String title) {
        setStageRoot(stage, root);
        stage.setTitle(title);
        stage.show();
    }
/**
 * Performs set stage root.
 *
 * @param stage stage
 * @param root root
 */

    public static void setStageRoot(Stage stage, Parent root) {
        boolean wasMaximized = stage.isMaximized();
        stage.setResizable(true);

        Scene currentScene = stage.getScene();
        if (currentScene == null) {
            stage.setScene(new Scene(root));
            currentScene = stage.getScene();
        } else {
            currentScene.setRoot(root);
        }

        applyPreferredWindowSize(stage, currentScene, root, wasMaximized);

        if (wasMaximized) {
            stage.setMaximized(true);
        }
    }
/**
 * Performs apply preferred window size.
 * This method coordinates the main operation for this action.
 *
 * @param stage stage
 * @param scene scene
 * @param root root
 * @param wasMaximized was maximized
 */

    private static void applyPreferredWindowSize(Stage stage, Scene scene, Parent root, boolean wasMaximized) {
        double preferredWidth = root.prefWidth(-1);
        double preferredHeight = root.prefHeight(preferredWidth);

        if (!isUsableSize(preferredWidth) || !isUsableSize(preferredHeight)) {
            return;
        }

        double decorationWidth = getDecorationSize(stage.getWidth(), scene.getWidth());
        double decorationHeight = getDecorationSize(stage.getHeight(), scene.getHeight());

        stage.setMinWidth(preferredWidth + decorationWidth);
        stage.setMinHeight(preferredHeight + decorationHeight);

        if (!wasMaximized && (scene.getWidth() < preferredWidth || scene.getHeight() < preferredHeight)) {
            stage.sizeToScene();
        }
    }
/**
 * Performs is usable size.
 *
 * @param value value
 * @return result value
 */

    private static boolean isUsableSize(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value) && value > 0;
    }
/**
 * Performs get decoration size.
 *
 * @param stageSize stage size
 * @param sceneSize scene size
 * @return result value
 */

    private static double getDecorationSize(double stageSize, double sceneSize) {
        if (!isUsableSize(stageSize) || !isUsableSize(sceneSize)) {
            return 0;
        }

        return Math.max(0, stageSize - sceneSize);
    }
}
