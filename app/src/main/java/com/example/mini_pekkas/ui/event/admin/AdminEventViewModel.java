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
    public MutableLiveData<String> getOrganizerName() {
        return organizerName;
    }

    public MutableLiveData<String> getEventName() {
        return eventName;
    }

    public MutableLiveData<String> getLocation() {
        return location;
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