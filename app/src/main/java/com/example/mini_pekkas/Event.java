package com.example.mini_pekkas;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.HashMap;
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
    private Bitmap waitlistQR;
    private Bitmap detailsQR;

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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("banner", this.banner);
        map.put("title", this.title);
        map.put("description", this.description);
        map.put("details", this.details);
        map.put("startDate", this.startDate);
        map.put("endDate", this.endDate);
        map.put("location", this.location);
        map.put("quCode", this.quCode);
        map.put("reqGeo", this.reqGeo);
        // TODO Add other initial event data as needed
        return map;
    }
}
