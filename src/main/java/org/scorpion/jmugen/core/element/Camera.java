package org.scorpion.jmugen.core.element;

import org.scorpion.jmugen.core.config.SystemConfig;
import org.scorpion.jmugen.core.maths.Matrix4f;

public class Camera {

    private SystemConfig systemConfig;

    public Camera(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    Matrix4f viewMatrix = Matrix4f.identity();

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public int getViewportWidth() {
        return systemConfig.getGameWidth();
    }

    public int getViewportHeight() {
        return systemConfig.getGameHeight();
    }
}
