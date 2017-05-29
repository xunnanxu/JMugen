package org.scorpion.jmugen.core.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    public void testConfig() {
        Config config = Config.Builder.newConfig()
                .add("foo", 2)
                .add("foo.bar.baz", "abc")
                .add("bar.baz", "def")
                .get();
        assertEquals("abc", config.get("foo", "bar", "baz"));
        assertEquals("def", config.get("bar", "baz"));
        assertEquals(Integer.valueOf(2), config.get("foo"));
        assertNull(config.get("foo", "bar"));
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    public void testMerge() {
        Config config = Config.Builder.newConfig()
                .add("foo", 2)
                .add("bar", 1)
                .add("baz.qux", "def")
                .get();
        Config toMerge = Config.Builder.newConfig()
                .add("foo.bar.baz", "abc")
                .add("bar", "a")
                .add("baz", "xyz")
                .get();
        config = config.merge(toMerge);
        assertEquals("abc", config.get("foo", "bar", "baz"));
        assertEquals(Integer.valueOf(2), config.get("foo"));
        assertEquals("a", config.get("bar"));
        assertEquals("xyz", config.get("baz"));
        assertEquals("def", config.get("baz", "qux"));

        assertEquals("abc", toMerge.get("foo", "bar", "baz"));
        assertEquals("a", toMerge.get("bar"));
        assertEquals("xyz", toMerge.get("baz"));
    }

}