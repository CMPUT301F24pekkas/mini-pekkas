package com.example.mini_pekkas;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notifications {
    private String description;
    private Date date;
    private long priority;
    private String title;

    /**
     * Constructor for notifications with Date class
     */
    public Notifications(String title, String description, Date date, long priority) {
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.title = title;
    }

    /**
     * Constructor for notifications with Firebase Timestamp class
     */
    public Notifications(String title, String description, Timestamp date, long priority) {
        this.description = description;
        this.date = date.toDate();
        this.priority = priority;
        this.title = title;
    }

    public Notifications(Map<String, Object> map) {
        this.description = (String) map.get("description");

        // Check if the date field is of type Date or Timestamp, Convert to Date
        if (map.get("date") instanceof Date) {
            this.date = (Date) map.get("date");
        } else if (map.get("date") instanceof Timestamp) {
            this.date = ((Timestamp) map.get("date")).toDate();
        }

        this.priority = (long) map.get("priority");
        this.title = (String) map.get("title");
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("description", description);
        map.put("date", date);
        map.put("priority", priority);
        map.put("title", title);
        return map;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public Timestamp getTimestamp() {
        return new Timestamp(date);
    }

    public long getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    // Setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}


