package org.scorpion.jmugen.core.render;

import org.scorpion.jmugen.core.config.StageDef;
import org.scorpion.jmugen.core.element.Camera;
import org.scorpion.jmugen.core.maths.Point2i;

public class RenderProperties {

    public RenderProperties() {
    }

    public Camera camera;

    public StageDef.BG.Trans colorBlending = StageDef.BG.Trans.NONE;

    // used when blending is ADDALPHA
    public float alphaModifier = 1f;

    public Point2i offset;

    public int tileX = 0;
    public int tileY = 0;
    public int tileSpacingX = 0;
    public int tileSpacingY = 0;

}
