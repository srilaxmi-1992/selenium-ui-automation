package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        String path = System.getProperty("user.dir") + "/src/test/resources/config.properties";
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties from: " + path, e);
        }
    }

    public static String getProperty(String key) {
        String value = System.getProperty(key); // for Jenkins, sending props from mvn commandline
        if (value != null && !value.isEmpty()) {
            return value.trim();
        }
        return properties.getProperty(key);
    }
}
