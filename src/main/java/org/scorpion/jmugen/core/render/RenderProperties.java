package org.scorpion.jmugen.core.render;

import org.scorpion.jmugen.core.config.StageDef;

public class RenderProperties {

    public RenderProperties() {
    }

    public StageDef.BG.Trans colorBlending = StageDef.BG.Trans.NONE;

    // used when blending is ADDALPHA
    public float alphaModifier = 1f;

}
