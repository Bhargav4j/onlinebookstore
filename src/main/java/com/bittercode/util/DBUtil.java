package com.bittercode.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.bittercode.constant.ResponseCode;
import com.bittercode.model.StoreException;

public class DBUtil {

    private static Connection connection;

    static {

        try {

            Class.forName(DatabaseConfig.DRIVER_NAME);

            // Configure PostgreSQL connection properties
            Properties props = new Properties();
            props.setProperty("user", DatabaseConfig.DB_USER_NAME);
            props.setProperty("password", DatabaseConfig.DB_PASSWORD);
            props.setProperty("ssl", "false");
            props.setProperty("prepareThreshold", "0");
            props.setProperty("preparedStatementCacheQueries", "256");
            props.setProperty("preparedStatementCacheSizeMiB", "5");

            connection = DriverManager.getConnection(DatabaseConfig.CONNECTION_STRING, props);

            // Set auto-commit to true for PostgreSQL
            connection.setAutoCommit(true);

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();

        }

    }// End of static block

    public static Connection getConnection() throws StoreException {

        if (connection == null) {
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }

        return connection;
    }

}
