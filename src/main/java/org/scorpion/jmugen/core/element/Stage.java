package org.scorpion.jmugen.core.element;

import org.scorpion.jmugen.core.config.ConfigKeys;
import org.scorpion.jmugen.core.config.SpriteId;
import org.scorpion.jmugen.core.config.StageDef;
import org.scorpion.jmugen.core.data.GroupedContent;
import org.scorpion.jmugen.core.format.Sprite;
import org.scorpion.jmugen.core.maths.Point2f;
import org.scorpion.jmugen.core.maths.Vector3f;
import org.scorpion.jmugen.core.render.RenderObject;
import org.scorpion.jmugen.io.input.file.SffFileReader;
import org.scorpion.jmugen.render.*;
import org.scorpion.jmugen.util.ClasspathResource;
import org.scorpion.jmugen.util.FileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Stage extends GameObject<StageDef> {

    private static final String STAGES_DIR = "stages";
    private static final Logger LOG = LoggerFactory.getLogger(Stage.class);

    private List<StageDef.BG> foregroundBGs;
    private List<StageDef.BG> backgroundBGs;
    private GroupedContent<? extends Sprite> sprites;
    private Map<StageDef.BG, RenderObject> objectMap = new LinkedHashMap<>();

    public Stage(Map<String, String> globalConfig, StageDef config) {
        super(globalConfig, config);
    }

    @Override
    public Void load() {
        List<StageDef.BG> bgs = config.getBackgrounds();
        foregroundBGs = bgs.stream().filter(bg -> bg.isForeground()).collect(Collectors.toList());
        backgroundBGs = bgs.stream().filter(bg -> !bg.isForeground()).collect(Collectors.toList());

        String stagesHomeDir = globalConfig.get(ConfigKeys.RESOURCE_HOME) + File.separator + STAGES_DIR;
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
            Texture texture = new Texture(sprite).load();
            int width = sprite.getWidth();
            int height = sprite.getHeight();

            Point2f start = bg.getStartCoord();
            // Specifies the background element's starting position with respect to the top center of the screen
            // (positive y values go downward). The background elementfs axis (the one specified for the designated
            // sprite in the SFF) is placed at this starting position. If omitted, start defaults to 0,0.
            LOG.debug(bg.getName() + " start: " + start);
            LOG.debug(bg.getName() + " width: " + width);
            LOG.debug(bg.getName() + " height: " + height);

            Mesh mesh = new RectangularMesh(
                    new Vector3f((320 - width) / 2, -start.y, 0),
                    new Vector3f((320 + width) / 2, -start.y - height, 0)
            );
            objectMap.put(bg, new RenderObject(mesh, texture));
        }
    }

    @Override
    public void render() {
        for (Map.Entry<StageDef.BG, RenderObject> entry : objectMap.entrySet()) {
            StageDef.BG bg = entry.getKey();
//            System.out.println(bg.getSpriteId());
            Shader bgShader = Shaders.getBackgroundShader();
            bgShader.enable();
            bgShader.setMatrix4f("vw_mat", Camera.INSTANCE.getViewMatrix());
            entry.getValue().render();
            bgShader.disable();
        }
    }

    @Override
    public void update() {

    }

    @Override
    public String toString() {
        return "Stage " + config.getName();
    }
}
