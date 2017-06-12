package org.scorpion.jmugen.core.render;

import org.scorpion.jmugen.core.config.StageDef;
import org.scorpion.jmugen.core.config.SystemConfig;
import org.scorpion.jmugen.core.maths.Point2f;

public class RenderProperties {

    private SystemConfig systemConfig;

    public RenderProperties(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    public SystemConfig getSystemConfig() {
        return systemConfig;
    }

    public StageDef.BG.Trans colorBlending = StageDef.BG.Trans.NONE;

    // used when blending is ADDALPHA
    public float alphaModifier = 1f;

    public Point2f offset;
    public Point2f viewOffset;
    public Point2f viewOffsetDelta;

    public int tileX = 0;
    public int tileY = 0;
    public int tileSpacingX = 0;
    public int tileSpacingY = 0;

}
