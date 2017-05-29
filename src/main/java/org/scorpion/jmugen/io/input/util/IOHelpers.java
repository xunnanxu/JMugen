package org.scorpion.jmugen.io.input.util;

import java.io.IOException;
import java.io.InputStream;

public class IOHelpers {

    public static int readChars(InputStream in, char[] buf) throws IOException {
        int len = buf.length;
        if (len == 0) {
            return 0;
        }
        int i = 0;
        for (; i < len; i++) {
            int b = in.read();
            if (b == -1) {
                break;
            }
            buf[i] = (char) b;
        }
        return i == 0 ? -1 : i + 1;
    }

    public static char[] readChars(InputStream in, int length) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int b = in.read();
            if (b == -1) {
                break;
            }
            sb.append((char) b);
        }
        return sb.toString().toCharArray();
    }

}
