package com.example.mini_pekkas.ui.event.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.R;

public class EventViewModel extends ViewModel {

    // all the texts that can be changed within the event view
    private final MutableLiveData<String> eventName;
    private final MutableLiveData<String> organizerName;
    private final MutableLiveData<String> location;
    private final MutableLiveData<String> eventDescription;
    private final MutableLiveData<String> eventDetails;
    private final MutableLiveData<Integer> eventImage;



    public EventViewModel() {
        // initializing all the text views that can change as MutableLiveData types
        eventName = new MutableLiveData<>();
        organizerName = new MutableLiveData<>();
        location = new MutableLiveData<>();
        eventDescription = new MutableLiveData<>();
        eventDetails = new MutableLiveData<>();

        // initializing all the image views that can change
        eventImage = new MutableLiveData<>();

        // placeholder text for now
        eventName.setValue("A Community Event!");
        organizerName.setValue("Organizer Name");
        location.setValue("Location");
        eventDescription.setValue("This is a very long event description because this is a very cool even that has a lot of description to it because it needs a lot of description and lots of description is very cool. Now what if this event description ran so long that it went of the page!!!! that would be crazy fr!!!!!!! but i need to test if this is actually a scrollable view and it wont just die after getting out of bounds!!!!! I need some more text here, I probably shouldve just used the built in example text they always start you off with. But hey this works!");
        eventDetails.setValue("This is a very long event description because this is a very cool even that has a lot of description to it because it needs a lot of description and lots of description is very cool");

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