package com.example.mini_pekkas;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event {
    // Attributes
    private String id;
    private String title;
    private String name;
    private User eventHost;
    private String description;
    private String startDate;
    private String endDate;
    private int price;
    private String facility;
    private double facilityGeoLat;
    private double facilityGeoLong;
    private int maxAttendees;
    private ArrayList<User> attendees;
    private ArrayList<User> waitlist;
    private boolean isUserInWaitlist;
    private String checkinID;
    private String checkinRq;
    private boolean geo;
    private Bitmap waitlistQR;
    private Bitmap detailsQR;

    // Constructors
    public Event(String id, String name, User eventHost, String description, String startDate, String endDate, int price,
                 String facility, double facilityGeoLat, double facilityGeoLong, int maxAttendees,
                 ArrayList<User> waitlist, String checkinID, String checkinRq, boolean geo) {
        this.id = id;
        this.name = name;
        this.eventHost = eventHost;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.facility = facility;
        this.facilityGeoLat = facilityGeoLat;
        this.facilityGeoLong = facilityGeoLong;
        this.maxAttendees = maxAttendees;
        this.waitlist = waitlist;
        this.checkinID = checkinID;
        this.checkinRq = checkinRq;
        this.geo = geo;
    }



    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public User getEventHost() { return eventHost; }
    public void setEventHost(User eventHost) { this.eventHost = eventHost; }


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getFacility() { return facility; }
    public void setFacility(String facility) { this.facility = facility; }

    public double getFacilityGeoLat() { return facilityGeoLat; }
    public void setFacilityGeoLat(double facilityGeoLat) { this.facilityGeoLat = facilityGeoLat; }

    public double getFacilityGeoLong() { return facilityGeoLong; }
    public void setFacilityGeoLong(double facilityGeoLong) { this.facilityGeoLong = facilityGeoLong; }

    public int getMaxAttendees() { return maxAttendees; }
    public void setMaxAttendees(int maxAttendees) { this.maxAttendees = maxAttendees; }

    public ArrayList<User> getAttendees() { return attendees; }
    public void setAttendees(ArrayList<User> attendees) { this.attendees = attendees; }

    public ArrayList<User> getWaitlist() { return waitlist; }
    public void setWaitlist(ArrayList<User> waitlist) { this.waitlist = waitlist; }

    public String getCheckinID() { return checkinID; }
    public void setCheckinID(String checkinID) { this.checkinID = checkinID; }

    public String getCheckinRq() { return checkinRq; }
    public void setCheckinRq(String checkinRq) { this.checkinRq = checkinRq; }

    public boolean isGeo() { return geo; }
    public void setGeo(boolean geo) { this.geo = geo; }

    public Bitmap getDetailsQR() {
        return detailsQR;
    }
    public void setDetailsQR (Bitmap detailsQR) {
        this.detailsQR = detailsQR;
    }

    public Bitmap getWaitlistQR() {
        return waitlistQR;
    }

    public void setWaitlistQR(Bitmap waitlistQR) {
        this.waitlistQR = waitlistQR;
    }




    public Bitmap getQRCodeFromID(int width, int height) {
        return null;
    }

    public Event(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.title = (String) map.get("title");
        this.name = (String) map.get("name");
        this.eventHost = (User) map.get("eventHost");
        this.description = (String) map.get("description");
        this.startDate = (String) map.get("startDate");
        this.endDate = (String) map.get("endDate");
        this.price = map.get("price") != null ? (int) map.get("price") : 0;
        this.facility = (String) map.get("facility");
        this.facilityGeoLat = map.get("facilityGeoLat") != null ? (double) map.get("facilityGeoLat") : 0.0;
        this.facilityGeoLong = map.get("facilityGeoLong") != null ? (double) map.get("facilityGeoLong") : 0.0;
        this.maxAttendees = map.get("maxAttendees") != null ? (int) map.get("maxAttendees") : 0;
        this.attendees = (ArrayList<User>) map.get("attendees");
        this.waitlist = (ArrayList<User>) map.get("waitlist");
        this.checkinID = (String) map.get("checkinID");
        this.checkinRq = (String) map.get("checkinRq");
        this.geo = map.get("geo") != null ? (boolean) map.get("geo") : false;
        this.waitlistQR = (Bitmap) map.get("waitlistQR");
        this.detailsQR = (Bitmap) map.get("detailsQR");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("name", name);
        map.put("eventHost", eventHost);
        map.put("description", description);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("price", price);
        map.put("facility", facility);
        map.put("facilityGeoLat", facilityGeoLat);
        map.put("facilityGeoLong", facilityGeoLong);
        map.put("maxAttendees", maxAttendees);
        map.put("attendees", attendees);
        map.put("waitlist", waitlist);
        map.put("checkinID", checkinID);
        map.put("checkinRq", checkinRq);
        map.put("geo", geo);
        map.put("waitlistQR", waitlistQR);
        map.put("detailsQR", detailsQR);
        return map;
    }
}