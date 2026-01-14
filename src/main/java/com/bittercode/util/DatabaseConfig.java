package com.bittercode.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class DatabaseConfig {

    static Properties prop = new Properties();
    static {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("application.properties");

        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final static String DRIVER_NAME = getEnvOrProperty("DRIVER_NAME", "db.driver");
    public final static String DB_HOST = getEnvOrProperty("DB_HOST", "db.host");
    public final static String DB_PORT = getEnvOrProperty("DB_PORT", "db.port");
    public final static String DB_NAME = getEnvOrProperty("DB_NAME", "db.name");
    public final static String DB_USER_NAME = getEnvOrProperty("DB_USERNAME", "db.username");
    public final static String DB_PASSWORD = getEnvOrProperty("DB_PASSWORD", "db.password");
    public final static String CONNECTION_STRING = DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    private static String getEnvOrProperty(String envVar, String propKey) {
        String value = System.getenv(envVar);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return prop.getProperty(propKey);
    }

}
