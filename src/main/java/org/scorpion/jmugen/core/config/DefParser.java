package org.scorpion.jmugen.core.config;

import org.apache.commons.lang3.StringUtils;
import org.scorpion.jmugen.exception.GenericIOException;
import org.scorpion.jmugen.util.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefParser {

    private static final String COMMENT_PATTERN = "\\s*(;.*)?";
    private static final Pattern COMMENT_OR_EMPTY = Pattern.compile(COMMENT_PATTERN);
    private static final Pattern GROUP = Pattern.compile("\\[([^\\[\\]]*)\\]");
    private static final Pattern KEY_VALUE_PAIR = Pattern.compile("([^;]*)=([^;]*)" +  COMMENT_PATTERN);

    private static final Logger LOG = LoggerFactory.getLogger(DefParser.class);

    public static GroupedConfig parse(Resource resource) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(resource.load()))) {
            GroupedConfig.Builder configBuilder = GroupedConfig.Builder.newConfig();
            String line;
            String group = null;
            Config.Builder groupConfigBuilder = null;
            while ((line = in.readLine()) != null) {
                if (COMMENT_OR_EMPTY.matcher(line).matches()) {
                    continue;
                }
                {
                    Matcher groupMatcher = GROUP.matcher(line);
                    if (groupMatcher.find()) {
                        if (groupConfigBuilder != null) {
                            configBuilder.add(group, groupConfigBuilder.get());
                            groupConfigBuilder = Config.Builder.newConfig();
                        }
                        group = groupMatcher.group(1).trim().toLowerCase();
                        continue;
                    }
                }
                {
                    Matcher keyValueMatcher = KEY_VALUE_PAIR.matcher(line);
                    if (keyValueMatcher.find()) {
                        String key = keyValueMatcher.group(1).trim().toLowerCase();
                        String value = keyValueMatcher.group(2).trim();
                        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                            value = value.substring(1, value.length() - 1);
                        }
                        if (groupConfigBuilder == null) {
                            groupConfigBuilder = Config.Builder.newConfig();
                        }
                        groupConfigBuilder.add(key, value);
                        continue;
                    }
                }
                LOG.warn(String.format("Unrecognizable config (%s): %s", resource.getName(), line));
            }
            // add the last group
            if (groupConfigBuilder != null) {
                configBuilder.add(group, groupConfigBuilder.get());
            }
            return configBuilder.get();
        } catch (IOException e) {
            throw new GenericIOException(e);
        }
    }

}
