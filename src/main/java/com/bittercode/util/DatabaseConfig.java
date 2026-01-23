package com.bittercode.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class DatabaseConfig {

    static Properties prop = new Properties();
    static {
        // Use class-based resource loading for better reliability in Java 17+
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                prop.load(input);
            } else {
                System.err.println("Unable to find application.properties");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final static String DRIVER_NAME = prop.getProperty("db.driver");
    public final static String DB_HOST = prop.getProperty("db.host");
    public final static String DB_PORT = prop.getProperty("db.port");
    public final static String DB_NAME = prop.getProperty("db.name");
    public final static String DB_USER_NAME = prop.getProperty("db.username");
    public final static String DB_PASSWORD = prop.getProperty("db.password");
    public final static String CONNECTION_STRING = DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

}
