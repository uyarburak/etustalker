package com.okapi.stalker.data.storage.type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;
    public String code;
    public String title;
    private Set<String> sectionPointers;

    public Course() {
        sectionPointers = new HashSet<>();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Course))
            return false;
        if (key().equals(((Course) other).key()))
            return true;
        else return false;
    }

    public void addSection(Section section) {
        sectionPointers.add(section.key());
    }

    public String key() {
        return code + " " + title + "\0";
    }

    @Override
    public String toString() {
        return code + ": " + title;
    }

}
