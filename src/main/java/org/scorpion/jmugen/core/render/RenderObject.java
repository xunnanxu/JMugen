package org.scorpion.jmugen.core.render;

import org.scorpion.jmugen.render.Mesh;
import org.scorpion.jmugen.render.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class RenderObject {

    public final Mesh mesh;
    public final Texture texture;

    public RenderObject(Mesh mesh, Texture texture) {
        this.mesh = mesh;
        this.texture = texture;
    }

    public void render() {
        glEnable(GL_BLEND);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        texture.bind();
        mesh.bind();
        mesh.draw();
        mesh.unbind();
        texture.unbind();
        glDisable(GL_BLEND);
    }
}
