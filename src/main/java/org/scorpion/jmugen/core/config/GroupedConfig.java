package org.scorpion.jmugen.core.config;

import java.util.*;

public class GroupedConfig {

    private Map<String, Config> container = new LinkedHashMap<>();

    public static class Builder {

        GroupedConfig config = new GroupedConfig();

        public static Builder newConfig() {
            return new Builder();
        }

        public Builder add(String group, Config value) {
            config.add(group, value);
            return this;
        }

        public GroupedConfig get() {
            return config;
        }
    }

    void add(String group, Config config) {
        container.put(group, config);
    }

    public Config get(String group) {
        return container.get(group);
    }

    public <T> T get(String group, String... keys) {
        Config config = container.get(group);
        if (config == null) {
            return null;
        }
        return config.get(keys);
    }

    public GroupedConfig merge(GroupedConfig that) {
        that.container.forEach((group, config) -> {
            Config thisConfig = container.get(group);
            container.put(group, new Config().merge(thisConfig).merge(config));
        });
        return this;
    }

    public Set<String> groups() {
        return container.keySet();
    }

    public int size() {
        return container.size();
    }

    @Override
    public String toString() {
        return container.toString();
    }
}
