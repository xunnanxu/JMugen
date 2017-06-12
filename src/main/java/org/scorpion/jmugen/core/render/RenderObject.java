package org.scorpion.jmugen.core.render;

import org.scorpion.jmugen.core.config.StageDef;
import org.scorpion.jmugen.core.maths.Matrix4f;
import org.scorpion.jmugen.core.maths.Vector3f;
import org.scorpion.jmugen.render.Mesh;
import org.scorpion.jmugen.render.Shader;
import org.scorpion.jmugen.render.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

public class RenderObject {

    private final Mesh mesh;
    private final Texture texture;
    private final RenderProperties renderProperties;
    private final Matrix4f modelMatrix = Matrix4f.identity();

    public RenderObject(Mesh mesh, Texture texture, RenderProperties renderProperties) {
        this.mesh = mesh;
        this.texture = texture;
        this.renderProperties = renderProperties;
    }

    public void configShader(Shader shader) {
        if (renderProperties.colorBlending == StageDef.BG.Trans.ADDALPHA) {
            shader.setUniform1f(Shader.ALPHA_MODIFIER, renderProperties.alphaModifier);
        }
    }

    public void render(Shader shader) {
        glEnable(GL_BLEND);
        mesh.bind();
        switch (renderProperties.colorBlending) {
            case NONE:
                glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
                texture.bind();
                drawMesh(shader);
                texture.unbind();
                break;
            case ADD:
                glBlendFuncSeparate(GL_SRC_COLOR, GL_ONE_MINUS_SRC_COLOR, GL_ONE, GL_ZERO);
                texture.bind();
                drawMesh(shader);
                texture.unbind();
                break;
            case ADD1:
                glBlendColor(1.0f, 1.0f, 1.0f, 0.5f);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                drawMesh(shader);
                glBlendFuncSeparate(GL_SRC_COLOR, GL_ONE_MINUS_SRC_COLOR, GL_ONE, GL_ZERO);
                texture.bind();
                drawMesh(shader);
                texture.unbind();
                break;
            case ADDALPHA:
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                texture.bind();
                drawMesh(shader);
                texture.unbind();
                break;
            case SUB:
                glBlendEquation(GL_FUNC_SUBTRACT);
                glBlendFuncSeparate(GL_ONE_MINUS_SRC_COLOR, GL_ONE_MINUS_DST_COLOR,
                        GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);
                texture.bind();
                drawMesh(shader);
                texture.unbind();
                glBlendEquation(GL_FUNC_ADD);
                break;
        }
        mesh.unbind();
        glDisable(GL_BLEND);
    }

    public void resetShader(Shader shader) {
        shader.setUniform1f(Shader.ALPHA_MODIFIER, 1f);
    }

    private void drawMesh(Shader shader) {
        if (renderProperties.tileX == 0 && renderProperties.tileY == 0) {
            modelMatrix.translateTo(new Vector3f(renderProperties.offset.x, renderProperties.offset.y, 0));
            shader.setMatrix4f(Shader.MODEL_MATRIX, modelMatrix);
            mesh.draw();
            return;
        }
        int viewPortWidth = renderProperties.camera.getViewportWidth();
        int viewPortHeight = renderProperties.camera.getViewportHeight();
        int numTilesLeftX = renderProperties.tileX;
        if (numTilesLeftX == 0) {
            numTilesLeftX = 1;
        }
        int numTilesLeftY = renderProperties.tileY;
        if (numTilesLeftY == 0) {
            numTilesLeftY = 1;
        }
        int firstTileLeftX = renderProperties.offset.x;
        int firstTileTopY = renderProperties.offset.y;
        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();
        while (firstTileLeftX + textureWidth + renderProperties.tileSpacingX >= 0 &&
                (numTilesLeftX > 1 || renderProperties.tileX == 1)) {
            numTilesLeftX--;
            if (numTilesLeftX < 0) {
                numTilesLeftX = 0;
            }
            firstTileLeftX -= textureWidth + renderProperties.tileSpacingX;
        }
        while (firstTileTopY - textureWidth - renderProperties.tileSpacingX <= 0 &&
                (numTilesLeftY > 1 || renderProperties.tileY == 1)) {
            numTilesLeftY--;
            if (numTilesLeftY < 0) {
                numTilesLeftY = 0;
            }
            firstTileTopY += textureHeight + renderProperties.tileSpacingY;
        }
        for (int i = 0; renderProperties.tileX == 1 || i <= renderProperties.tileX; i++) {
            int x = firstTileLeftX + i * (textureWidth + renderProperties.tileSpacingX);
            if (x >= viewPortWidth) {
                break;
            }
            for (int j = 0; renderProperties.tileY == 1 || j <= renderProperties.tileY; j++) {
                int y = firstTileTopY - j * (textureHeight + renderProperties.tileSpacingY);
                if (y <= -viewPortHeight) {
                    break;
                }
                modelMatrix.translateTo(new Vector3f(x, y, 0));
                shader.setMatrix4f(Shader.MODEL_MATRIX, modelMatrix);
                mesh.draw();
            }
        }
    }
}
