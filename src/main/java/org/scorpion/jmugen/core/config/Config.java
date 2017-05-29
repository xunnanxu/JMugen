package org.scorpion.jmugen.core.config;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;

public class Config {

    private Map<String, Object> container = new HashMap<>();

    public Config() {
    }

    public Config(String key, Object value) {
        container.put(key, value);
    }

    public static class Builder {

        Config config = new Config();

        public static Builder newConfig() {
            return new Builder();
        }

        public Builder add(String key, Object value) {
            String[] keys = StringUtils.split(key, '.');
            config.add(keys, value);
            return this;
        }

        public Config get() {
            return config;
        }
    }

    Config add(String[] keys, Object value) {
        return add(Arrays.asList(keys), value);
    }

    @SuppressWarnings("unchecked")
    Config add(List<String> keys, Object value) {
        if (keys.isEmpty()) {
            return this;
        }
        String key = keys.get(0);
        List<String> restOfKeys = keys.subList(1, keys.size());
        Object valueOrGroup = container.get(key);
        if (valueOrGroup == null) {
            if (restOfKeys.isEmpty()) {
                container.put(key, value);
                return this;
            }
            Config sub = new Config();
            container.put(key, sub);
            sub.add(restOfKeys, value);
            return this;
        }
        // overwrite existing value
        if (restOfKeys.isEmpty()) {
            container.put(key, value);
            return this;
        }
        if (valueOrGroup instanceof Config) {
            ((Config) valueOrGroup).add(restOfKeys, value);
            return this;
        }
        // key has multiple layers. escalate value to group
        Config sub = convertToGroup(key);
        sub.add(restOfKeys, value);
        return this;

    }

    Config convertToGroup(String key) {
        Object value = container.remove(key);
        Config group = new Config();
        group.container.put(null, value);
        container.put(key, group);
        return group;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String... keys) {
        Config currentLevel = this;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Object value = currentLevel.container.get(key);
            if (i == keys.length - 1) {
                if (value instanceof Config) {
                    return (T) ((Config) value).container.get(null);
                }
                return (T) value;
            }
            if (!(value instanceof Config)) {
                return null;
            }
            currentLevel = (Config) value;
        }
        return null;
    }

    public Config merge(Config that) {
        if (that == null) {
            return this;
        }
        for (Map.Entry<String, Object> entry : that.container.entrySet()) {
            String key = entry.getKey();
            Object thisValue = container.get(key);
            Object thatValue = entry.getValue();
            if (thisValue == null || !(thisValue instanceof Config) && !(thatValue instanceof Config)) {
                container.put(key, thatValue);
                continue;
            }
            if (!(thisValue instanceof Config)) {
                Config sub = convertToGroup(key);
                sub.merge((Config) thatValue);
                continue;
            }
            if (!(thatValue instanceof Config)) {
                ((Config) thisValue).container.put(null, thatValue);
                continue;
            }
            ((Config) thisValue).merge((Config) thatValue);
        }
        return this;
    }

    public <T> Config applyConverters(Map<String, ? extends Function<? super T, ?>> converters) {
        converters.forEach((key, converter) -> {
            String[] keys = StringUtils.split(key, '.');
            T config = get(keys);
            if (config != null) {
                add(keys, converter.apply(config));
            }
        });
        return this;
    }

    @Override
    public String toString() {
        return container.toString();
    }
}
