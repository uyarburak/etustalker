package com.okapi.stalker.data.storage.type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Instructor implements Comparable<Instructor>, Serializable {

    private static final long serialVersionUID = 1L;
    public String name;
    public String department;
    public String mail;
    private Set<String> sectionKeys;

    public Instructor() {
        sectionKeys = new HashSet<>();
    }

    @Override
    public int compareTo(Instructor other) {
        return name.compareTo((other).name);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Instructor))
            return false;
        if (key().equals(((Instructor) other).key()))
            return true;
        else return false;
    }

    public String key() {
        return name;
    }

    public void addSection(Section section) {
        sectionKeys.add(section.key());
    }

    @Override
    public String toString() {
        return " name: " + name + "\n" +
                "department: " + department + "\n" +
                " mail: " + mail;
    }
}
