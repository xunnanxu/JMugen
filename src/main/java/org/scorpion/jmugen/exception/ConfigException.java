package org.scorpion.jmugen.exception;

public class ConfigException extends GenericException implements NonFatal {

    public ConfigException(String message) {
        super(message, null, false, false);
    }

    public static ConfigException missing(String title, String group, String key) {
        StringBuilder sb = new StringBuilder(title)
                .append(": Missing ")
                .append(key);
        if (group != null) {
            sb.append(" in ").append(group);
        }
        return new ConfigException(sb.toString());
    }

}
