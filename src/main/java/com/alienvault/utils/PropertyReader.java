package com.alienvault.utils;

import com.alienvault.exception.AppException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by sneti on 6/14/17.
 */
public class PropertyReader {
    private final static Logger logger = Logger.getLogger(PropertyReader.class);
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(FileLoader.loadFileReader("app.properties"));
        } catch (IOException e) {
            String message = "Exception occurred while reading app.properties.";
            logger.error(message, e);
            throw new AppException(message, e);
        }
    }

    public static String readProperty(final String propertyName) {
        return properties.getProperty(propertyName);
    }

}
