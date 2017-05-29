package org.scorpion.jmugen.core.maths;

import org.apache.commons.lang3.StringUtils;
import org.scorpion.jmugen.exception.ConfigException;

public class Rectangle4i {

    public final Point2i leftBottom, rightTop;

    public Rectangle4i(Point2i leftBottom, Point2i rightTop) {
        this.leftBottom = leftBottom;
        this.rightTop = rightTop;
    }

    public static Rectangle4i fromString(String input) {
        String[] config = StringUtils.split(input, ',');
        if (config == null || config.length < 4) {
            throw new ConfigException("Invalid rectangle: " + input);
        }
        int x1 = Integer.parseInt(config[0].trim());
        int y1 = Integer.parseInt(config[1].trim());
        int x2 = Integer.parseInt(config[2].trim());
        int y2 = Integer.parseInt(config[3].trim());
        return new Rectangle4i(new Point2i(x1, y1), new Point2i(x2, y2));
    }

    public int getWidth() {
        return Math.abs(rightTop.x - leftBottom.x) + 1;
    }

    public int getHeight() {
        return Math.abs(rightTop.y - leftBottom.y) + 1;
    }

    @Override
    public String toString() {
        return "{" + leftBottom + ", " + rightTop + "}";
    }

}
