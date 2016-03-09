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

	private boolean writeBack;
	private InputStream is;

	private Map<String, Course> courseMap;
	private Map<String, Section> sectionMap;
	private Map<String, Student> studentMap;
	private Map<String, Instructor> instructortMap;
	private Map<String, ClassRoom> classRoomMap;
	private Map<String, Interval> intervalMap;
	private Set<String> departmentSet;

	public static void set(InputStream is, String source, int size, int size2) {
		if (stash != null)
			stash.writeBack();
		stash = new Stash(is, source, size, size2);
	}

	public static Stash get() {
		if (stash == null) 
			throw new IllegalStateException("Stash is not set!");
		return stash;
	}

	private Stash(InputStream is, String source, int size, int size2) {
		this.is = is;
		try {
			load(is);
		} catch (FileNotFoundException e) {
			read(source, size, size2);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
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

	private void read(String source, int size, int size2) {
		Stacker stacker = Stacker.call();
		stacker.readFrom(source, size, size2);
		courseMap = stacker.courseMap;
		sectionMap = stacker.sectionMap;
		studentMap = stacker.studentMap;
		instructortMap = stacker.instructorMap;
		classRoomMap = stacker.classRoomMap;
		intervalMap = stacker.intervalMap;
		departmentSet = stacker.departmentSet;

		writeBack = true;
	}

	public void writeBack() {
		//if (writeBack)
			//Stacker.call().writeTo(destionation);
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
	// test
	public static void main(String[] args) throws FileNotFoundException {
		long s = System.nanoTime();
		Stash.set(new FileInputStream("res/stash.bin"), "res/htmls/spring2016", 595, 603);
		Stash stash = Stash.get();

		System.out.println(stash.classRoomMap.size());
		System.out.println((System.nanoTime() - s) / 1E9 );
		for (Map.Entry<String, ClassRoom> entry: stash.classRoomMap.entrySet()) {
			for (String string : entry.getValue().getIntervalKeys()) {
				System.out.println(stash.intervalMap.get(string));
			}
		}
//		for (Map.Entry<String, Section> entry : stash.sectionMap.entrySet()) {
//			System.out.println(entry.getValue());
//			for (String string : entry.getValue().getIntervalKeys()) {
//				System.out.println(stash.intervalMap.get(string));
//			}
//		}
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			if (scanner.nextLine().equals("q")) {
				stash.writeBack();
				break;
			}
		}
		scanner.close();
		System.out.println("Done");

	}
}
