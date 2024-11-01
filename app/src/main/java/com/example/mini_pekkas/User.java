package com.example.mini_pekkas;

import java.util.Map;

public class User {
    private String name;
    private String email;
    private String phone;
    private String facility;
    private Boolean isOrganizer;
    // private Boolean isAdmin;
    private String profilePhoto;    // Could be another value

    public User(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.email = (String) map.get("email");
        this.phone = (String) map.get("phone");
        this.facility = (String) map.get("facility");
        this.isOrganizer = (Boolean) map.get("isOrganizer");
        // this.isAdmin = (Boolean) map.get("isAdmin");
        this.profilePhoto = (String) map.get("profilePhoto");
    }
}