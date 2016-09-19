package com.okapi.stalker.data.storage.model;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")

public class Instructor implements Person{
	private Integer id;
	private String name;
	private String mail;
	private Department department;
	private Character gender;
	private String office;
	private String website;
	private String image;
	private String lab;

	private Set<Section> sections;

	public String getLab() {
		return lab;
	}
	public void setLab(String lab) {
		this.lab = lab;
	}


	public Instructor() {
		sections = new HashSet<Section>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
		this.department = department;
	}

	public Character getGender() {
		return gender;
	}

	public void setGender(Character gender) {
		this.gender = gender;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	@Override
	public Set<Section> getSections() {
		return sections;
	}

	public void setSections(Set<Section> sections) {
		this.sections = sections;
	}

	public boolean addSection(Section section){
		return sections.add(section);
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Instructor){
			Instructor other = (Instructor) obj;
			String insName = other.getName().replaceAll("\\.", "");
			if(isSubSequence(insName.toLowerCase(), this.name.toLowerCase())){
				this.mail = other.getMail();
				this.department	= other.getDepartment();
				this.office = other.getOffice();
				this.website = other.getWebsite();
				this.image = other.getImage();
				return true;
			}
		}
		return false;
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

	@Override
	public String toString() {
		return String.format("Instructor Name: %s\nMail: %s\nDepartment: %s\nSex: %c\nOffice: %s\nWebsite: %s\nImage: %s",
				this.name, this.mail, this.department, this.gender, this.office, this.website, this.image);
	}





}