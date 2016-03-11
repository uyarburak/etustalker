package com.okapi.stalker.data.storage.type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ClassRoom implements Serializable, Comparable<ClassRoom> {

    private static final long serialVersionUID = 1L;
    public String name;
    private Set<String> intervalKeys;

    public ClassRoom(String name) {
        this.name = name;
        intervalKeys = new HashSet<>();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ClassRoom))
            return false;
        if (key().equals(((ClassRoom) other).key()))
            return true;
        else return false;
    }

    @Override
    public int compareTo(ClassRoom o) {
        return name.compareTo(o.name);
    }

    public void addInterval(Interval interval) {
        intervalKeys.add(interval.key());
    }

    public Set<String> getIntervalKeys() {
        return intervalKeys;
    }

    public String key() {
        return name + "\0";
    }

    @Override
    public String toString() {
        return "name: " + name;
    }

}
