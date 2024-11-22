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