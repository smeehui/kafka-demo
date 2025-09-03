package org.smee.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database extends AbstractSimpleConfiguration {
    public Database() {
        this.loadConfigProperties("db");
    }

    public Connection getConnection() throws SQLException {
        if (this.properties.isEmpty()) {
            this.loadConfigProperties("db");
        }
        return DriverManager.getConnection(
                this.properties.getProperty("url"),
                this.properties.getProperty("user"),
                this.properties.getProperty("password")
        );
    }
}
