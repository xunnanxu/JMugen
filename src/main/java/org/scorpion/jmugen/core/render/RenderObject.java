package org.scorpion.jmugen.core.render;

import org.scorpion.jmugen.render.Mesh;
import org.scorpion.jmugen.render.Texture;

public class RenderObject {

    public final Mesh mesh;
    public final Texture texture;

    public RenderObject(Mesh mesh, Texture texture) {
        this.mesh = mesh;
        this.texture = texture;
    }

    public void render() {
        texture.bind();
        mesh.bind();
        mesh.draw();
        mesh.unbind();
        texture.unbind();
    }
}
