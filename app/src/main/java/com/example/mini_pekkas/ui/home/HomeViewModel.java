package com.example.mini_pekkas.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> eventName;
    private final MutableLiveData<String> eventDescription;
    private final MutableLiveData<String> eventDayCount;


    public HomeViewModel() {
        eventName = new MutableLiveData<>();
        eventDescription = new MutableLiveData<>();
        eventDayCount = new MutableLiveData<>();

        eventName.setValue("A Community Event!");
        eventDescription.setValue("This is a very long event description because this is a very cool even that has a lot of description to it because it needs a lot of description and lots of description is very cool. Now what if this event description ran so long that it went of the page!!!! that would be crazy fr!!!!!!! but i need to test if this is actually a scrollable view and it wont just die after getting out of bounds!!!!! I need some more text here, I probably shouldve just used the built in example text they always start you off with. But hey this works!");
        eventDayCount.setValue("Starts in %d Days");
    }

    public MutableLiveData<String> getEventDescription() {
        return eventDescription;
    }

    public MutableLiveData<String> getEventName() {
        return eventName;
    }

    public MutableLiveData<String> getEventDayCount() {
        return eventDayCount;
    }

}