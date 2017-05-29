package org.scorpion.jmugen.core.render;

public interface Renderable {

    /**
     * called once
     */
    public void init();

    /**
     * bind resources, called for each frame
     */
    public void render();

    /**
     * update coords, etc.
     */
    public void update();

}
