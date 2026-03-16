module com.berrybyte.ipos_sa {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.berrybyte.login to javafx.fxml;
    opens com.berrybyte.account to javafx.fxml;
    opens com.berrybyte.common to javafx.fxml;

    exports com.berrybyte.login;
    exports com.berrybyte.account;
    exports com.berrybyte.common;
}