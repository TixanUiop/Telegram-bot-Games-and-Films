package org.evgeny.Util;

import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class GetProperties {

    private static final Properties props = new Properties();


    static {
        loadProperties();
    }

    private static void loadProperties() {
        InputStream resourceAsStream = GetProperties.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            props.load(resourceAsStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
