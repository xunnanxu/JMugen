package org.scorpion.jmugen.core.maths;

import org.scorpion.jmugen.util.BufferUtils;

import java.nio.FloatBuffer;

public class Matrix4f {

    private float[] elements = new float[16];
    private FloatBuffer buffer;

    private Matrix4f() {
    }

    public static Matrix4f identity() {
        Matrix4f m = new Matrix4f();
        m.elements[0] = 1.0f;
        m.elements[5] = 1.0f;
        m.elements[10] = 1.0f;
        m.elements[15] = 1.0f;
        return m;
    }

    public static Matrix4f orthographic(float left, float right, float top, float bottom, float near, float far) {
        Matrix4f m = identity();
        m.elements[0] = 2.0f / (right - left);
        m.elements[5] = 2.0f / (top - bottom);
        m.elements[10] = 2.0f / (near - far);
        m.elements[12] = (left + right) / (left - right);
        m.elements[13] = (bottom + top) / (bottom - top);
        m.elements[14] = (far + near) / (far - near);
        return m;
    }

    /**
     * 1 0 0 x<br>
     * 0 1 0 y<br>
     * 0 0 1 z<br>
     * 0 0 0 1<br>
     */
    public static Matrix4f translate(Vector3f vector) {
        return identity().translateTo(vector);
    }

    public Matrix4f translateTo(Vector3f vector) {
        elements[12] = vector.x;
        elements[13] = vector.y;
        elements[14] = vector.z;
        return this;
    }

    public static Matrix4f resize(Vector3f vector) {
        return identity().resizeTo(vector);
    }

    public Matrix4f resizeTo(Vector3f vector) {
        elements[0] = vector.x;
        elements[5] = vector.y;
        elements[10] = vector.z;
        return this;
    }

    /**
     * cos -sin 0 0<br>
     * sin  cos 0 0<br>
     * 0     0  1 0<br>
     * 0     0  0 1<br>
     */
    public static Matrix4f rotate(float angle) {
        Matrix4f m = identity();
        float a = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(a);
        float sin = (float) Math.sin(a);
        m.elements[0] = cos;
        m.elements[1] = sin;
        m.elements[4] = -sin;
        m.elements[5] = cos;
        return m;
    }

    public Matrix4f multiply(Matrix4f m2) {
        Matrix4f m = new Matrix4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += elements[i + k * 4] * m2.elements[j * 4 + k];
                }
                m.elements[i + j * 4] = sum;
            }
        }
        return m;
    }

    public FloatBuffer toFloatBuffer() {
        if (buffer == null) {
            buffer = BufferUtils.toFloatBuffer(elements);
            return buffer;
        }
        buffer.rewind();
        buffer.put(elements);
        buffer.flip();
        return buffer;
    }

}
