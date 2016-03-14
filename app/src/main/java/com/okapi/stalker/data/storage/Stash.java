package com.okapi.stalker.data.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.KeyStore.Entry;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.okapi.stalker.data.storage.type.ClassRoom;
import com.okapi.stalker.data.storage.type.Course;
import com.okapi.stalker.data.storage.type.Instructor;
import com.okapi.stalker.data.storage.type.Interval;
import com.okapi.stalker.data.storage.type.Section;
import com.okapi.stalker.data.storage.type.Student;

public class Stash {

    private static Stash stash;

    private Map<String, Course> courseMap;
    private Map<String, Section> sectionMap;
    private Map<String, Student> studentMap;
    private Map<String, Instructor> instructortMap;
    private Map<String, ClassRoom> classRoomMap;
    private Map<String, Interval> intervalMap;
    private Set<String> departmentSet;

    private Stash(InputStream stream) {
        try {
            load(stream);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void set(InputStream stream) {
        stash = new Stash(stream);
    }

    public static Stash get() {
        if (stash == null)
            throw new IllegalStateException("Stash is not set!");
        return stash;
    }

    private void load(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream stash;
        stash = new ObjectInputStream(is);
        courseMap = (Map<String, Course>) stash.readObject();
        sectionMap = (Map<String, Section>) stash.readObject();
        studentMap = (Map<String, Student>) stash.readObject();
        instructortMap = (Map<String, Instructor>) stash.readObject();
        classRoomMap = (Map<String, ClassRoom>) stash.readObject();
        intervalMap = (Map<String, Interval>) stash.readObject();
        departmentSet = (Set<String>) stash.readObject();
        stash.close();
    }

    public Set<String> getCourseKeys() {
        return courseMap.keySet();
    }

    public Course getCourse(String key) {
        return courseMap.get(key);
    }

    public Set<String> getStudentKeys() {
        return studentMap.keySet();
    }

    public Student getStudent(String key) {
        return studentMap.get(key);
    }

    public Set<String> getSectionKeys() {
        return sectionMap.keySet();
    }

    public Section getSection(String key) {
        return sectionMap.get(key);
    }

    public Set<String> getInstructorKeys() {
        return instructortMap.keySet();
    }

    public Instructor getInstructor(String key) {
        return instructortMap.get(key);
    }

    public Set<String> getClassRoomKeys() {
        return classRoomMap.keySet();
    }

    public ClassRoom getClassRoom(String key) {
        return classRoomMap.get(key);
    }

    public Set<String> getIntervalKeys() {
        return intervalMap.keySet();
    }

    public Interval getInterval(String key) {
        return intervalMap.get(key);
    }

    public Set<String> getDepartments() {
        return departmentSet;
    }

}
