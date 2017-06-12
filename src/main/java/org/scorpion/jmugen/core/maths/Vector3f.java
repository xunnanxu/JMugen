package org.scorpion.jmugen.core.maths;

public final class Vector3f {

    public final float x, y, z;

    public static final Vector3f ZERO = new Vector3f(0f, 0f, 0f);

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
