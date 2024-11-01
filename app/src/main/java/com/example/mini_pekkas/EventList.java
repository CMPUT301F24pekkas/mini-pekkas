package com.example.mini_pekkas;

import java.util.List;
import java.util.ArrayList;


/* Fill in List parameter with User e.x List<User>
   EventParticipant should also be replaced by User (or whatever the name of the user object is)
 */
public class EventList {

    // Name of the event and a list of all the participants
    private String eventName;
    private List<User> participants;

    public EventList(String eventName) {
        this.eventName = eventName;
        this.participants = new ArrayList<User>();
    }

    // Getters
    public String getEventName() {
        return eventName;
    }

    public List<User> getParticipants() {
        return participants;
    }

    // Method to add participant to event
    public void joinEvent(User participant) {
        participants.add(participant);
    }

    // Method to get the number of participants in event
    public int getParticipantCount() {
        return participants.size();
    }
}
