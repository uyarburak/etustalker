package com.okapi.stalker.data.storage.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class Course implements Serializable{
	private String code;
	private String title;
	private boolean active;

	private Set<Section> sections;
	
	public Course() {
		sections = new HashSet<Section>();
	}
	
	public boolean addSection(Section section){
		if(sections.add(section)){
			section.setCourse(this);
			return true;
		}
		return false;
	}
	
	public Set<Section> getSections() {
		return sections;
	}
	public void setSections(Set<Section> sections) {
		this.sections = sections;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}


	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Course)
			return this.code.equals(((Course) obj).getCode());
		return false;
	}

	@Override
	public String toString() {
		return String.format("Course Title: %s, Code: %s, Status: %s"
				, this.title, this.code, active ? "ACTIVE" : "DEACTIVE");
	}


}