package org.scorpion.jmugen.core.config;

import java.util.Map;

public class SystemConfig {

    private int windowWidth;
    private int windowHeight;
    private int gameWidth;
    private int gameHeight;
    private String resourceHome;

    public SystemConfig(Map<String, String> configs) {
        windowWidth = Integer.parseInt(configs.get(ConfigKeys.HORIZONTAL_RES));
        windowHeight = Integer.parseInt(configs.get(ConfigKeys.VERTICAL_RES));
        gameWidth = Integer.parseInt(configs.get(ConfigKeys.GAME_WIDTH));
        gameHeight = Integer.parseInt(configs.get(ConfigKeys.GAME_HEIGHT));
        resourceHome = configs.get(ConfigKeys.RESOURCE_HOME);
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    public String getResourceHome() {
        return resourceHome;
    }
}
