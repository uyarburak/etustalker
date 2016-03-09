package com.okapi.stalker.data.storage.type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Section implements Serializable {

	public String course;
	public String number;
	public String instructor;
	public int size;

	private Set<String> studentKeys;
	private Set<String> intervalKeys;

	public Section() {
		studentKeys = new HashSet();
		intervalKeys = new HashSet();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Section))
			return false;
		if (key().equals(((Section)other).key()))
			return true;
		else return false;
	}

	public String key() {
		return course + "\0" + number;
	}

	public void addStudent(Student student) {
		studentKeys.add(student.key());
	}

	public void addInterval(Interval interval) {
		intervalKeys.add(interval.key());
	}

	public Set<String> getStudentKeys() {
		return studentKeys;
	}

	public Set<String> getIntervalKeys() {
		return intervalKeys;
	}


	public String toString() {
		return  "    Course: " + course + "\n" +
				"   Section: " + number + "\n" +  
				"Instructor: " + instructor + "\n" + 
				"      Size: " + size;
	}

	private static final long serialVersionUID = 1L;

}
