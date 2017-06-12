package org.scorpion.jmugen.io.input.keyboard;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.*;

public class KeyboardInputHandler extends GLFWKeyCallback {

    protected boolean[] keys = new boolean[4096];
    private Map<Integer, List<KeyStrokeHandler>> keyStrokeHandlers = new HashMap<>();

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        keys[key] = action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT;
        if (action == GLFW.GLFW_PRESS) {
            keyStrokeHandlers.getOrDefault(key, Collections.emptyList()).forEach(h -> h.onKey(mods));
        }
    }

    public void registerStrokeHandler(int code, KeyStrokeHandler handler) {
        keyStrokeHandlers
                .computeIfAbsent(code, k -> new ArrayList<>())
                .add(handler);
    }

    public boolean isKeyPressed(int key) {
        return keys[key];
    }
}

