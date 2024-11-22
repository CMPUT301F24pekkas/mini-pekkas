package com.example.mini_pekkas;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Shared ViewModel for the Organizer actions.
 * Handles retrieving, updating and adding events
 */
public class OrganizerEventsListViewModel extends ViewModel {
    private final String DeviceId;
    private final Firebase firebaseHelper;
    private final MutableLiveData<ArrayList<Event>> EventList;
    private final MutableLiveData<Event> SelectedEvent;
    private boolean isDataInitialized = false;

    public OrganizerEventsListViewModel(Context context){
        firebaseHelper = new Firebase(context);
        DeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        ArrayList<Event> currentEventList = new ArrayList<>();
        EventList = new MutableLiveData<>();
        EventList.setValue(currentEventList);
        SelectedEvent = new MutableLiveData<>();
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public MutableLiveData<Event> getSelectedEvent() {
        return SelectedEvent;
    }
    public void setSelectedEvent(Event event) {
        assert event != null;
        SelectedEvent.setValue(event);
    }

    public LiveData<ArrayList<Event>> getEventList(){
        return EventList;
    }

    public void addEvent(Event event){
        ArrayList<Event> currentEventList = EventList.getValue();
        assert event != null;
        currentEventList.add(event);
        EventList.setValue(currentEventList);
        Log.d("OrganizerEventsListViewModel", "Event added: " + event.getName() + "tempListsize: " + currentEventList.size() + "EventListsize: " + currentEventList.size());

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
    public void updateEventInDb(Event event){
        firebaseHelper.updateEvent(event);
    }

    /**
     * this function ensures that the events are only loaded once
     *
     */
    public void initializeDataIfNeeded() {
        if (!isDataInitialized) {
            getEventsFromDb();
            isDataInitialized = true;
        }
    }
    /**
     * Retrieves events created by the user from db and adds them to the EventList.
     *
     */
    public void getEventsFromDb() {

        Firebase.EventListRetrievalListener listener = new Firebase.EventListRetrievalListener() {

            @Override
            public void onEventListRetrievalCompleted(ArrayList<Event> events) {
                Log.d("OrganizerEventsListViewModel", "Event list retrieval completed" + " size:" + events.size());
                //if no events

                for (Event event : events) {
                    addEvent(event);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("OrganizerEventsListViewModel", "Error occurred: " + e.getMessage());
            }

        };
        firebaseHelper.getOrganizedEvents(listener);

    }

}
