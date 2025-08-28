package org.smee.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

@Slf4j
@Getter
public abstract class AbstractSimpleConfiguration implements Configuration {
    protected Properties properties = new Properties();

    @Override
    public void loadConfigProperties(String prefix) {
        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream("application.properties");) {
            if (resource == null) {
                return;
            }
            new BufferedReader(new InputStreamReader(resource)).lines().forEach(line -> {
                if (line.startsWith(prefix)) {
                    String key = line.split("=")[0].substring(prefix.length() + 1).trim();
                    String value = line.split("=")[1].trim();
                    properties.put(key, value);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Config loaded: {}", properties);
    }
}
