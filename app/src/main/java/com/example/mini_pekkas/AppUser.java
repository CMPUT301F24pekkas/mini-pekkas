package com.example.mini_pekkas;

import java.util.HashMap;
import java.util.Map;

public class AppUser {
    // Attributes
    private String deviceID;
    private String name;
    private String email;
    private String phone;
    private String homepage;
    private boolean geo;
    private boolean admin;
    private boolean organizer;
    private String profilePhoto;

    // Constructors
    public AppUser(String deviceID, String name, String email, String phone, String homepage, boolean geo, boolean admin, boolean organizer) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.homepage = homepage;
        this.geo = geo;
        this.admin = admin;
        this.organizer = organizer;
    }

    public AppUser(String deviceID, String name, String email, String phone, String homepage, boolean geo, boolean admin, boolean organizer, String profilePhoto) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.homepage = homepage;
        this.geo = geo;
        this.admin = admin;
        this.organizer = organizer;
        this.profilePhoto = profilePhoto;
    }

    // Operations / Getters and Setters
    public String getDataString() {
        return "Device ID: " + deviceID + ", Name: " + name + ", Email: " + email;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public boolean isGeo() {
        return geo;
    }

    public void setGeo(boolean geo) {
        this.geo = geo;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isOrganizer() {
        return organizer;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("deviceID", deviceID);
        map.put("phone", phone);
        map.put("homepage", homepage);
        map.put("geo", geo);
        map.put("admin", admin);
        map.put("profilePhoto", profilePhoto);
        map.put("organizer", organizer);
        return map;
    }
}

