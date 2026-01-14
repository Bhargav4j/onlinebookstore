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

    private static String getConfigValue(String envVar, String propKey) {
        String envValue = System.getenv(envVar);
        return (envValue != null && !envValue.isEmpty()) ? envValue : prop.getProperty(propKey);
    }

    public final static String DRIVER_NAME = prop.getProperty("db.driver");
    public final static String DB_HOST = getConfigValue("DB_HOST", "db.host");
    public final static String DB_PORT = getConfigValue("DB_PORT", "db.port");
    public final static String DB_NAME = getConfigValue("DB_NAME", "db.name");
    public final static String DB_USER_NAME = getConfigValue("DB_USER_NAME", "db.username");
    public final static String DB_PASSWORD = getConfigValue("DB_PASSWORD", "db.password");
    public final static String CONNECTION_STRING = DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

}
