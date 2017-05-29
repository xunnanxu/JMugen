package org.scorpion.jmugen.core.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class GroupedConfigTest {

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    public void testGroupedConfig() {
        GroupedConfig groupedConfig = GroupedConfig.Builder.newConfig()
                .add("foo", Config.Builder.newConfig().add("bar.baz", 1).get())
                .add("bar", Config.Builder.newConfig().add("a.b", 2).get())
                .get();
        assertEquals(2, groupedConfig.size());
        assertEquals(Integer.valueOf(1), groupedConfig.get("foo", "bar", "baz"));
        assertEquals(Integer.valueOf(2), groupedConfig.get("bar", "a", "b"));
    }

}