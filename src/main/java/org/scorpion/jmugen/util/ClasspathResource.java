package org.scorpion.jmugen.util;

import org.scorpion.jmugen.exception.GenericIOException;

import java.io.InputStream;

public class ClasspathResource implements Resource {

    private String path;

    public ClasspathResource(String path) {
        this.path = path;
    }

    @Override
    public InputStream load() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new GenericIOException(path + " not found");
        }
        return in;
    }

    @Override
    public String getName() {
        return path;
    }

    @Override
    public String toString() {
        return getName();
    }
}
