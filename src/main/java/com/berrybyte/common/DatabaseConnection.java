package com.berrybyte.common;

import java.sql.DriverManager;
import java.sql.Connection;

public class DatabaseConnection {
    public Connection databaselink;

    public Connection getConnection() {
        String databaseName = "ipos_sa";
        String databaseUsername = "teamuser";
        String databasePassword = "password";
        String url = "jdbc:mysql://192.168.0.22/" + databaseName;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaselink = DriverManager.getConnection(url, databaseUsername, databasePassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaselink;
    }
}
