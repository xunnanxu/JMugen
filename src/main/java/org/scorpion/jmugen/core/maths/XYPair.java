package org.scorpion.jmugen.core.maths;

import org.apache.commons.lang3.StringUtils;
import org.scorpion.jmugen.exception.ConfigException;

public class XYPair {

    public final float x, y;

    public XYPair(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static XYPair fromString(String input) {
        String[] config = StringUtils.split(input, ',');
        if (config == null || config.length < 2) {
            throw new ConfigException("Invalid pair: " + input);
        }
        float x = Float.parseFloat(config[0].trim());
        float y = Float.parseFloat(config[1].trim());
        return new XYPair(x, y);
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }
}
