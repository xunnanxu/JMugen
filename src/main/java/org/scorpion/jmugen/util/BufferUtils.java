package org.scorpion.jmugen.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtils {

    public static ByteBuffer toByteBuffer(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer
                .allocateDirect(bytes.length)
                .order(ByteOrder.nativeOrder())
                .put(bytes);
        buffer.flip();
        return buffer;
    }

    public static IntBuffer toIntBuffer(int[] ints) {
        IntBuffer buffer = ByteBuffer
                .allocateDirect(ints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(ints);
        buffer.flip();
        return buffer;
    }

    public static FloatBuffer toFloatBuffer(float[] floats) {
        FloatBuffer buffer = ByteBuffer
                .allocateDirect(floats.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(floats);
        buffer.flip();
        return buffer;
    }

}
