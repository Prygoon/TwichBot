package com.github.philippheuer.chatbot4twitch.dbFeatures;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBWorker {
    private Connection connection;

    public DBWorker() {
        try {
            Driver driver = new org.postgresql.Driver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(DB_Settings.getBaseUrl(), DB_Settings.getBaseUsername(), DB_Settings.getBasePass());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Connection getConnection() {
        return connection;
    }
}
