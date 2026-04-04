module com.teesolutions.ipospu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // CRITICAL: Allows us to use DatabaseManager

    opens com.teesolutions.ipospu to javafx.fxml;
    exports com.teesolutions.ipospu;

    // We will need to open our controllers later so the UI can see them
    opens com.teesolutions.ipospu.controllers to javafx.fxml;
    exports com.teesolutions.ipospu.controllers;
}