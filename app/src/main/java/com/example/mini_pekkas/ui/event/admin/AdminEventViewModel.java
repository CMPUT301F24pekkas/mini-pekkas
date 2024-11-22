package com.example.mini_pekkas.ui.event.admin;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.R;

/**
 * View model for the event fragment.
 * TODO Need to adjust data as needed
 */
public class AdminEventViewModel extends ViewModel {

    // all the texts that can be changed within the event view
    private final MutableLiveData<String> eventName;
    private final MutableLiveData<String> organizerName;
    private final MutableLiveData<String> location;
    private final MutableLiveData<String> eventDescription;
    private final MutableLiveData<String> eventDetails;
    private final MutableLiveData<Integer> eventImage;

    /**
     * Constructor to initialize all mutable live data for the event details.
     */
    public AdminEventViewModel() {
        // initializing all the text views that can change as MutableLiveData types
        eventName = new MutableLiveData<>();
        organizerName = new MutableLiveData<>();
        location = new MutableLiveData<>();
        eventDescription = new MutableLiveData<>();
        eventDetails = new MutableLiveData<>();

        // initializing all the image views that can change
        eventImage = new MutableLiveData<>();

        // placeholder text for now
//        eventName.setValue(mockEvent().getName());
//        organizerName.setValue(mockEvent().getEventHost().getName());
//        location.setValue("Location");
//        eventDescription.setValue(mockEvent().getDescription());

        // placeholder for images for now
        eventImage.setValue(R.drawable.ic_launcher_background);


    }

    // getters for all the text views
    /**
     * Gets the organizer's name.
     *
     * @return the live data for the organizer's name
     */
    public MutableLiveData<String> getOrganizerName() {
        return organizerName;
    }

    /**
     * Gets the event name.
     *
     * @return the live data for the event name
     */
    public MutableLiveData<String> getEventName() {
        return eventName;
    }

    /**
     * Gets the event location.
     *
     * @return the live data for the event location
     */
    public MutableLiveData<String> getLocation() {
        return location;
    }

    /**
     * Gets the event description.
     *
     * @return the live data for the event description
     */
    public MutableLiveData<String> getEventDescription() {
        return eventDescription;
    }

    /**
     * Gets the event details.
     *
     * @return the live data for the event details
     */
    public MutableLiveData<String> getEventDetails() {
        return eventDetails;
    }

    /**
     * Gets the event image.
     *
     * @return the live data for the event image resource
     */
    public MutableLiveData<Integer> getEventImage() {
        return eventImage;
    }

}