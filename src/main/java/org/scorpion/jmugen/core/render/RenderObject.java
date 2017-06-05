package org.scorpion.jmugen.core.render;

import org.scorpion.jmugen.core.config.StageDef;
import org.scorpion.jmugen.render.Mesh;
import org.scorpion.jmugen.render.Shader;
import org.scorpion.jmugen.render.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

public class RenderObject {

    private final Mesh mesh;
    private final Texture texture;
    private final RenderProperties renderProperties;

    public RenderObject(Mesh mesh, Texture texture, RenderProperties renderProperties) {
        this.mesh = mesh;
        this.texture = texture;
        this.renderProperties = renderProperties;
    }

    public void configShader(Shader shader) {
        if (renderProperties.colorBlending == StageDef.BG.Trans.ADDALPHA) {
            shader.setUniform1f(Shader.ALPHA_MODIFIER, renderProperties.alphaModifier);
        }
    }

    public void render() {
        glEnable(GL_BLEND);
        mesh.bind();
        switch (renderProperties.colorBlending) {
            case NONE:
                glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
                texture.bind();
                mesh.draw();
                texture.unbind();
                break;
            case ADD:
                glBlendFuncSeparate(GL_SRC_COLOR, GL_ONE_MINUS_SRC_COLOR, GL_ONE, GL_ZERO);
                texture.bind();
                mesh.draw();
                texture.unbind();
                break;
            case ADD1:
                glBlendColor(1.0f, 1.0f, 1.0f, 0.5f);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                mesh.draw();
                glBlendFuncSeparate(GL_SRC_COLOR, GL_ONE_MINUS_SRC_COLOR, GL_ONE, GL_ZERO);
                texture.bind();
                mesh.draw();
                texture.unbind();
                break;
            case ADDALPHA:
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                texture.bind();
                mesh.draw();
                texture.unbind();
                break;
            case SUB:
                glBlendEquation(GL_FUNC_SUBTRACT);
                glBlendFuncSeparate(GL_ONE_MINUS_SRC_COLOR, GL_ONE_MINUS_DST_COLOR,
                        GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);
                texture.bind();
                mesh.draw();
                texture.unbind();
                glBlendEquation(GL_FUNC_ADD);
                break;
        }
        mesh.unbind();
        glDisable(GL_BLEND);
    }

    public void resetShader(Shader shader) {
        shader.setUniform1f(Shader.ALPHA_MODIFIER, 1f);
    }
}
