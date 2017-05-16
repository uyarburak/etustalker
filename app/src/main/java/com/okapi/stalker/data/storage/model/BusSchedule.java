package com.okapi.stalker.data.storage.model;

/**
 * Created by burak on 5/16/2017.
 */

public class BusSchedule {
    private String time, from, to, routeURL;
    public BusSchedule(){
    }
    public BusSchedule(String time, String from, String to){
        this.from = from;
        this.time = time;
        this.to = to;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getRouteURL() {
        return routeURL;
    }

    public void setRouteURL(String routeURL) {
        this.routeURL = routeURL;
    }

    @Override
    public String toString() {
        return "BusSchedule{" +
                "time='" + time + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", routeURL='" + routeURL + '\'' +
                '}';
    }
}
