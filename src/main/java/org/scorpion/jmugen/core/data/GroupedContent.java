package org.scorpion.jmugen.core.data;

import java.util.HashMap;
import java.util.Map;

public class GroupedContent<T> {

    protected Map<Integer, Map<Integer, T>> groups = new HashMap<>();

    public GroupedContent() {
    }

    public boolean isEmpty() {
        return groups.isEmpty();
    }

    public int size() {
        return groups.size();
    }

    public Map<Integer, T> getGroup(Integer group) {
        return groups.get(group);
    }

    public T getElement(Integer group, Integer id) {
        Map<Integer, T> grp = getGroup(group);
        if (grp == null) {
            return null;
        }
        return grp.get(id);
    }

    public T putElement(Integer group, Integer id, T value) {
        Map<Integer, T> grp = getGroup(group);
        if (grp == null) {
            grp = new HashMap<>();
            groups.put(group, grp);
        }
        return grp.put(id, value);
    }

    public boolean hasGroup(Integer group) {
        return groups.containsKey(group);
    }

    public boolean hasElement(Integer group, Integer id) {
        Map<Integer, T> grp = groups.get(group);
        return grp != null && grp.containsKey(id);
    }
}
