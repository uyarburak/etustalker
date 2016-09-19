package com.okapi.stalker.data.storage.model;


public class Room{
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Room){
			return this.name.equals(((Room) obj).getName());
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("Room Name : %s", this.name);
	}
	
	
}