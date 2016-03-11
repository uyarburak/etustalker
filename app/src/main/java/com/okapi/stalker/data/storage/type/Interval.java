package com.okapi.stalker.data.storage.type;

import java.io.Serializable;

public class Interval implements Serializable {

    private static final long serialVersionUID = 1L;

    ;
    public Time time;

    ;
    public Day day;
    public ClassRoom classRoom;
    public Interval() {
    }

    public String key() {
        return day + " " + time + " " + classRoom.name + "\0";
    }

    @Override
    public String toString() {
        return "day: " + day + "\n"
                + "time: " + time + "\n"
                + "classroom: " + classRoom.name;
    }

    public enum Time {ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE, THIRTEEN}

    public enum Day {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY}

}
