package com.example.mini_pekkas;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event {
    // Attributes
    private String id;
    private String name;
    private User eventHost;
    private String description;
    private String startDate;
    private String endDate;
    private float price;
    private String facility;
    private double facilityGeoLat;
    private double facilityGeoLong;
    private int maxAttendees;
    private ArrayList<User> attendees;
    private ArrayList<User> waitlist;
    private boolean isUserInWaitlist;
    private String QrCode;
    private boolean geo;
    private String posterPhotoUrl;

    // Constructors
    public Event(String id, String name, User eventHost, String description, String startDate, String endDate, float price,
                 String facility, double facilityGeoLat, double facilityGeoLong, int maxAttendees,
                 ArrayList<User> waitlist, String QrCode, boolean geo, String posterPhotoUrl) {
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
        this.QrCode = QrCode;
        this.geo = geo;
        this.posterPhotoUrl = posterPhotoUrl;
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

    public float getPrice() { return price; }
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



    public String getQrCode() { return QrCode; }
    public void setQrCode(String Qrcode) { this.QrCode = Qrcode; }

    public boolean isGeo() { return geo; }
    public void setGeo(boolean geo) { this.geo = geo; }

    public String getPosterPhotoUrl() {
        return posterPhotoUrl;
    }

    public void setPosterPhotoUrl(String posterPhotoUrl) {
        this.posterPhotoUrl = posterPhotoUrl;
    }

    public Bitmap getQRCodeFromID(int width, int height) {
        return null;
    }

    public Event(Map<String, Object> map) {
        this.id = (String) map.get("id");
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
        this.QrCode = (String) map.get("QrCode");
        this.geo = map.get("geo") != null ? (boolean) map.get("geo") : false;

    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
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
        map.put("geo", geo);
        map.put("QrCode", QrCode);

        return map;
    }
}