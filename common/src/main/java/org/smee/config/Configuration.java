package org.smee.config;

import java.util.Properties;

public interface Configuration {
    Properties getProperties();
    void loadConfigProperties(String prefix);
}
