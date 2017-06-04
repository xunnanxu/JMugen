package org.scorpion.jmugen.io.input.file;

import com.google.common.io.LittleEndianDataInputStream;
import org.scorpion.jmugen.core.data.GroupedContent;
import org.scorpion.jmugen.core.format.PCXSprite;
import org.scorpion.jmugen.core.format.Sprite;
import org.scorpion.jmugen.core.maths.Point2i;
import org.scorpion.jmugen.exception.GenericIOException;
import org.scorpion.jmugen.io.input.util.IOHelpers;
import org.scorpion.jmugen.util.FileResource;
import org.scorpion.jmugen.util.Loadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * --| SFF file structure
 |--------------------------------------------------*\
 Version 1.01
 HEADER (512 bytes)
 ------
 Bytes
 00-11 "ElecbyteSpr\0" signature [12]
 12-15 1 verhi, 1 verlo, 1 verlo2, 1 verlo3 [04]
 16-19 Number of groups [04]
 20-24 Number of images [04]
 24-27 File offset where first subfile is located [04]
 28-31 Size of subheader in bytes [04]
 32 Palette type (1=SPRPALTYPE_SHARED or 0=SPRPALTYPE_INDIV) [01]
 33-35 Blank; set to zero [03]
 36-511 Blank; can be used for comments [476]

 SUBFILEHEADER (32 bytes)
 -------
 Bytes
 00-03 File offset where next subfile in the "linked list" is [04]
 located. Null if last subfile

 04-07 Subfile length (not including header) [04]
 Length is 0 if it is a linked sprite
 08-09 Image axis X coordinate [02]
 10-11 Image axis Y coordinate [02]
 12-13 Group number [02]
 14-15 Image number (in the group) [02]
 16-17 Index of previous copy of sprite (linked sprites only) [02]
 This is the actual
 18 True if palette is same as previous image [01]
 19-31 Blank; can be used for comments [14]
 32- PCX graphic data. If palette data is available, it is the last
 768 bytes.
 *--------------------------------------------------------------------------*/
public class SffFileReader implements Loadable<GroupedContent<? extends Sprite>> {

    public static final int SFF_HEADER_SIZE = 512;
    public static final int SFF_SPRITE_HEADER_SIZE = 32;

    private static final Logger LOG = LoggerFactory.getLogger(SffFileReader.class);

    protected FileResource resource;

    public SffFileReader(FileResource resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        if (resource == null) {
            return super.toString();
        }
        else {
            return resource.getName();
        }
    }

    @Override
    public GroupedContent<? extends Sprite> load() {
        RandomAccessFile in = resource.loadRandomAccessFile();
        try {
            SffHeader sffHeader = parseHeader(in);
            GroupedContent<PCXSprite> sprites = getAllSprites(in, sffHeader);
            return sprites;
        } catch (IOException e) {
            throw new GenericIOException(e);
        }
    }

    SffHeader parseHeader(DataInput in) throws IOException {
        byte[] header = new byte[SFF_HEADER_SIZE];
        try {
            in.readFully(header);
        } catch (EOFException e) {
            throw new GenericIOException("Invalid SFF file (corrupted header): " + resource.getName());
        }
        return new SffHeader(header);
    }

