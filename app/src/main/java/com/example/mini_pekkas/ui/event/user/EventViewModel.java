package com.example.mini_pekkas.ui.event.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.example.mini_pekkas.Firebase;


public class EventViewModel extends ViewModel {

    // MutableLiveData for the Event object
    private final MutableLiveData<Event> event;

    // Other fields for additional event details
    private final MutableLiveData<String> eventName;
    private final MutableLiveData<String> organizerName;
    private final MutableLiveData<String> facility;
    private final MutableLiveData<String> eventDescription;
    private final MutableLiveData<String> eventDetails;
    private final MutableLiveData<Integer> eventImage;

    public EventViewModel() {
        // Initialize MutableLiveData fields
        event = new MutableLiveData<>();
        eventName = new MutableLiveData<>();
        organizerName = new MutableLiveData<>();
        facility = new MutableLiveData<>();
        eventDescription = new MutableLiveData<>();
        eventDetails = new MutableLiveData<>();
        eventImage = new MutableLiveData<>();
    }

    // Getter for the Event object
    public MutableLiveData<Event> getEvent() {
        return event;
    }

    // Method to set the Event object and update other related fields
    public void setEvent(Event event) {
        this.event.setValue(event);

        // Optionally update other fields
        if (event != null) {
            eventName.setValue(event.getName());
            organizerName.setValue(event.getEventHost().getName());
            facility.setValue(event.getFacility());
            eventDescription.setValue(event.getDescription());
        }
    }

    // Getters for other fields
    public MutableLiveData<String> getEventName() {
        return eventName;
    }

    public MutableLiveData<String> getOrganizerName() {
        return organizerName;
    }

    public MutableLiveData<String> getFacility() {
        return facility;
    }

    public MutableLiveData<String> getEventDescription() {
        return eventDescription;
    }

    public MutableLiveData<String> getEventDetails() {
        return eventDetails;
    }

    public MutableLiveData<Integer> getEventImage() {
        return eventImage;
    }
}
