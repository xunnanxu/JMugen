package org.scorpion.jmugen.core.format;

public interface Sprite {

    byte[] getData();

    int getWidth();

    int getHeight();

    /**
     * The actual x axis value of the starting reference point related to the texture itself.
     * Positive value means left.
     * @return x offset
     */
    int getXOffset();

    /**
     * The actual y axis value of the starting reference point related to the texture itself.
     * Negative value means downwards.
     * @return y offset
     */
    int getYOffset();

}
