package com.example.mini_pekkas;

import android.content.Context;
import android.provider.Settings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Shared ViewModel for the Organizer actions.
 * Handles retrieving, updating and adding events
 */
public class OrganizerEventsViewModel {
    private final String DeviceId;
    private final Firebase firebaseHelper;
    private MutableLiveData<ArrayList<Event>> EventList;

    public OrganizerEventsViewModel(Context context){
        firebaseHelper = new Firebase(context);
        DeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        ArrayList<Event> currentEventList = new ArrayList<>();
        EventList.setValue(currentEventList);
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void addEvent(Event event){
        ArrayList<Event> currentEventList = EventList.getValue();
        assert event != null;
        currentEventList.add(event);
        EventList.setValue(currentEventList);

    }
    public void deleteEvent(String eventId){
        ArrayList<Event> currentEventList = EventList.getValue();
        for(int i = 0; i < currentEventList.size(); i++){
            if (Objects.equals(currentEventList.get(i).getId(), eventId)){
                currentEventList.remove(i);
                break;
            }
        }
        EventList.setValue(currentEventList);

    }
//TODO: event
//    public void editEvent(Event event){
//
//    }

//    private ArrayList<Event> getEventsFromDb{
//
//    }
//
}
