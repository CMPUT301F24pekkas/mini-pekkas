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

/**
 * ViewModel class for managing event data in the UI.
 * This class holds LiveData for event details and provides methods to update and retrieve them.
 */
public class EventViewModel extends ViewModel {

    // MutableLiveData for the Event object
    private final MutableLiveData<Event> event;

    // Other fields for additional event details
    private final MutableLiveData<String> eventName;
    private final MutableLiveData<String> organizerName;
    private final MutableLiveData<String> facility;
    private final MutableLiveData<String> eventDescription;
    private final MutableLiveData<String> eventImage;

    /**
     * Constructor for the EventViewModel.
     * Initializes all the MutableLiveData fields.
     */
    public EventViewModel() {
        // Initialize MutableLiveData fields
        event = new MutableLiveData<>();
        eventName = new MutableLiveData<>();
        organizerName = new MutableLiveData<>();
        facility = new MutableLiveData<>();
        eventDescription = new MutableLiveData<>();
        eventImage = new MutableLiveData<>();
    }

    // Getter for the Event object
    /**
     * Getter for the Event object.
     *
     * @return The MutableLiveData containing the Event object
     */
    public MutableLiveData<Event> getEvent() {
        return event;
    }

    // Method to set the Event object and update other related fields
    /**
     * Sets the Event object and updates other related fields accordingly.
     *
     * @param event The Event object to set
     */
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
    /**
     * Getter for the event name.
     *
     * @return The MutableLiveData containing the event name
     */
    public MutableLiveData<String> getEventName() {
        return eventName;
    }

    /**
     * Getter for the organizer's name.
     *
     * @return The MutableLiveData containing the organizer's name
     */
    public MutableLiveData<String> getOrganizerName() {
        return organizerName;
    }

    /**
     * Getter for the facility where the event is held.
     *
     * @return The MutableLiveData containing the facility name
     */
    public MutableLiveData<String> getFacility() {
        return facility;
    }

    /**
     * Getter for the event description.
     *
     * @return The MutableLiveData containing the event description
     */
    public MutableLiveData<String> getEventDescription() {
        return eventDescription;
    }

    /**
     * Getter for the event image resource.
     *
     * @return The MutableLiveData containing the event image resource ID
     */
    public MutableLiveData<String> getEventImage() {
        return eventImage;
    }
}
