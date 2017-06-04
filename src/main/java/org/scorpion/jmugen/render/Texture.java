package org.scorpion.jmugen.render;

import org.scorpion.jmugen.core.format.PCXSprite;
import org.scorpion.jmugen.core.format.Sprite;
import org.scorpion.jmugen.exception.GenericIOException;
import org.scorpion.jmugen.exception.ImageRenderException;
import org.scorpion.jmugen.util.BufferUtils;
import org.scorpion.jmugen.util.Loadable;
import org.scorpion.jmugen.util.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class Texture implements Loadable<Texture> {

    public static class Properties {

        public boolean repeatX;
        public boolean repeatY;

        public static final Properties DEFAULT = new Properties();

    }

    protected int width, height;
    protected int textureId;
    protected BufferedImage bufferedImage;
    protected Sprite sprite;
    protected Properties properties;

    public Texture(Resource resource) {
        this.properties = Properties.DEFAULT;
        try {
            bufferedImage = ImageIO.read(resource.load());
        } catch (IOException e) {
            throw new GenericIOException(e);
        }
    }

    public Texture(Sprite sprite, Properties properties) {
        this.sprite = sprite;
        this.properties = properties;
    }

    @Override
    public Texture load() {
        if (bufferedImage != null) {
            loadBufferedImage();
        } else if (sprite != null) {
            if (sprite instanceof PCXSprite) {
                loadPCX((PCXSprite) sprite);
            } else {
                throw new UnsupportedOperationException("Sprite not supported");
            }
        }
        return this;
    }

    void loadPCX(PCXSprite pcxSprite) {
        PCXSprite.PCXHeader header = pcxSprite.getPCXHeader();
        PCXSprite.PCXPalette palette = pcxSprite.getPalette();
        byte[] data = pcxSprite.getData();
        width = header.width;
        height = header.height;
        int[] abgr = new int[width * height];
        int x = 0, y = 0, index = 0;
        while (y < height && index < data.length) {
            int value = data[index++] & 0xFF;
            int count = 1;
            // if the byte has the top two bits set
            if ((value & 0b11000000) == 0b11000000) {
                count = value & 0b00111111;
                value = data[index++] & 0xFF;
            }
            for (int i = 0; i < count && x < width; i++, x++) {
                if (value <= 0) {
                    continue;
                }
                int r = palette.getR(value) & 0xFF;
                int g = palette.getG(value) & 0xFF;
                int b = palette.getB(value) & 0xFF;
                abgr[x + y * width] = 255 << 24 | b << 16 | g << 8 | r;
            }
            if (x == width) {
                x = 0;
                y++;
            }
        }
        initTexture(abgr);
    }

    void loadBufferedImage() {
        int[] abgr;
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        abgr = new int[width * height];
        Raster raster = bufferedImage.getRaster();
        ColorModel colorModel = bufferedImage.getColorModel();
        int nbands = raster.getNumBands();
        int dataType = raster.getDataBuffer().getDataType();
        Object data;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                data = new byte[nbands];
                break;
            case DataBuffer.TYPE_USHORT:
                data = new short[nbands];
                break;
            case DataBuffer.TYPE_INT:
                data = new int[nbands];
                break;
            case DataBuffer.TYPE_FLOAT:
                data = new float[nbands];
                break;
            case DataBuffer.TYPE_DOUBLE:
                data = new double[nbands];
                break;
            default:
                throw new ImageRenderException("Illegal image (Unidentifiable buffer type)", null, true, false);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Object dataElements = raster.getDataElements(x, y, data);
                int a = colorModel.getAlpha(dataElements);
                int b = colorModel.getBlue(dataElements);
                int g = colorModel.getGreen(dataElements);
                int r = colorModel.getRed(dataElements);
                abgr[x + y * width] = a << 24 | b << 16 | g << 8 | r;
            }
        }
        initTexture(abgr);
    }

    private void initTexture(int[] abgr) {
        textureId = glGenTextures();
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.toIntBuffer(abgr));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, properties.repeatX ? GL_REPEAT : GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, properties.repeatY ? GL_REPEAT : GL_CLAMP_TO_BORDER);
        unbind();
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

}
