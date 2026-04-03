package com.berrybyte.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class MerchantMenuNavigation {

    private static final String ADMIN_MERCHANT_MENU_PATH = "/dashboard/merchantMenu.fxml";
    private static final String MANAGER_MERCHANT_MENU_PATH = "/dashboard/managerMerchantMenu.fxml";

    private static String currentMerchantMenuPath = ADMIN_MERCHANT_MENU_PATH;

    private MerchantMenuNavigation() {
    }

    public static void useAdminMerchantMenu() {
        currentMerchantMenuPath = ADMIN_MERCHANT_MENU_PATH;
    }

    public static void useManagerMerchantMenu() {
        currentMerchantMenuPath = MANAGER_MERCHANT_MENU_PATH;
    }

    public static void switchToCurrentMerchantMenu(ActionEvent event, String title) throws IOException {
        SceneSwitcher.switchScene(event, currentMerchantMenuPath, title);
    }

    public static void openCurrentMerchantMenu(Node sourceNode, String title) throws IOException {
        Parent root = FXMLLoader.load(MerchantMenuNavigation.class.getResource(currentMerchantMenuPath));

        Stage stage = (Stage) sourceNode.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}
