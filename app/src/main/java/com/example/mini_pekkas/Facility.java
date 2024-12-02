package com.example.mini_pekkas;

/**
 * This class creates a facility object with getters and setters to its attributes
 */
public class Facility {
    private String name;
    private String facilityPhotoUrl;
    private String description;

    /**
     * Constructs a Facility object with the specified attributes.
     * @param name The name of the facility.
     * @param description The description of the facility.
     * @param facilityPhotoUrl the URL of the facility's photo.
     */
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

    /**
     * Gets the name of the facility.
     * @return the facility name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the URL of the facility's photo.
     * @return the facility photo URL
     */
    public String getFacilityPhotoUrl() {
        return facilityPhotoUrl;
    }

    /**
     * Gets the description of the facility.
     * @return the facility description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the name of the facility.
     * @param name the facility name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the URL of the facility's photo.
     * @param facilityPhotoUrl the facility photo URL to set
     */
    public void setFacilityPhotoUrl(String facilityPhotoUrl) {
        this.facilityPhotoUrl = facilityPhotoUrl;
    }

    /**
     * Sets the description of the facility.
     * @param description the facility description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}


