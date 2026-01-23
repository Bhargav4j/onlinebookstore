package com.bittercode.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.bittercode.constant.ResponseCode;
import com.bittercode.model.StoreException;

public class DBUtil {

    private static Connection connection;

    static {

        try {
            // Class.forName() is no longer required for JDBC 4.0+ drivers (Java 6+)
            // Driver is automatically loaded via ServiceLoader mechanism
            connection = DriverManager.getConnection(DatabaseConfig.CONNECTION_STRING, DatabaseConfig.DB_USER_NAME,
                    DatabaseConfig.DB_PASSWORD);
        } catch (SQLException e) {

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
