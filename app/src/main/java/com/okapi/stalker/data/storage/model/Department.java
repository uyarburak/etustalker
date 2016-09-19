package com.okapi.stalker.data.storage.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class Department implements Serializable{
	private String name;
	private String mainURL;
	private String faculty;

	private Set<Student> students;
	private Set<Instructor> instructors;
	
	public Department() {
		students = new HashSet<Student>();
		instructors = new HashSet<Instructor>();
	}
	
	public boolean addStudent(Student student){
		return students.add(student);
	}
	public boolean addInstructor(Instructor instructor){
		instructor.setDepartment(this);
		return instructors.add(instructor);
	}
	
	public String getMainURL() {
		return mainURL;
	}
	public void setMainURL(String mainURL) {
		this.mainURL = mainURL;
	}
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Department){
			return this.name.equals(((Department) obj).getName());
		}
		return false;
	}
	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	public Set<Instructor> getInstructors() {
		return instructors;
	}

	public void setInstructors(Set<Instructor> instructors) {
		this.instructors = instructors;
	}

	@Override
	public String toString() {
		return String.format("Department Name: %s\nMain Page: %s\nFaculty Name: %s"
				, this.name, this.mainURL, this.faculty);
	}
	
	private boolean isSubSequence(String s1, String s2){
		
	    // Base Cases
	    if (s1.isEmpty()) return true;
	    if (s2.isEmpty()) return false;
	 
	    // If last characters of two strings are matching
	    if(s1.charAt(s1.length()-1) == s2.charAt(s2.length()-1))
	    	return isSubSequence(s1.substring(0,  s1.length()-1), s2.substring(0,  s2.length()-1)); 
	 
	    // If last characters are not matching
	    return isSubSequence(s1, s2.substring(0,  s2.length()-1)); 
	}
	
	
}