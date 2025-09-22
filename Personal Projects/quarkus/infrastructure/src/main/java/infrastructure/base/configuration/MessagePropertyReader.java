package infrastructure.base.configuration;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.inject.Singleton;
import lombok.Getter;

@Singleton
@Getter
public class MessagePropertyReader {

    private final Properties properties;

    public MessagePropertyReader() throws IOException {

        properties = new Properties();
        String propertyFileName = "message.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyFileName);

        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("Message Property file was not found!");
        }
    }

    public String getMessage(String messageKey) {
        return properties.getProperty(messageKey);
    }

    public String getMessageOrDefault(String messageKey, String defaultValue) {
        return properties.getProperty(messageKey, defaultValue);
    }

}