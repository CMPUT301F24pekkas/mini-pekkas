package com.example.mini_pekkas;

public class Facility {
    private String name;
    private String facilityPhotoUrl;
    private String description;

    public Facility(String name, String description, String facilityPhotoUrl) {
        this.name = name;
        this.description = description;
        this.facilityPhotoUrl = facilityPhotoUrl;
    }

    /**
     * Default constructor for creating an empty Facility object.
     */
    public Facility() {

    }

    public String getName() {
        return name;
    }

    public String getFacilityPhotoUrl() {
        return facilityPhotoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFacilityPhotoUrl(String facilityPhotoUrl) {
        this.facilityPhotoUrl = facilityPhotoUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


