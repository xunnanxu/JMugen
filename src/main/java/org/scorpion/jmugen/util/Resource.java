package org.scorpion.jmugen.util;

import java.io.InputStream;

public interface Resource {

    InputStream load();

    String getName();

}
