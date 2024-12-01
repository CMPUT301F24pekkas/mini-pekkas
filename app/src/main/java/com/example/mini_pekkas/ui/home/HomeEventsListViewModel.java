package com.example.mini_pekkas.ui.home;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Shared ViewModel for the user actions.
 * Handles retrieving, updating, adding, and deleting events for users.
 */
public class HomeEventsListViewModel extends ViewModel {
    private final String DeviceId;
    private final Firebase firebaseHelper;
    private final MutableLiveData<ArrayList<Event>> EventList;
    private final MutableLiveData<Event> SelectedEvent;
    private boolean isDataInitialized = false;

    /**
     * Constructor for the HomeEventsListViewModel.
     * Initializes Firebase helper, DeviceId, and sets up the EventList.
     *
     * @param context The context for accessing system services (e.g., device ID).
     */
    public HomeEventsListViewModel(Context context){
        firebaseHelper = new Firebase(context);
        DeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        ArrayList<Event> currentEventList = new ArrayList<>();
        EventList = new MutableLiveData<>();
        EventList.setValue(currentEventList);
        SelectedEvent = new MutableLiveData<>();
    }

    /**
     * Returns the device ID of the user.
     *
     * @return The unique device ID of the user.
     */
    public String getDeviceId() {
        return DeviceId;
    }

    /**
     * Returns the selected event.
     *
     * @return The MutableLiveData object representing the selected event.
     */
    public MutableLiveData<Event> getSelectedEvent() {
        return SelectedEvent;
    }

    /**
     * Adds a new event to the event list.
     *
     * @param event The event to be added to the list.
     */
    public void setSelectedEvent(Event event) {
        assert event != null;
        SelectedEvent.setValue(event);
    }

    /**
     * Retrieves events created by the organizer from the database and adds them to the EventList.
     */
    public LiveData<ArrayList<Event>> getEventList(){
        return EventList;
    }

    /**
     * Ensures that event data is only loaded once by checking if data has already been initialized.
     * If not, it calls getEventsFromDb() to retrieve event data.
     */
    public void addEvent(Event event) {
        ArrayList<Event> currentEventList = EventList.getValue();
        if (currentEventList == null) {
            currentEventList = new ArrayList<>();
        }
        currentEventList.add(event);
        EventList.postValue(currentEventList);
    }

    /**
     * Deletes an event from the event list based on the event ID.
     *
     * @param eventId The ID of the event to be deleted.
     */
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
     * Retrieves events waitlisted by the user from db and adds them to the EventList.
     *
     */
    public void getEventsFromDb() {

        Firebase.EventListRetrievalListener listener = new Firebase.EventListRetrievalListener() {

            @Override
            public void onEventListRetrievalCompleted(ArrayList<Event> events) {
                Log.d("HomeEventsListViewModel", "Event list retrieval completed" + " size:" + events.size());
                //if no events

                for (Event event : events) {
                    addEvent(event);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("HomeEventsListViewModel", "Error occurred: " + e.getMessage());
            }

        };
        firebaseHelper.getWaitlistedEvents(listener);

    }
    public void removeEventFromDb(Event event) {
        firebaseHelper.leaveEvent(event);
    }
}
