package com.vladislavlevchik.utils;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class ConfigUtil {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalArgumentException("Config file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load configuration file");
        }
    }

    public static String getApiKey() {
        return properties.getProperty("api.id");
    }
}
