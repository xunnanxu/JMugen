package org.scorpion.jmugen.core.config;

import org.junit.Test;
import org.scorpion.jmugen.util.StringResource;

import static org.junit.Assert.*;

public class DefParserTest {

    @Test
    public void testParse() throws Exception {
        String str = ";comment\n[GROUP]; group comment\n;blfd\n\nstage.1=abc;stage config\n[abc]\na=b\naaa";
        GroupedConfig config = DefParser.parse(new StringResource(str));
        assertEquals("abc", config.get("group", "stage", "1"));
        assertEquals("b", config.get("abc", "a"));
    }
}