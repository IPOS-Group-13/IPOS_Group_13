module com.teesolutions.ipospu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.mail;

    opens com.teesolutions.ipospu to javafx.fxml;
    exports com.teesolutions.ipospu;


    opens com.teesolutions.ipospu.controllers to javafx.fxml;
    exports com.teesolutions.ipospu.controllers;
}