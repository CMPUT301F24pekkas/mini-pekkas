package com.example.mini_pekkas;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String facility;
   // private String profilePhoto;    // Could be another value

    public User(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.lastname = (String) map.get("lastname");
        this.email = (String) map.get("email");
        this.phone = (String) map.get("phone");
        this.facility = (String) map.get("facility");
        //this.profilePhoto = (String) map.get("profilePhoto");
    }

    public User(String name, String lastname, String email, String phone, String facility) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.facility = facility;
        //this.profilePhoto = profilePhoto;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

//    public String getProfilePhoto() {
//        return profilePhoto;
//    }
//
//    public void setProfilePhoto(String profilePhoto) {
//        this.profilePhoto = profilePhoto;
//    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("lastname", lastname);
        map.put("email", email);
        map.put("phone", phone);
        map.put("facility", facility);
        //map.put("isOrganizer", isOrganizer);
        // map.put("isAdmin", isAdmin);
        //map.put("profilePhoto", profilePhoto);
        return map;
    }
}