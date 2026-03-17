package com.berrybyte.common;

import java.sql.DriverManager;
import java.sql.Connection;

public class DatabaseConnection {
    public Connection databaselink;

    public Connection getConnection() {
        String databaseName = "railway";
        String databaseUsername = "root";
        String databasePassword = "ymQLxQUfCfstogNMynirnffMcLrBrKsP";
        String url = "jdbc:mysql://switchyard.proxy.rlwy.net:20890/railway";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaselink = DriverManager.getConnection(url, databaseUsername, databasePassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaselink;
    }
}
