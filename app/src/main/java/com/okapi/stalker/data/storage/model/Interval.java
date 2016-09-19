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
		if(obj == null || !(obj instanceof Interval))
			return false;
		Interval other = (Interval) obj;
		return other.day.equals(this.day) && other.hour.equals(this.hour) && other.room.equals(this.room) && other.section.getCourse().getCode().equals(this.getSection().getCourse().getCode());
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