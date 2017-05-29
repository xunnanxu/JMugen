package org.scorpion.jmugen.core.maths;

import org.apache.commons.lang3.StringUtils;
import org.scorpion.jmugen.exception.ConfigException;

public class Rectangle4f {

    public final Point2f leftBottom, rightTop;

    public Rectangle4f(Point2f leftBottom, Point2f rightTop) {
        this.leftBottom = leftBottom;
        this.rightTop = rightTop;
    }

    public static Rectangle4f fromString(String input) {
        String[] config = StringUtils.split(input, ',');
        if (config == null || config.length < 4) {
            throw new ConfigException("Invalid rectangle: " + input);
        }
        float x1 = Float.parseFloat(config[0].trim());
        float y1 = Float.parseFloat(config[1].trim());
        float x2 = Float.parseFloat(config[2].trim());
        float y2 = Float.parseFloat(config[3].trim());
        return new Rectangle4f(new Point2f(x1, y1), new Point2f(x2, y2));
    }

    public float getWidth() {
        return Math.abs(rightTop.x - leftBottom.x);
    }

    public float getHeight() {
        return Math.abs(rightTop.y - leftBottom.y);
    }

    @Override
    public String toString() {
        return "{" + leftBottom + ", " + rightTop + "}";
    }
}
