package com.example.mini_pekkas.ui.event.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.mini_pekkas.R;

public class EventViewModel extends ViewModel {

    // all the texts that can be changed within the event view
    private final MutableLiveData<String> eventName;
    private final MutableLiveData<String> organizerName;
    private final MutableLiveData<String> location;
    private final MutableLiveData<String> eventDescription;
    private final MutableLiveData<String> eventDetails;
    private final MutableLiveData<Integer> eventImage;
    private final MutableLiveData<Event> event;


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

        // Mock Data Event

        event = new MutableLiveData<>();
        event.setValue(mockEvent());

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

    public MutableLiveData<Event> getEvent() { return event; }


    // Mock data
    private Event mockEvent() {
        Map<String, Object> user1Data = new HashMap<>();
        user1Data.put("name", "John Doe");
        user1Data.put("email", "john@example.com");
        user1Data.put("phone", "123-456-7890");
        user1Data.put("isOrganizer", false);

        Map<String, Object> user2Data = new HashMap<>();
        user2Data.put("name", "Alice Smith");
        user2Data.put("email", "alice@example.com");
        user2Data.put("phone", "098-765-4321");
        user2Data.put("isOrganizer", false);

        User user1 = new User(user1Data);
        User user2 = new User(user2Data);

        ArrayList<User> waitlist = new ArrayList<>();
        waitlist.add(user1);

        return new Event("1", "Sample Event", user2, "Sample event description",
                "2024-11-05", "2024-11-06", 50,
                "Community Hall", 37.7749, -122.4194,
                100, waitlist,
                "CHK123", true);
    }

}