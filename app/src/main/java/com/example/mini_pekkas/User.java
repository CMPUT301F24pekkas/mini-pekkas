package com.example.mini_pekkas;

import java.util.HashMap;
import java.util.Map;
/**
 * Represents a User with personal details such name, lastname, email, phone, and facility.
 */
public class User {
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String facility;
    private String profilePhotoUrl;
    private String userID;
    private String facilityPhotoUrl;
    private String facilityDesc;

    /**
     * Constructs a User object from a map of attributes.
     *
     * @param map A Map containing user details with keys "name", "lastname", "email", "phone", "facility", and "id".
     */
    public User(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.lastname = (String) map.get("lastname");
        this.email = (String) map.get("email");
        this.phone = (String) map.get("phone");
        this.facility = (String) map.get("facility");
        this.profilePhotoUrl = (String) map.get("profilePhoto");
        this.userID = (String) map.get("userID");
        this.facilityPhotoUrl = (String) map.get("facilityPhoto");
        this.facilityDesc = (String) map.get("facilityDesc");
    }

    /**
     * Default constructor for creating an empty User object.
     */
    public User() {
    }

    /**
     * Constructs a User object with specified attributes.
     *
     * @param name      The user's first name.
     * @param lastname  The user's last name.
     * @param email     The user's email address.
     * @param phone     The user's phone number.
     * @param facility  The user's associated facility.
     * @param userID        The user's ID. (Should only be used in firebase)
     */
    public User(String name, String lastname, String email, String phone, String facility, String profilePhotoUrl, String userID, String facilityPhotoUrl, String facilityDesc) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.facility = facility;
        this.profilePhotoUrl = profilePhotoUrl;
        this.userID = userID;
        this.facilityPhotoUrl = facilityPhotoUrl;
        this.facilityDesc = facilityDesc;
    }

    /**
     * Gets the user's first name.
     *
     * @return The first name of the user.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the user's first name.
     *
     * @param name The first name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Gets the user's last name.
     *
     * @return The last name of the user.
     */
    public String getLastname() {
        return lastname;
    }
    /**
     * Sets the user's last name.
     *
     * @param lastname The last name to set.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    /**
     * Gets the user's email address.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets the user's email address.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Gets the user's phone number.
     *
     * @return The phone number of the user.
     */
    public String getPhone() {
        return phone;
    }
    /**
     * Sets the user's phone number.
     *
     * @param phone The phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    /**
     * Gets the user's associated facility.
     *
     * @return The facility associated with the user.
     */
    public String getFacility() {
        return facility;
    }
    /**
     * Sets the user's associated facility.
     *
     * @param facility The facility to set.
     */
    public void setFacility(String facility) {
        this.facility = facility;
    }

    /**
     * Gets the URL of the user's profile photo.
     *
     * @return The profile photo URL of the user.
     */
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    /**
     * Sets the URL of the user's profile photo.
     *
     * @param profilePhotoUrl The profile photo URL to set.
     */
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getId() {
        return userID;
    }

    public void setId(String userID) {
        this.userID = userID;
    }

    public String getFacilityPhotoUrl() {
        return facilityPhotoUrl;
    }

    public void setFacilityPhotoUrl(String facilityPhotoUrl) {
        this.facilityPhotoUrl = facilityPhotoUrl;
    }

    public String getFacilityDesc() {
        return facilityDesc;
    }

    public void setFacilityDesc(String facilityDesc) {
        this.facilityDesc = facilityDesc;
    }

    /**
     * Converts the User object into a Map of attributes.
     *
     * @return A Map containing the user's details, with keys "name", "lastname", "email", "phone", and "facility".
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("lastname", lastname);
        map.put("email", email);
        map.put("phone", phone);
        map.put("facility", facility);
        map.put("profilePhoto", profilePhotoUrl);
        map.put("userID", userID);
        map.put("facilityDesc", facilityDesc);
        return map;
    }
}