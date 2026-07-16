package com.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader — Centralized configuration reader for the framework.
 *
 * Loads key-value pairs from src/test/resources/config.properties at startup.
 * Supports runtime overrides via JVM system properties (-Dkey=value),
 * which allows CI/CD pipelines to override settings without modifying files.
 *
 * Priority order (highest to lowest):
 *   1. JVM system property:  mvn test -Dbrowser=firefox
 *   2. config.properties value
 *   3. Default value passed to get(key, defaultValue)
 *
 * Usage examples:
 *   String browser = ConfigReader.get("browser");               // throws if missing
 *   String url     = ConfigReader.get("base.url", "");          // returns "" if missing
 *   long timeout   = Long.parseLong(ConfigReader.get("explicit.wait", "15"));
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();

    // Path to the properties file — relative to project root
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    /*
     * Static initializer — runs once when the class is first loaded.
     * Loads the properties file into memory so all tests share one instance.
     * Throws RuntimeException at startup if the file is missing (fail-fast behavior).
     */
    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            log.info("config.properties loaded successfully.");
        } catch (IOException e) {
            log.error("Failed to load config.properties from: {}", CONFIG_PATH, e);
            throw new RuntimeException("config.properties not found at: " + CONFIG_PATH);
        }
    }

    /**
     * Returns the value for the given key.
     * Checks JVM system properties first so -Dkey=value overrides always win.
     *
     * @param key Property key to look up
     * @return Trimmed string value
     * @throws RuntimeException if the key is not found in system properties or config.properties
     */
    public static String get(String key) {
        // Check JVM system property first — allows: mvn test -Dbrowser=edge
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in config.properties");
        }
        return value.trim();
    }

    /**
     * Returns the value for the given key, or a default if the key is not found.
     * Use this when a property is optional and a sensible fallback exists.
     *
     * @param key          Property key to look up
     * @param defaultValue Value to return when key is absent
     * @return Property value or defaultValue
     */
    public static String get(String key, String defaultValue) {
        try {
            return get(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
}
