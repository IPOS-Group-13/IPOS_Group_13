package com.berrybyte.common;

import java.sql.DriverManager;
import java.sql.Connection;

public class DatabaseConnection {
    public Connection databaselink;

    public Connection getConnection() {
        String databaseName = "ipos_sa";
        String databaseUsername = "root";
        String databasePassword = "Hwhqh1h1537hd-";
        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaselink = DriverManager.getConnection(url, databaseUsername, databasePassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaselink;
    }
}
