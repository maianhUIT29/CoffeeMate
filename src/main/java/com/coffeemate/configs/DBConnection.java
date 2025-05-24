package com.coffeemate.configs;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    public static Connection getConnection() {
        Connection connection = null;
        FileInputStream inputStream = null;

        try {
            // Load configuration from config.properties
            Properties properties = new Properties();
            inputStream = new FileInputStream("config.properties"); // Ensure the path is correct
            properties.load(inputStream);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            // Load JDBC driver and establish connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, user, password);

            // Optional: force autocommit ON
            connection.setAutoCommit(true);

            // Debug info
            System.out.println("✅ Oracle DB Connected!");
            System.out.println("🔗 URL: " + connection.getMetaData().getURL());
            System.out.println("🔧 AutoCommit: " + connection.getAutoCommit());

        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Failed to connect to DB: " + e.getMessage());
        } finally {
            // Ensure FileInputStream is closed after use
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("🔌 Connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("❌ Failed to close connection:");
                e.printStackTrace();
            }
        }
    }
}
