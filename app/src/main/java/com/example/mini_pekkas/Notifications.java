package com.example.mini_pekkas;

public class Notifications {
    private String description;
    private String date;
    private int priority;
    private String title;

    public Notifications(String description, String date, int priority, String title) {
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public int getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    // Setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}


