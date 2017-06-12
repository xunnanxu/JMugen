package org.scorpion.jmugen.core.config;

import org.apache.commons.lang3.StringUtils;
import org.scorpion.jmugen.core.maths.*;
import org.scorpion.jmugen.exception.ConfigException;

import java.util.*;
import java.util.function.Function;

public class StageDef implements Def<StageDef> {

    public static class BG implements Def<BG> {

        public static final String PREFIX = "bg ";

        private static final String TYPE = "type";
        private static final String SPRITE_NO = "spriteno";
        private static final String LAYER_NO = "layerno";
        private static final String START = "start";
        private static final String DELTA = "delta";
        private static final String TRANS = "trans";
        private static final String ALPHA = "alpha";
        private static final String MASK = "mask";
        private static final String TILE = "tile";
        private static final String TILE_SPACING = "tilespacing";
        private static final String WINDOW = "window";
        private static final String WINDOW_DELTA = "windowdelta";

        static final Map<String, Function<String, ?>> BG_CONFIG_CONVERTERS = new HashMap<>();

        static {
            BG_CONFIG_CONVERTERS.put(TYPE, input -> Type.valueOf(input.toUpperCase()));
            BG_CONFIG_CONVERTERS.put(SPRITE_NO, SpriteId::fromString);
            BG_CONFIG_CONVERTERS.put(LAYER_NO, Integer::valueOf);
            BG_CONFIG_CONVERTERS.put(START, Point2i::fromString);
            BG_CONFIG_CONVERTERS.put(DELTA, Point2f::fromString);
            BG_CONFIG_CONVERTERS.put(TRANS, input -> Trans.valueOf(input.toUpperCase()));
            BG_CONFIG_CONVERTERS.put(ALPHA, Point2i::fromString);
            BG_CONFIG_CONVERTERS.put(MASK, BOOLEAN_CONVERTER);
            BG_CONFIG_CONVERTERS.put(TILE, Point2i::fromString);
            BG_CONFIG_CONVERTERS.put(TILE_SPACING, Point2i::fromString);
            BG_CONFIG_CONVERTERS.put(WINDOW, Rectangle4i::fromString);
            BG_CONFIG_CONVERTERS.put(WINDOW_DELTA, Point2f::fromString);
        }

        public enum Type {
            NORMAL,
            PARALLAX
        }

        /**
         *  Valid values are:
         *  "none" for normal drawing
         *  "add" for colour addition (like a spotlight effect)
         *  "add1" for colour addition with background dimmed to 50% brightness
         *  "addalpha" for colour addition with control over alpha values (you need an "alpha" parameter if you use this)
         *  "sub" for colour subtraction (like a shadow effect)
         */
        public enum Trans {
            NONE,
            ADD,
            ADD1,
            ADDALPHA,
            SUB
        }

        private static final Config DEFAULT_CONFIG = new Config.Builder()
                .add(TYPE, "NORMAL")
                .add(LAYER_NO, "0")
                .add(START, "0,0")
                .add(DELTA, "1,1")
                .add(TRANS, "NONE")
                .add(ALPHA, "256,0")
                .add(MASK, "0")
                .add(TILE, "0,0")
                .add(TILE_SPACING, "0,0")
                .add(WINDOW, "0,0,319,239")
                .add(WINDOW_DELTA, "0,0")
                .get();

        private String name;
        private Config config;

        public BG(String name, Config config) {
            this.name = name;
            this.config = new Config().merge(DEFAULT_CONFIG).merge(config);
        }

        /**
         * defined by "window"
         * @return canonical width of the drawing space before scaling
         */
        public int getOriginalWidth() {
            return ((Rectangle4i) config.get(WINDOW)).getWidth();
        }

        /**
         * defined by "window"
         * @return canonical width of the drawing space before scaling
         */
        public int getOriginalHeight() {
            return ((Rectangle4i) config.get(WINDOW)).getHeight();
        }

        public boolean isForeground() {
            return Integer.valueOf(1).equals(config.get(LAYER_NO));
        }

        public SpriteId getSpriteId() {
            return (SpriteId) config.get(SPRITE_NO);
        }

        public Point2i getStartCoord() {
            return (Point2i) config.get(START);
        }

        public int getTileX() {
            return ((Point2i) config.get(TILE)).x;
        }

        public int getTileY() {
            return ((Point2i) config.get(TILE)).y;
        }

        public int getTileSpacingX() {
            return ((Point2i) config.get(TILE_SPACING)).x;
        }

        public int getTileSpacingY() {
            return ((Point2i) config.get(TILE_SPACING)).y;
        }

        public Trans getTrans() {
            return config.get(TRANS);
        }

        public float getAlphaModifier() {
            Point2i alphaConfig = config.get(ALPHA);
            return ((float) alphaConfig.x) / (alphaConfig.x + alphaConfig.y);
        }