    GroupedContent<PCXSprite> getAllSprites(RandomAccessFile in, SffHeader sffHeader) throws IOException {
        byte[] prevPal = null;
        long endOffset = in.length() - 1;
        in.seek(sffHeader.firstSubFileOffset);
        GroupedContent<PCXSprite> sprites = new GroupedContent<>();
        byte[] spriteHeaderBuffer = new byte[SFF_SPRITE_HEADER_SIZE];
        int offset = sffHeader.firstSubFileOffset;

        while (true) {
            int headerBufferBytes = in.read(spriteHeaderBuffer);
            if (headerBufferBytes == -1) {
                break;
            }
            if (headerBufferBytes < SFF_SPRITE_HEADER_SIZE) {
                LOG.error(String.format(
                        "Unable to read sub file header at 0x%H: requested bytes: %d bytes read: %d", offset,
                        SFF_SPRITE_HEADER_SIZE, headerBufferBytes));
                break;
            }
            offset += headerBufferBytes;
            SpriteHeader spriteHeader = new SpriteHeader(spriteHeaderBuffer);
            int nextPos = spriteHeader.nextPosition;
            int imageSize = spriteHeader.subFileLen -
                    PCXSprite.PCXHeader.HEADER_SIZE -
                    (!spriteHeader.usePrevPal ? PCXSprite.PCXPalette.PALETTE_SIZE : 0);
            int group = spriteHeader.group;
            int index = spriteHeader.index;
            Point2i spriteOffset = new Point2i(spriteHeader.xAxis, spriteHeader.yAxis);

            byte[] header = new byte[PCXSprite.PCXHeader.HEADER_SIZE];
            byte[] imageData = new byte[imageSize];
            byte[] palette;
            in.read(header);
            int bytesRead = in.read(imageData);
            if (!spriteHeader.usePrevPal) {
                palette = new byte[PCXSprite.PCXPalette.PALETTE_SIZE];
                in.read(palette);
            }
            else {
                palette = prevPal;
            }
            if (bytesRead < imageSize && !spriteHeader.usePrevPal
                    && bytesRead + PCXSprite.PCXPalette.PALETTE_SIZE != imageSize) {
                LOG.warn(String.format(
                        "Unable to read sprite data at 0x%H requested bytes: %d bytes read: %d", offset,
                        imageSize, bytesRead));
                in.seek(nextPos);
                offset = nextPos;
                continue;
            }
            in.seek(nextPos);

            PCXSprite sprite = null;
            if (imageSize == 0) {
                // copy of a previous sprite
                int prev = spriteHeader.indexOfCopy;
                if (prev < 0 || !sprites.hasElement(group, prev)) {
                    LOG.warn(String.format(
                            "Unable to find the previous sprite %d as requested by sprite %d/%d at 0x%H. Sprite ignored.", prev,
                            group, index, offset));
                    offset = nextPos;
                    continue;
                }
                sprite = sprites.getElement(group, prev);
            }
            else if (spriteHeader.usePrevPal) {
                if (prevPal == null) {
                    LOG.warn(String.format("Unable to use the previous palette as requested by sprite %d/%d at 0x%H since it doesn't exist. Sprite ignored.",
                            group, index, offset));
                    offset = nextPos;
                    continue;
                }
                sprite = new PCXSprite(header, imageData, prevPal, spriteOffset);
            }
            else {
                if (imageData.length < PCXSprite.PCXHeader.HEADER_SIZE) {
                    LOG.warn(String.format("Sprite %d/%d size invalid (%d) at 0x%H. Sprite ignored.",
                            group, index, imageData.length, offset));
                    offset = nextPos;
                    continue;
                }
                sprite = new PCXSprite(header, imageData, palette, spriteOffset);
                if (sprite.getPalette() != null) {
                    prevPal = sprite.getPalette().getRaw();
                }
            }
            offset = nextPos;
            sprites.putElement(group, index, sprite);
        }
        return sprites;
    }

    public static class SffHeader {
        public final char[] signature = new char[12];
        public final int verHi;                               // 1
        public final int verLo;                               // 1
        public final int verLo2;                              // 1
        public final int verLo3;                              // 1
        public final int group;                               // 4
        public final int index;                               // 4
        public final int firstSubFileOffset;                  // 4
        public final int subHeaderSize;                       // 4
        public final int paletteType;                         // 1
                                                        // 0: SPRPALTYPE_INDIV
                                                        // 1: SPRPALTYPE_SHARED
        public final char[] blank = new char[3];
        public final char[] comment = new char[476];

        public SffHeader(byte[] header) {
            try (LittleEndianDataInputStream in = new LittleEndianDataInputStream(new ByteArrayInputStream(header))) {
                IOHelpers.readChars(in, signature);
                verHi = in.readUnsignedByte();
                verLo = in.readUnsignedByte();
                verLo2 = in.readUnsignedByte();
                verLo3 = in.readUnsignedByte();
                group = in.readInt();
                index = in.readInt();
                firstSubFileOffset = in.readInt();
                subHeaderSize = in.readInt();
                paletteType = in.readByte();
                IOHelpers.readChars(in, blank);
                IOHelpers.readChars(in, comment);
            } catch (IOException e) {
                throw new GenericIOException(e);
            }
        }
    }

    public static class SpriteHeader {
        public final int nextPosition;                // 4
        public final int subFileLen;                  // 4 (header excluded)
        public final short xAxis;                     // 2
        public final short yAxis;                     // 2
        public final short group;                     // 2
        public final short index;                     // 2
        public final short indexOfCopy;               // 2
        public final boolean usePrevPal;              // 1
        public final char[] comment = new char[13];

        public SpriteHeader(byte[] header) {
            try (LittleEndianDataInputStream in = new LittleEndianDataInputStream(new ByteArrayInputStream(header))) {
                nextPosition = in.readInt();
                subFileLen = in.readInt();
                xAxis = in.readShort();
                yAxis = in.readShort();
                group = in.readShort();
                index = in.readShort();
                indexOfCopy = in.readShort();
                usePrevPal = in.readBoolean();
                IOHelpers.readChars(in, comment);
            } catch (IOException e) {
                throw new GenericIOException(e);
            }
        }

    }

}
