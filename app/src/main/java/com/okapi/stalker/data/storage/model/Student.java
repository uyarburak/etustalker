package com.okapi.stalker.data.storage.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class Student implements Person{
	private String id;
	private String name;
	private String mail;
	private Department department;
	private Department department2;
	private Integer year;
	private Character gender;
	private String image;
	private Boolean active;

	private List<Tag> tags;
//	private Set<Friend> friends;

	private Set<Section> sections;

	public Student() {
//		friends = new HashSet<Friend>();
		sections = new HashSet<Section>();
		tags = new ArrayList<Tag>();
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		if(this.department == null)
			this.department = department;
//		else if(!this.department.equals(department))
//			this.department2 = department;
		else
			return;
		department.addStudent(this);
	}

	public Department getDepartment2() {
		return department2;
	}

	public void setDepartment2(Department department2) {
		this.department2 = department2;
		department.addStudent(this);
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Character getGender() {
		return gender;
	}

	public void setGender(Character gender) {
		this.gender = gender;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

//	public boolean addFriend(Student friend){
//		return friends.add(new Friend(this, friend));
//	}
//	public Set<Friend> getFriends() {
//		return friends;
//	}
	public boolean addSection(Section section){
		if(sections.add(section)){
			section.addStudent(this);
			return true;
		}
		return false;
	}
	@Override
	public Set<Section> getSections() {
		return sections;
	}

	public void setSections(Set<Section> sections) {
		this.sections = sections;
	}

//	public void setFriends(Set<Friend> friends) {
//		this.friends = friends;
//	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof Student){
			Student other = (Student)obj;
			return other.id.equals(id);
		}
		return false;
	}
	@Override
	public String toString() {
		return String.format("Student Name: %s\nMail: %s\nDepartment: %s\nYear: %s\nSex: %c\nImage: %s\nActive: %s",
				this.name, this.mail, this.department, this.year, this.gender, this.image, this.active);
	}


	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public boolean addTag(Tag tag) {
		return tags.add(tag);
	}

	public boolean hasTag(String string) {
		for (Tag tag : tags) {
			if (tag.getText().equals(string)) {
				return true;
			}
		}

		return false;
	}
	
}