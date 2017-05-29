package org.scorpion.jmugen.core.element;

import org.scorpion.jmugen.core.render.Renderable;

public abstract class Actor implements Renderable {

    boolean controllable;

    public boolean isControllable() {
        return controllable;
    }

    public void setControllable(boolean controllable) {
        this.controllable = controllable;
    }

}
