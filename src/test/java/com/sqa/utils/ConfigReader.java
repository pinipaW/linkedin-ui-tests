package com.sqa.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("config.properties not found, falling back to environment variables");
        }
    }

    public static String get(String key) {
        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        return env != null ? env : props.getProperty(key);
    }
}