package com.okapi.stalker.data.storage.type;

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
	
	public String key() {
		return name + "\0" + id;
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
