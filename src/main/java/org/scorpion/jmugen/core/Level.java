package org.scorpion.jmugen.core;

import org.scorpion.jmugen.core.maths.Matrix4f;
import org.scorpion.jmugen.core.maths.Vector3f;
import org.scorpion.jmugen.core.render.Renderable;
import org.scorpion.jmugen.render.*;
import org.scorpion.jmugen.util.ClasspathResource;

public class Level implements Renderable {

    private Mesh background;
    private Texture bgTexture;

    int xScroll = 0, bgMap = 0;

    public Level() {
    }

    @Override
    public void init() {
        background = new RectangularMesh(new Vector3f(-10.0f, -10.0f * 9.0f / 16.0f, 0.0f), new Vector3f(10.0f, 10.0f * 9.0f / 16.0f, 0.0f));
        bgTexture = new Texture(new ClasspathResource("images/Desert.jpg"));
        bgTexture.load();
    }

    @Override
    public void render() {
        bgTexture.bind();
        Shader bgShader = Shaders.getBackgroundShader();
        bgShader.enable();
        background.bind();
//        for (int i = bgMap; i < bgMap + 3; i++) {
            bgShader.setMatrix4f("vw_mat", Matrix4f.translate(new Vector3f(xScroll * 0.03f, 0, 0)));
            background.draw();
//        }
        bgShader.disable();
        bgTexture.unbind();
    }

    @Override
    public void update() {
        xScroll--;
        if (-xScroll % 335 == 0) {
            bgMap++;
        }
    }
}
