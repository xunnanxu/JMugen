package org.scorpion.jmugen.render;

import org.scorpion.jmugen.util.ClasspathResource;

public class Shaders {

    private static Shader backgroundShader;

    public static void loadBackgroundShader() {
        backgroundShader = new Shader("Background", new ClasspathResource("shaders/bg.vert"), new ClasspathResource("shaders/bg.frag"));
        backgroundShader.load();
    }

    public static Shader getBackgroundShader() {
        return backgroundShader;
    }

    public static void setBackgroundShader(Shader backgroundShader) {
        Shaders.backgroundShader = backgroundShader;
    }
}
