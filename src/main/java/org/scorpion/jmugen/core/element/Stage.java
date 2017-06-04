package org.scorpion.jmugen.core.element;

import org.scorpion.jmugen.core.config.SystemConfig;
import org.scorpion.jmugen.core.config.SpriteId;
import org.scorpion.jmugen.core.config.StageDef;
import org.scorpion.jmugen.core.data.GroupedContent;
import org.scorpion.jmugen.core.format.Sprite;
import org.scorpion.jmugen.core.maths.Point2f;
import org.scorpion.jmugen.core.maths.Vector3f;
import org.scorpion.jmugen.core.render.RenderObject;
import org.scorpion.jmugen.io.input.file.SffFileReader;
import org.scorpion.jmugen.render.*;
import org.scorpion.jmugen.util.FileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

public class Stage extends GameObject<StageDef> {

    private static final String STAGES_DIR = "stages";
    private static final Logger LOG = LoggerFactory.getLogger(Stage.class);

    private List<StageDef.BG> foregroundBGs;
    private List<StageDef.BG> backgroundBGs;
    private GroupedContent<? extends Sprite> sprites;
    private Map<StageDef.BG, RenderObject> objectMap = new LinkedHashMap<>();

    public Stage(SystemConfig systemConfig, StageDef config) {
        super(systemConfig, config);
    }

    @Override
    public Void load() {
        List<StageDef.BG> bgs = config.getBackgrounds();
        foregroundBGs = bgs.stream().filter(bg -> bg.isForeground()).collect(Collectors.toList());
        backgroundBGs = bgs.stream().filter(bg -> !bg.isForeground()).collect(Collectors.toList());

        String stagesHomeDir = systemConfig.getResourceHome() + File.separator + STAGES_DIR;
        File spriteFile = new File(stagesHomeDir, config.getSpriteFile());
        SffFileReader reader = new SffFileReader(new FileResource(spriteFile));

        sprites = reader.load();
        return null;
    }

    @Override
    public void init() {
        for (StageDef.BG bg : backgroundBGs) {
            SpriteId spriteId = bg.getSpriteId();
            Sprite sprite = sprites.getElement(spriteId.group, spriteId.id);
            if (sprite == null) {
                continue;
            }
            Texture.Properties textureProps = new Texture.Properties();
            textureProps.repeatX = bg.getTileX() != 0;
            Texture texture = new Texture(sprite, textureProps).load();
            int width = sprite.getWidth();
            int height = sprite.getHeight();

            Point2f start = bg.getStartCoord();
            // Specifies the background element's starting position with respect to the top center of the screen
            // (positive y values go downward). The background elementfs axis (the one specified for the designated
            // sprite in the SFF) is placed at this starting position. If omitted, start defaults to 0,0.
            LOG.debug(bg.getName() + " start: " + start);
            LOG.debug(bg.getName() + " width: " + width);
            LOG.debug(bg.getName() + " height: " + height);

            // for mugen sprites, the "start" coordinates are related to the top center of the screen
            Mesh mesh = new RectangularMesh(
                    new Vector3f(systemConfig.getGameWidth() / 2 - sprite.getXOffset(),
                            -start.y + sprite.getYOffset(), 0),
                    new Vector3f(systemConfig.getGameWidth() / 2 - sprite.getXOffset() + sprite.getWidth(),
                            -start.y - height + sprite.getYOffset(), 0)
            );
            objectMap.put(bg, new RenderObject(mesh, texture));
        }
    }

    @Override
    public void render() {
        glDisable(GL_DEPTH_TEST);
        for (Map.Entry<StageDef.BG, RenderObject> entry : objectMap.entrySet()) {
            StageDef.BG bg = entry.getKey();
            Shader bgShader = Shaders.getBackgroundShader();
            bgShader.enable();
            bgShader.setMatrix4f("vw_mat", Camera.INSTANCE.getViewMatrix());
            entry.getValue().render();
            bgShader.disable();
        }
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void update() {

    }

    @Override
    public String toString() {
        return "Stage " + config.getName();
    }
}
