package org.scorpion.jmugen.core.element;

import org.scorpion.jmugen.core.config.Def;
import org.scorpion.jmugen.core.config.SystemConfig;
import org.scorpion.jmugen.core.render.Renderable;
import org.scorpion.jmugen.io.input.keyboard.KeyboardInputHandler;
import org.scorpion.jmugen.util.Loadable;

public abstract class GameObject<D extends Def> implements Renderable, Loadable<Void> {

    protected D config;
    protected SystemConfig systemConfig;
    protected KeyboardInputHandler keyboardInputHandler;

    public GameObject(SystemConfig systemConfig, D config, KeyboardInputHandler keyboardInputHandler) {
        this.systemConfig = systemConfig;
        this.config = config;
        this.keyboardInputHandler = keyboardInputHandler;
    }

}