        @Override
        public BG load() {
            if (config.get(SPRITE_NO) == null) {
                throw new ConfigException("Sprite no. is missing in BG " + name);
            }
            config = config.applyConverters(BG_CONFIG_CONVERTERS);
            return this;
        }

        @Override
        public String toString() {
            return config.toString();
        }

        public String getName() {
            return name;
        }
    }

    private static final String GROUP_GERERAL_INFO = "info";
    private static final String STAGE_NAME = "name";

    private static final String GROUP_STAGE_INFO = "stageinfo";
    private static final String LOCAL_COORD = "localcoord";

    private static final String GROUP_SCALING_INFO = "scaling";
    private static final String XSCALE = "xscale";
    private static final String YSCALE = "yscale";

    private static final String GROUP_BG_DEF = "bgdef";
    private static final String SPR = "spr";
    private static final String DEBUG_BG = "debugbg";

    private static final GroupedConfig DEFAULT_GENERAL_CONFIG = new GroupedConfig.Builder()
            .add(GROUP_STAGE_INFO,
                    Config.build()
                            .add(LOCAL_COORD, "320,240")
                            .get()
            )
            .add(GROUP_BG_DEF, new Config(DEBUG_BG, "0"))
            .add(GROUP_SCALING_INFO, Config.build()
                    .add(XSCALE, "1")
                    .add(YSCALE, "1")
                    .get())
            .get();

    private static final Map<String, Function<String, ?>> STAGE_CONFIG_CONVERTERS = new HashMap<>();

    static {
        STAGE_CONFIG_CONVERTERS.put(LOCAL_COORD, Point2i::fromString);
    }

    private static final Map<String, Function<String, ?>> BG_DEF_CONVERTERS = new HashMap<>();

    static {
        BG_DEF_CONVERTERS.put(SPR, Function.identity());
        BG_DEF_CONVERTERS.put(DEBUG_BG, BOOLEAN_CONVERTER);
    }

    private static final Map<String, Function<String, ?>> SCALING_CONFIG_CONVERTERS = new HashMap<>();
    static {
        SCALING_CONFIG_CONVERTERS.put(XSCALE, Float::parseFloat);
        SCALING_CONFIG_CONVERTERS.put(YSCALE, Float::parseFloat);
    }

    private String name;
    private GroupedConfig stageConfig;
    private List<BG> backgrounds = new ArrayList<>();

    public StageDef(String name, GroupedConfig stageConfig) {
        this.name = name;
        this.stageConfig = new GroupedConfig().merge(DEFAULT_GENERAL_CONFIG).merge(stageConfig);
    }

    @Override
    public StageDef load() {
        Config generalInfoConfig = Optional.ofNullable(stageConfig.get(GROUP_GERERAL_INFO))
                .orElseGet(Config::new);
        String stageName = generalInfoConfig.get(STAGE_NAME);
        if (stageName != null) {
            this.name = stageName;
        }

        Config stageInfoConfig = Optional.ofNullable(stageConfig.get(GROUP_STAGE_INFO))
                .orElseGet(Config::new)
                .applyConverters(STAGE_CONFIG_CONVERTERS);
        stageConfig.add(GROUP_STAGE_INFO, stageInfoConfig);

        Config bgConfig = Optional.ofNullable(stageConfig.get(GROUP_BG_DEF))
                .orElseGet(Config::new)
                .applyConverters(BG_DEF_CONVERTERS);
        if (bgConfig.get(SPR) == null) {
            throw ConfigException.missing("Stage " + name, GROUP_BG_DEF, SPR);
        }
        stageConfig.add(GROUP_BG_DEF, bgConfig);

        Config scalingConfig = Optional.ofNullable(stageConfig.get(GROUP_SCALING_INFO))
                .orElseGet(Config::new)
                .applyConverters(SCALING_CONFIG_CONVERTERS);
        stageConfig.add(GROUP_SCALING_INFO, scalingConfig);

        // process BGs
        stageConfig.groups().forEach(group -> {
            if (!group.startsWith(BG.PREFIX)) {
                return;
            }
            String bgName = StringUtils.stripStart(group, BG.PREFIX).trim();
            BG bg = new BG(bgName, stageConfig.get(group));
            bg.load();
            backgrounds.add(bg);
        });
        return this;
    }

    public String getName() {
        return name;
    }

    public List<BG> getBackgrounds() {
        return backgrounds;
    }

    public String getSpriteFile() {
        return stageConfig.get(GROUP_BG_DEF).get(SPR);
    }

    public float getScalingX() {
        return stageConfig.get(GROUP_SCALING_INFO, XSCALE);
    }

    public float getScalingY() {
        return stageConfig.get(GROUP_SCALING_INFO, YSCALE);
    }

    public boolean isDebugBgEnabled() {
        return stageConfig.get(GROUP_BG_DEF).get(DEBUG_BG);
    }

    @Override
    public String toString() {
        return stageConfig.toString();
    }
}
