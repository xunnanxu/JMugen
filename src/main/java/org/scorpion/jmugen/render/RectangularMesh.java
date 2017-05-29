package org.scorpion.jmugen.render;

import org.scorpion.jmugen.core.maths.Vector3f;

public class RectangularMesh extends Mesh {

    public RectangularMesh(Vector3f topLeft, Vector3f bottomRight) {
        super(
                new float[] {
                        topLeft.x, bottomRight.y, (topLeft.z + bottomRight.z) / 2.0f,
                        topLeft.x, topLeft.y, topLeft.z,
                        bottomRight.x, topLeft.y, (topLeft.z + bottomRight.z) / 2.0f,
                        bottomRight.x, bottomRight.y, bottomRight.z
                },
                new byte[] {
                        0, 1, 2,
                        2, 3, 0
                },
                new float[] {
                        0, 1,
                        0, 0,
                        1, 0,
                        1, 1
                }
        );
    }

    public RectangularMesh(Vector3f center, float width, float height) {
        super(
                new float[] {
                        center.x - width / 2.0f, center.y - height / 2.0f, center.z,
                        center.x - width / 2.0f, center.y + height / 2.0f, center.z,
                        center.x + width / 2.0f, center.y + height / 2.0f, center.z,
                        center.x + width / 2.0f, center.y - height / 2.0f, center.z
                },
                new byte[] {
                        0, 1, 2,
                        2, 3, 0
                },
                new float[] {
                        0, 0,
                        0, 1,
                        1, 1,
                        1, 0
                }
        );
    }
}
