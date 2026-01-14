package com.bittercode.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    @Test
    void testDriverNameNotNull() {
        assertNotNull(DatabaseConfig.DRIVER_NAME);
    }

    @Test
    void testDbHostNotNull() {
        assertNotNull(DatabaseConfig.DB_HOST);
    }

    @Test
    void testDbPortNotNull() {
        assertNotNull(DatabaseConfig.DB_PORT);
    }

    @Test
    void testDbNameNotNull() {
        assertNotNull(DatabaseConfig.DB_NAME);
    }

    @Test
    void testDbUsernameNotNull() {
        assertNotNull(DatabaseConfig.DB_USER_NAME);
    }

    @Test
    void testDbPasswordNotNull() {
        assertNotNull(DatabaseConfig.DB_PASSWORD);
    }

    @Test
    void testConnectionStringNotNull() {
        assertNotNull(DatabaseConfig.CONNECTION_STRING);
    }

    @Test
    void testConnectionStringFormat() {
        String connectionString = DatabaseConfig.CONNECTION_STRING;
        if (connectionString != null && !connectionString.equals("null:null/null")) {
            assertTrue(connectionString.contains(":"));
            assertTrue(connectionString.contains("/"));
        }
    }

    @Test
    void testConnectionStringContainsHost() {
        String connectionString = DatabaseConfig.CONNECTION_STRING;
        if (DatabaseConfig.DB_HOST != null && !DatabaseConfig.DB_HOST.equals("null")) {
            assertTrue(connectionString.contains(DatabaseConfig.DB_HOST));
        }
    }

    @Test
    void testConnectionStringContainsPort() {
        String connectionString = DatabaseConfig.CONNECTION_STRING;
        if (DatabaseConfig.DB_PORT != null && !DatabaseConfig.DB_PORT.equals("null")) {
            assertTrue(connectionString.contains(DatabaseConfig.DB_PORT));
        }
    }

    @Test
    void testConnectionStringContainsDbName() {
        String connectionString = DatabaseConfig.CONNECTION_STRING;
        if (DatabaseConfig.DB_NAME != null && !DatabaseConfig.DB_NAME.equals("null")) {
            assertTrue(connectionString.contains(DatabaseConfig.DB_NAME));
        }
    }

    @Test
    void testPropertiesNotNull() {
        assertNotNull(DatabaseConfig.prop);
    }

    @Test
    void testDriverNameIsString() {
        assertTrue(DatabaseConfig.DRIVER_NAME instanceof String);
    }

    @Test
    void testDbHostIsString() {
        assertTrue(DatabaseConfig.DB_HOST instanceof String);
    }

    @Test
    void testDbPortIsString() {
        assertTrue(DatabaseConfig.DB_PORT instanceof String);
    }

    @Test
    void testDbNameIsString() {
        assertTrue(DatabaseConfig.DB_NAME instanceof String);
    }

    @Test
    void testDbUsernameIsString() {
        assertTrue(DatabaseConfig.DB_USER_NAME instanceof String);
    }

    @Test
    void testDbPasswordIsString() {
        assertTrue(DatabaseConfig.DB_PASSWORD instanceof String);
    }

    @Test
    void testConnectionStringIsString() {
        assertTrue(DatabaseConfig.CONNECTION_STRING instanceof String);
    }
}
