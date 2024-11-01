package com.example.mini_pekkas;

import java.util.ArrayList;
import java.util.Map;

import android.graphics.Bitmap;

public class Event {
    // Attributes
    private String id;
    private String title;
    private String name;
    private AppUser eventHost;
    private String description;
    private String startDate;
    private String endDate;
    private int price;
    private String facility;
    private double facilityGeoLat;
    private double facilityGeoLong;
    private int maxAttendees;
    private ArrayList<AppUser> attendees;
    private ArrayList<AppUser> waitlist;
    private String checkinID;
    private String checkinRq;
    private boolean geo;
    private Bitmap waitlistQR;
    private Bitmap detailsQR;

    // Constructors
    public Event(String id, String name, AppUser eventHost, String description, String startDate, String endDate, int price,
                 String facility, double facilityGeoLat, double facilityGeoLong, int maxAttendees, ArrayList<AppUser> attendees,
                 ArrayList<AppUser> waitlist, String checkinID, String checkinRq, boolean geo) {
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
        this.attendees = attendees;
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


    public AppUser getEventHost() { return eventHost; }
    public void setEventHost(AppUser eventHost) { this.eventHost = eventHost; }


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

    public ArrayList<AppUser> getAttendees() { return attendees; }
    public void setAttendees(ArrayList<AppUser> attendees) { this.attendees = attendees; }

    public ArrayList<AppUser> getWaitlist() { return waitlist; }
    public void setWaitlist(ArrayList<AppUser> waitlist) { this.waitlist = waitlist; }

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
}