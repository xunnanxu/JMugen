package org.scorpion.jmugen.core.element;

import org.lwjgl.glfw.GLFW;
import org.scorpion.jmugen.core.config.SpriteId;
import org.scorpion.jmugen.core.config.StageDef;
import org.scorpion.jmugen.core.config.SystemConfig;
import org.scorpion.jmugen.core.data.GroupedContent;
import org.scorpion.jmugen.core.format.Sprite;
import org.scorpion.jmugen.core.maths.Matrix4f;
import org.scorpion.jmugen.core.maths.Point2f;
import org.scorpion.jmugen.core.maths.Point2i;
import org.scorpion.jmugen.core.maths.Vector3f;
import org.scorpion.jmugen.core.render.RenderObject;
import org.scorpion.jmugen.core.render.RenderProperties;
import org.scorpion.jmugen.io.input.file.SffFileReader;
import org.scorpion.jmugen.io.input.keyboard.KeyboardInputHandler;
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
    private Matrix4f scalingMatrix;
    private GroupedContent<? extends Sprite> sprites;
    private Map<StageDef.BG, RenderObject> objectMap = new LinkedHashMap<>();

    public Stage(SystemConfig systemConfig, StageDef config, KeyboardInputHandler keyboardInputHandler) {
        super(systemConfig, config, keyboardInputHandler);
    }

    @Override
    public Void load() {
        List<StageDef.BG> bgs = config.getBackgrounds();
        foregroundBGs = bgs.stream().filter(StageDef.BG::isForeground).collect(Collectors.toList());
        backgroundBGs = bgs.stream().filter(bg -> !bg.isForeground()).collect(Collectors.toList());

        String stagesHomeDir = systemConfig.getResourceHome() + File.separator + STAGES_DIR;
        File spriteFile = new File(stagesHomeDir, config.getSpriteFile());
        SffFileReader reader = new SffFileReader(new FileResource(spriteFile));

        sprites = reader.load();
        return null;
    }

    @Override
    public void init() {
        scalingMatrix = Matrix4f.resize(new Vector3f(config.getScalingX(), config.getScalingY(), 1f));
        for (StageDef.BG bg : backgroundBGs) {
            SpriteId spriteId = bg.getSpriteId();
            Sprite sprite = sprites.getElement(spriteId.group, spriteId.id);
            if (sprite == null) {
                continue;
            }
            Texture texture = new Texture(sprite).load();
            int width = sprite.getWidth();
            int height = sprite.getHeight();

            Point2i start = bg.getStartCoord();
            // Specifies the background element's starting position with respect to the top center of the screen
            // (positive y values go downward). The background elementfs axis (the one specified for the designated
            // sprite in the SFF) is placed at this starting position. If omitted, start defaults to 0,0.
            LOG.debug(bg.getName() + " start: " + start);
            LOG.debug(bg.getName() + " width: " + width);
            LOG.debug(bg.getName() + " height: " + height);

            Point2f offset = new Point2f(systemConfig.getGameWidth() / 2 - sprite.getXOffset() - start.x,
                    sprite.getYOffset() - start.y);

            RenderProperties renderProperties = new RenderProperties(systemConfig);
            renderProperties.colorBlending = bg.getTrans();
            renderProperties.alphaModifier = bg.getAlphaModifier();
            renderProperties.tileX = bg.getTileX();
            renderProperties.tileY = bg.getTileY();
            renderProperties.tileSpacingX = bg.getTileSpacingX();
            renderProperties.tileSpacingY = bg.getTileSpacingY();
            renderProperties.offset = offset;
            renderProperties.viewOffsetDelta = bg.getDelta();
            renderProperties.viewOffset = new Point2f(0, 0);

            // for mugen sprites, the "start" coordinates are related to the top center of the screen
            Mesh mesh = new RectangularMesh(
                    Vector3f.ZERO,
                    new Vector3f(width, -height, 0)
            );
            objectMap.put(bg, new RenderObject(mesh, texture, renderProperties));
        }
    }

    @Override
    public void render() {
        glDisable(GL_DEPTH_TEST);
        Shader bgShader = Shaders.getBackgroundShader();
        bgShader.enable();
        bgShader.setMatrix4f(Shader.SCALING_MATRIX, scalingMatrix);
        for (Map.Entry<StageDef.BG, RenderObject> entry : objectMap.entrySet()) {
            RenderObject renderObject = entry.getValue();
            renderObject.configShader(bgShader);
            renderObject.render(bgShader);
            renderObject.resetShader(bgShader);
        }
        bgShader.disable();
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void update() {
        if (keyboardInputHandler.isKeyPressed(GLFW.GLFW_KEY_LEFT) ||
                keyboardInputHandler.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            for (RenderObject renderObject : objectMap.values()) {
                RenderProperties props = renderObject.getRenderProperties();
                if (keyboardInputHandler.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
                    props.viewOffset.x = Math.min(props.viewOffset.x += 1, -config.getCameraBoundLeft());
                } else {
                    props.viewOffset.x = Math.max(props.viewOffset.x -= 1, -config.getCameraBoundRight());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Stage " + config.getName();
    }
}
