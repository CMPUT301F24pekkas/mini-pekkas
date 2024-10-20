package com.example.mini_pekkas.ui.event.organizer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.R;

public class EventOrgViewModel2 extends ViewModel {

    // all the texts that can be changed within the event view
    private final MutableLiveData<String> eventName;
    private final MutableLiveData<String> organizerName;
    private final MutableLiveData<String> location;
    private final MutableLiveData<String> eventDescription;
    private final MutableLiveData<String> eventDetails;
    private final MutableLiveData<Integer> eventImage;
    private final MutableLiveData<String> chosenPart;
    private final MutableLiveData<String> canceledPart;
    private final MutableLiveData<String> enrolledPart;



    public EventOrgViewModel2() {
        // initializing all the text views that can change as MutableLiveData types
        eventName = new MutableLiveData<>();
        organizerName = new MutableLiveData<>();
        location = new MutableLiveData<>();
        eventDescription = new MutableLiveData<>();
        eventDetails = new MutableLiveData<>();
        chosenPart = new MutableLiveData<>();
        enrolledPart = new MutableLiveData<>();
        canceledPart = new MutableLiveData<>();

        // initializing all the image views that can change
        eventImage = new MutableLiveData<>();

        // placeholder text for now
        eventName.setValue("A Community Event!");
        organizerName.setValue("Organizer Name");
        location.setValue("Location");
        eventDescription.setValue("Event description here.");
        eventDetails.setValue("Event details here.");
        chosenPart.setValue("123");
        canceledPart.setValue("123");
        enrolledPart.setValue("22/100");

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
    public MutableLiveData<String> getChosenPart() {
        return chosenPart;
    }

    public MutableLiveData<String> getCanceledPart() {
        return canceledPart;
    }

    public MutableLiveData<String> getEnrolledPart() {
        return enrolledPart;
    }


}