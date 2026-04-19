package com.berrybyte.common;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Represents database connection.
 */
public class DatabaseConnection {

    public Connection databaselink;
/**
 * Returns connection.
 *
 * @return result value
 */

    public Connection getConnection() {
        try {
            Properties properties = new Properties();

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db.properties.local");

            if (inputStream == null) {
                throw new RuntimeException("db.properties.local file not found in resources folder.");
            }

            properties.load(inputStream);

            String databaseHost = properties.getProperty("db.host");
            String databasePort = properties.getProperty("db.port");
            String databaseName = properties.getProperty("db.name");
            String databaseUsername = properties.getProperty("db.username");
            String databasePassword = properties.getProperty("db.password");

            String url = "jdbc:mysql://" + databaseHost + ":" + databasePort + "/" + databaseName + "?serverTimezone=UTC";

            Class.forName("com.mysql.cj.jdbc.Driver");
            databaselink = DriverManager.getConnection(url, databaseUsername, databasePassword);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return databaselink;
    }
}
