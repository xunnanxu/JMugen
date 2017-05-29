package org.scorpion.jmugen.core.format;

import com.google.common.io.LittleEndianDataInputStream;
import org.scorpion.jmugen.exception.GenericIOException;

import java.io.*;

public class PCXSprite implements Sprite {

    protected byte[] data;
    protected PCXPalette palette;
    protected PCXHeader pcxHeader;

    public PCXSprite(byte[] header, byte[] imageData, byte[] palette) {
        this.data = imageData;
        this.palette = new PCXPalette(palette);
        pcxHeader = new PCXHeader(header);
    }

    /**
     * 128 bytes
     * 0    1    manufacturer byte, must be 10 decimal
     * 1    1    PCX version number
     * 2    1    run length encoding byte, must be 1
     * 3    1    number of bits per pixel per bit plane
     * 4    8    image limits in pixels: Xmin, Ymin, Xmax, Ymax
     * 12   2    horizontal dots per inch when printed (unreliable)
     * 14   2    vertical dots per inch when printed (unreliable)
     * 16   48   16-color palette (16 RGB triples between 0-255)
     * 64   1    reserved 0
     * 65   1    number of bit planes
     * 66   2    bytes per image row
     * 68   2    16-color palette interpretation (unreliable)
     * 70   2    horizontal screen resolution - 1 (unreliable)
     * 72   2    vertical screen resolution - 1 (unreliable)
     * 74   54   reserved, must be zero
     */
    public static final class PCXHeader {

        public static final int HEADER_SIZE = 128;

        public final byte bitsPerPixelPerPlane;
        public final byte numOfColorPlanes;
        public final short width;
        public final short height;
        public final short bytesPerRow;

        public PCXHeader(byte[] pcxData) {
            try (LittleEndianDataInputStream input = new LittleEndianDataInputStream(new ByteArrayInputStream(pcxData))) {
                input.skipBytes(3);
                bitsPerPixelPerPlane = input.readByte();
                short xul = input.readShort();
                short yul = input.readShort();
                short xlr = input.readShort();
                short ylr = input.readShort();
                width = (short) (xlr - xul + 1);
                height = (short) (ylr - yul + 1);
                input.skipBytes(53);
                numOfColorPlanes = input.readByte();
                bytesPerRow = input.readShort();
                input.skipBytes(60);
            } catch (IOException e) {
                throw new GenericIOException(e);
            }
        }
    }

    public static final class PCXPalette {
        public static final int PALETTE_SIZE = 3 * 256;

        private byte[] palette;

        public PCXPalette(byte[] palette) {
            this.palette = palette;
        }

        public byte[] getRaw() {
            return palette;
        }

        public byte getR(int i) {
            return palette[3 * i];
        }

        public byte getG(int i) {
            return palette[3 * i + 1];
        }

        public byte getB(int i) {
            return palette[3 * i + 2];
        }

    }

    @Override
    public byte[] getData() {
        return data;
    }

    public PCXHeader getPCXHeader() {
        return pcxHeader;
    }

    public PCXPalette getPalette() {
        return palette;
    }

    @Override
    public int getWidth() {
        return pcxHeader.width;
    }

    @Override
    public int getHeight() {
        return pcxHeader.height;
    }
}
