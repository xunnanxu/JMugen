package org.scorpion.jmugen.core.element;

import org.scorpion.jmugen.core.maths.Matrix4f;

public class Camera {

    public static final Camera INSTANCE = new Camera();

    Matrix4f viewMatrix = Matrix4f.identity();

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
    }
}
