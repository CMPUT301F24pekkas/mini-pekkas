package com.example.mini_pekkas;

import java.util.List;
import java.util.ArrayList;


public class EventList {

    // Name of the event and a list of all the participants
    private String eventName;
    private List<AppUser> participants;

    public EventList(String eventName) {
        this.eventName = eventName;
        this.participants = new ArrayList<AppUser>();
    }

    // Getters
    public String getEventName() {
        return eventName;
    }

    public List<AppUser> getParticipants() {
        return participants;
    }

    // Method to add participant to event
    public void joinEvent(AppUser participant) {
        participants.add(participant);
    }

    // Method to get the number of participants in event
    public int getParticipantCount() {
        return participants.size();
    }
}
