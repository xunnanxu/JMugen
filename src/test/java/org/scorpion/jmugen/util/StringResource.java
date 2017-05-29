package org.scorpion.jmugen.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class StringResource implements Resource {

    InputStream in;

    public StringResource(String str) {
        in = new ByteArrayInputStream(str.getBytes(Charset.forName("UTF-8")));
    }

    @Override
    public InputStream load() {
        return in;
    }

    @Override
    public String getName() {
        return "string";
    }
}
