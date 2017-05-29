package org.scorpion.jmugen.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyUtils {

    public static Map<String, String> loadConfig(Resource resource) throws IOException {
        Properties properties = new Properties();
        properties.load(resource.load());
        Map<String, String> props = new HashMap<>();
        properties.stringPropertyNames().forEach(n -> props.put(n, properties.getProperty(n)));
        return props;
    }

}
