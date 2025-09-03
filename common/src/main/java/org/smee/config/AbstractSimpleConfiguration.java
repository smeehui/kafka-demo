package org.smee.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Getter
public abstract class AbstractSimpleConfiguration implements Configuration {
    protected Properties properties = new Properties();

    @Override
    public void loadConfigProperties(String prefix) {
        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (resource == null) {
                return;
            }
            new BufferedReader(new InputStreamReader(resource)).lines().forEach(line -> {
                if (line.startsWith(prefix)) {
                    String key = line.split("=")[0].substring(prefix.length() + 1).trim();
                    String value = line.split("=")[1].trim();
                    if (value.startsWith("${")){
                        value = System.getenv(processEnvValue(value));
                    }
                    properties.put(key, value);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("Config loaded: {}", properties);
    }

    private String processEnvValue(String value) {
        // The regex pattern to match and capture the value inside ${...}
        String regex = "\\$\\{([^}]+)\\}";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(value);

        return matcher.find() ? matcher.group(1) : "";
    }
}
