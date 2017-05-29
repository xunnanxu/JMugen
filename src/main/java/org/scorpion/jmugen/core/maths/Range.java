package org.scorpion.jmugen.core.maths;

import org.apache.commons.lang3.StringUtils;
import org.scorpion.jmugen.exception.ConfigException;

public class Range {

    public final float start, end;

    public Range(float start, float end) {
        this.start = start;
        this.end = end;
    }

    public static Range fromString(String input) {
        String[] config = StringUtils.split(input, ',');
        if (config == null || config.length < 2) {
            throw new ConfigException("Invalid range: " + input);
        }
        float x = Float.parseFloat(config[0].trim());
        float y = Float.parseFloat(config[1].trim());
        return new Range(x, y);
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }
}
