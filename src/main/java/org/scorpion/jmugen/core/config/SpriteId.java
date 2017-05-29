package org.scorpion.jmugen.core.config;

import org.apache.commons.lang3.StringUtils;
import org.scorpion.jmugen.exception.ConfigException;

public class SpriteId {

    public final int group, id;

    public SpriteId(int group, int id) {
        this.group = group;
        this.id = id;
    }

    public static SpriteId fromString(String input) {
        String[] config = StringUtils.split(input, ',');
        if (config == null || config.length < 2) {
            throw new ConfigException("Invalid sprite id config: " + input);
        }
        int group = Integer.parseInt(config[0].trim());
        int id = Integer.parseInt(config[1].trim());
        return new SpriteId(group, id);
    }

    @Override
    public String toString() {
        return "group=" + group + ", id=" + id;
    }
}
