package com.example.mini_pekkas;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * This class creates an event object with getters and setters to its attributes
 * @version 1.11 11/04/20224 added getUsers, fixed event add bug
 */
public class Event {
    // Attributes
    private String id;
    private String name;
    private User eventHost;
    private String description;
    private String details;
    private Date startDate;
    private Date endDate;
    private double price;
    private String facility;
    private double facilityGeoLat;
    private double facilityGeoLong;
    private long maxAttendees;
    private ArrayList<User> attendees;
    private ArrayList<User> waitlist;
    private boolean isUserInWaitlist;
    private String QrCode;
    private boolean geo;
    private String posterPhotoUrl;

    // Constructors
    /**
     * Constructs an Event object with the specified attributes.
     *
     * @param id               The unique identifier for the event.
     * @param name             The name of the event.
     * @param eventHost        The host of the event.
     * @param description      The description of the event.
     * @param startDate        The start date of the event.
     * @param endDate          The end date of the event.
     * @param price            The price of the event.
     * @param facility         The facility where the event is held.
     * @param facilityGeoLat   The latitude of the facility location.
     * @param facilityGeoLong  The longitude of the facility location.
     * @param maxAttendees     The maximum number of attendees.
     * @param waitlist         The list of users on the event's waitlist.
     * @param QrCode           The QR code associated with the event.
     * @param geo              Indicates whether geographic location is enabled for the event.
     * @param posterPhotoUrl   The URL of the event poster.
     */
    public Event(String id, String name, User eventHost, String description, Date startDate, Date endDate, float price,
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
    /**
     * Default constructor for creating an empty Event object.
     */
    public Event() {
    }


    // Getters and Setters
    /**
     * Gets the unique identifier of the event.
     * @return the event ID
     */
    public String getId() { return id; }
    /**
     * Sets the unique identifier of the event.
     * @param id the event ID to set
     */
    public void setId(String id) { this.id = id; }
    /**
     * Gets the name of the event.
     * @return the event name
     */
    public String getName() { return name; }
    /**
     * Sets the name of the event.
     * @param name the event name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the host of the event.
     * @return the event host as a User object
     */
    public User getEventHost() { return eventHost; }
    /**
     * Sets the host of the event.
     * @param eventHost the User object representing the event host
     */
    public void setEventHost(User eventHost) { this.eventHost = eventHost; }

    /**
     * Gets the description of the event.
     * @return the event description
     */
    public String getDescription() { return description; }
    /**
     * Sets the description of the event.
     * @param description the event description to set
     */
    public void setDescription(String description) { this.description = description; }

    public String getDetails() { return details; }

    public void setDetails(String details) { this.details = details; }

    /**
     * Gets the start date of the event.
     * @return the start date of the event
     */
    public Date getStartDate() { return startDate; }
    /**
     * Sets the start date of the event.
     * @param startDate the start date of the event to set
     */
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    /**
     * Gets the end date of the event.
     * @return the end date of the event
     */
    public Date getEndDate() { return endDate; }
    /**
     * Sets the end date of the event.
     * @param endDate the end date of the event to set
     */
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    /**
     * Gets the price of the event.
     * @return the price of the event
     */
    public double getPrice() { return price; }
    /**
     * Sets the price of the event.
     * @param price the price of the event to set
     */
    public void setPrice(int price) { this.price = price; }
    /**
     * Gets the facility name where the event is held.
     * @return the facility name
     */
    public String getFacility() { return facility; }
    /**
     * Sets the facility name where the event is held.
     * @param facility the facility name to set
     */
    public void setFacility(String facility) { this.facility = facility; }
    /**
     * Gets the latitude of the facility location.
     * @return the latitude of the facility
     */
    public double getFacilityGeoLat() { return facilityGeoLat; }
    /**
     * Sets the latitude of the facility location.
     * @param facilityGeoLat the latitude of the facility to set
     */
    public void setFacilityGeoLat(double facilityGeoLat) { this.facilityGeoLat = facilityGeoLat; }
    /**
     * Gets the longitude of the facility location.
     * @return the longitude of the facility
     */
    public double getFacilityGeoLong() { return facilityGeoLong; }
    /**
     * Sets the longitude of the facility location.
     * @param facilityGeoLong the longitude of the facility to set
     */
    public void setFacilityGeoLong(double facilityGeoLong) { this.facilityGeoLong = facilityGeoLong; }
    /**
     * Gets the maximum number of attendees for the event.
     * @return the maximum number of attendees
     */
    public long getMaxAttendees() { return maxAttendees; }
    /**
     * Sets the maximum number of attendees for the event.
     * @param maxAttendees the maximum number of attendees to set
     */
    public void setMaxAttendees(long maxAttendees) { this.maxAttendees = maxAttendees; }
    /**
     * Gets the list of users attending the event.
     * @return an ArrayList of User objects representing the attendees
     */

    public ArrayList<User> getAttendees() { return attendees; }
    /**
     * Sets the list of users attending the event.
     * @param attendees an ArrayList of User objects representing the attendees
     */
    public void setAttendees(ArrayList<User> attendees) { this.attendees = attendees; }
    /**
     * Gets the waitlist of users for the event.
     * @return an ArrayList of User objects representing the waitlist
     */
    public ArrayList<User> getWaitlist() { return waitlist; }
    /**
     * Sets the waitlist of users for the event.
     * @param waitlist an ArrayList of User objects representing the waitlist
     */
    public void setWaitlist(ArrayList<User> waitlist) { this.waitlist = waitlist; }
    /**
     * Gets the QR code associated with the event.
     * @return the event's QR code as a String
     */
    public String getQrCode() { return QrCode; }

    /**
     * Sets the QR code for the event.
     * @param QrCode the QR code to set
     */
    public void setQrCode(String QrCode) { this.QrCode = QrCode; }
    /**
     * Checks if geo location is allowed for the event.
     * @return true if geographic location is enabled, false otherwise
     */
    public boolean isGeo() { return geo; }
    /**
     * Sets whether geo location is allowed for event.
     * @param geo true to enable geographic location, false to disable
     */
    public void setGeo(boolean geo) { this.geo = geo; }

    /**
     * Gets the URL of the event poster.
     *
     * @return the URL of the event's poster
     */
    public String getPosterPhotoUrl() {
        return posterPhotoUrl;
    }

    /**
     * Sets the URL of the event poster.
     *
     * @param posterPhotoUrl the URL to set for the poster
     */
    public void setPosterPhotoUrl(String posterPhotoUrl) {
        this.posterPhotoUrl = posterPhotoUrl;
    }

    /**
     * Generates a QR code for the event using its unique ID.
     *
     * @param width  the width of the QR code
     * @param height the height of the QR code
     * @return a Bitmap representation of the generated QR code
     */
    public Bitmap getQRCodeFromID(int width, int height) {
        return null;
    }

    /**
     * Constructs an Event object by initializing it with data from a Map.
     *
     * @param map A Map containing key-value pairs representing the event's data.
     */
    public Event(Map<String, Object> map) {
        for (String key : map.keySet()) {
            Log.d("Event Debug", "Key: " + key);
        }
        if (map == null) {
            Log.e("Error", "Document data is null");
            return;
        }

        this.id = (String) map.get("id");
        this.name = (String) map.get("name");
        if (map.get("eventHost") instanceof User) {
            this.eventHost = (User) map.get("eventHost");
        } else {
            this.eventHost = new User((Map<String, Object>) map.get("eventHost"));
        }
        this.description = (String) map.get("description");
        Object startDateObject = map.get("startDate");
        this.startDate = (startDateObject instanceof com.google.firebase.Timestamp)
                ? ((com.google.firebase.Timestamp) startDateObject).toDate()
                : (startDateObject instanceof java.util.Date)
                ? (java.util.Date) startDateObject
                : null;

        // Parse endDate
        Object endDateObject = map.get("endDate");
        this.endDate = (endDateObject instanceof com.google.firebase.Timestamp)
                ? ((com.google.firebase.Timestamp) endDateObject).toDate()
                : (endDateObject instanceof java.util.Date)
                ? (java.util.Date) endDateObject
                : null;
        this.price = map.get("price") != null ? (double) map.get("price") : 0;
        this.facility = (String) map.get("facility");
        this.facilityGeoLat = map.get("facilityGeoLat") != null ? (double) map.get("facilityGeoLat") : 0.0;
        this.facilityGeoLong = map.get("facilityGeoLong") != null ? (double) map.get("facilityGeoLong") : 0.0;
        this.maxAttendees = map.get("maxAttendees") != null ? (long) map.get("maxAttendees") : 0;
        this.attendees = (ArrayList<User>) map.get("attendees");
        this.waitlist = (ArrayList<User>) map.get("waitlist");
        this.QrCode = (String) map.get("QrCode");
        this.geo = map.get("geo") != null ? (boolean) map.get("geo") : false;
        this.posterPhotoUrl = (String) map.get("posterUrl");

    }

    /**
     * Converts this Event object to a Map representation for storing in Firebase.
     *
     * @return a Map representing the Event object with all its attributes
     */
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
        map.put("posterUrl", posterPhotoUrl);

        return map;
    }
}