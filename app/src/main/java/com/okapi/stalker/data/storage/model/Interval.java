package com.okapi.stalker.data.storage.model;

import java.io.Serializable;

@SuppressWarnings("serial")

public class Interval implements Serializable{
	private Integer id;
	private Integer day;
	private Integer hour;
	private String room;

	private Section section;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getHour() {
		return hour;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Interval){
			Interval other = (Interval) obj;
			return other.day == day && other.hour == hour;
		}
		return false;
	}

	@Override
	public int hashCode() {
		//1861
		return day*6 + hour;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.day);
		builder.append(this.hour);
		builder.append(this.room);
		builder.append(this.section.getCourse().getCode());
		builder.append(this.section.getSectionNo());
		return builder.toString();
	}
	
	
	
}