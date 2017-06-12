package org.scorpion.jmugen.core.maths;

import org.apache.commons.lang3.StringUtils;
import org.scorpion.jmugen.exception.ConfigException;

public class Point2f {

    public float x, y;

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Point2f fromString(String input) {
        String[] config = StringUtils.split(input, ',');
        if (config == null || config.length < 2) {
            throw new ConfigException("Invalid point: " + input);
        }
        float x = Float.parseFloat(config[0].trim());
        float y = Float.parseFloat(config[1].trim());
        return new Point2f(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
