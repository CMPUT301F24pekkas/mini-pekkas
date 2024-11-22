package com.example.mini_pekkas;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The Notifications class represents a notification that includes details such as
 * title, description, date, and priority.
 */
public class Notifications {
    private String description;
    private Date date;
    private long priority;
    private String title;

    /**
     * Constructor for creating a Notification object with a Date.
     *
     * @param title       The title of the notification.
     * @param description A brief description of the notification.
     * @param date        The date when the notification was created.
     * @param priority    The priority level of the notification (higher value means higher priority).
     */
    public Notifications(String title, String description, Date date, long priority) {
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.title = title;
    }

    /**
     * Constructor for creating a Notification object with a Firebase Timestamp.
     *
     * @param title       The title of the notification.
     * @param description A brief description of the notification.
     * @param date        The Firebase Timestamp when the notification was created.
     * @param priority    The priority level of the notification (higher value means higher priority).
     */
    public Notifications(String title, String description, Timestamp date, long priority) {
        this.description = description;
        this.date = date.toDate();
        this.priority = priority;
        this.title = title;
    }

    /**
     * Constructor that creates a Notification object from a Map.
     * The Map is expected to contain keys for "description", "date", "priority", and "title".
     *
     * @param map A Map containing key-value pairs representing notification attributes.
     */
    public Notifications(Map<String, Object> map) {
        this.description = (String) map.get("description");

        // Check if the date field is of type Date or Timestamp, Convert to Date
        if (map.get("date") instanceof Date) {
            this.date = (Date) map.get("date");
        } else if (map.get("date") instanceof Timestamp) {
            this.date = ((Timestamp) map.get("date")).toDate();
        }

        this.priority = (long) map.get("priority");
        this.title = (String) map.get("title");
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("description", description);
        map.put("date", date);
        map.put("priority", priority);
        map.put("title", title);
        return map;
    }

    /**
     * Gets the description of the notification.
     *
     * @return The description of the notification.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the date when the notification was created.
     *
     * @return The Date of the notification.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the Firebase Timestamp of the notification.
     *
     * @return The Firebase Timestamp of the notification.
     */
    public Timestamp getTimestamp() {
        return new Timestamp(date);
    }

    /**
     * Gets the priority level of the notification.
     *
     * @return The priority of the notification (higher value indicates higher priority).
     */
    public long getPriority() {
        return priority;
    }

    /**
     * Gets the title of the notification.
     *
     * @return The title of the notification.
     */
    public String getTitle() {
        return title;
    }

    // Setters

    /**
     * Sets the description of the notification.
     *
     * @param description The new description of the notification.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the date of the notification.
     *
     * @param date The new date of the notification.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Sets the priority level of the notification.
     *
     * @param priority The new priority level of the notification.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Sets the title of the notification.
     *
     * @param title The new title of the notification.
     */
    public void setTitle(String title) {
        this.title = title;
    }

}


