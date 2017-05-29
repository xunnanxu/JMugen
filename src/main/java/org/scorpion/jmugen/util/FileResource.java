package org.scorpion.jmugen.util;

import org.scorpion.jmugen.exception.GenericIOException;

import java.io.*;

public class FileResource implements Resource {

    private static final String CURRENT_DIR = new File("").getAbsolutePath();

    private File file;

    public FileResource(String file) {
        this(new File(file));
    }

    public FileResource(File file) {
        if (file.isAbsolute()) {
            this.file = file;
        } else {
            this.file = new File(CURRENT_DIR + File.separator + file.getPath());
        }
        this.file = file;
    }

    @Override
    public InputStream load() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new GenericIOException(e);
        }
    }

    public RandomAccessFile loadRandomAccessFile() {
        try {
            return new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            throw new GenericIOException(e);
        }
    }

    @Override
    public String getName() {
        return file.getAbsolutePath();
    }

    @Override
    public String toString() {
        return getName();
    }
}
