package org.scorpion.jmugen.core.element;

import org.scorpion.jmugen.core.config.Def;
import org.scorpion.jmugen.core.render.Renderable;
import org.scorpion.jmugen.util.Loadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;

public abstract class GameObject<D extends Def> implements Renderable, Loadable<Void>, Callable<Void> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected D config;
    protected Map<String, String> globalConfig;

    public GameObject(Map<String, String> globalConfig, D config) {
        this.globalConfig = globalConfig;
        this.config = config;
    }

    @Override
    public Void call() throws Exception {
        try {
            logger.info("Loading " + toString());
            return load();
        } finally {
            logger.info(toString() + " has been successfully loaded.");
        }
    }
}
