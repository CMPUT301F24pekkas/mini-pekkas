package com.example.mini_pekkas;

import java.util.Date;
import java.util.Map;

public class Event {
    private String banner;
    private String title;
    private String description;
    private String details;
    private Date startDate;
    private Date endDate;
    private String location;
    private String quCode;
    private String reqGeo;

    public Event(Map<String, Object> map){
        this.banner = (String) map.get("banner");
        this.title = (String) map.get("title");
        this.description = (String) map.get("description");
        this.details = (String) map.get("details");
        this.startDate = (Date) map.get("startDate");
        this.endDate = (Date) map.get("endDate");
        this.location = (String) map.get("location");
        this.quCode = (String) map.get("quCode");
        this.reqGeo = (String) map.get("reqGeo");
        // TODO Add other initial event data as needed
    }
}
