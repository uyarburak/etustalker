package com.okapi.stalker.data.storage.type;

import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.search.SearchParam;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class Student implements Comparable<Student>, Serializable {

    public String id;
    public String name;
    public String major;
    public String year;
    public String mail;

    public Set<String> sectionKeys;

    public Student() {
        sectionKeys = new HashSet<>();
    }

    public String key() {
        return id;
    }

    public String attribute(SearchParam param) {
        switch(param) {
            case ID:
                return id;
            case NAME:
                return name;
            case MAJOR:
                return major;
            case COURSE:
                Stash stash = Stash.get();
                StringBuilder builder = new StringBuilder();
                for (String k: sectionKeys) {
                    String course = stash.getSection(k).course;
                    builder.append(course.substring(0, 3))
                            .append(course.substring(4))
                            .append(" ");
                }
                return builder.toString();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int compareTo(Student other) {
        return id.compareTo((other).id);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Student))
            return false;
        if (key().equals(((Student)other).key()))
            return true;
        else return false;
    }

    public void addSection(Section section) {
        sectionKeys.add(section.key());
    }

    @Override
    public String toString() {
        return  " name: " + name + "\n" +
                "   id: " + id + "\n" +
                "major: " + major + "\n" +
                " year: " + year + "\n" +
                " mail: " + mail;
    }

    private static final long serialVersionUID = 1L;

}
