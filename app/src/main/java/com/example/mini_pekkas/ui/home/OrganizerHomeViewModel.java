package com.example.mini_pekkas.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.Event;

import android.content.Context;
import android.util.Log;

/**
 * ViewModel for the Organizer Home Fragment.
 * Handles retrieving and updating events from Firebase for organizers.
 */
public class OrganizerHomeViewModel extends ViewModel {

    private final MutableLiveData<Event> selectedEvent;
    private final Firebase firebaseHelper;

    /**
     * Constructor to initialize the ViewModel with context.
     *
     * @param context The application context required for Firebase operations.
     */
    public OrganizerHomeViewModel(Context context) {
        selectedEvent = new MutableLiveData<>();
        firebaseHelper = new Firebase(context);
    }

    /**
     * Loads the event details using the provided event ID from Firebase.
     *
     * @param eventID The ID of the event to be retrieved.
     */
    public void loadEvent(String eventID) {
        firebaseHelper.getEvent(eventID, new Firebase.EventRetrievalListener() {
            @Override
            public void onEventRetrievalCompleted(Event event) {
                if (event != null) {
                    selectedEvent.setValue(event);
                    Log.d("OrganizerHomeViewModel", "Event retrieved: " + event.getName());
                } else {
                    Log.w("OrganizerHomeViewModel", "No event found with ID: " + eventID);
                    selectedEvent.setValue(null);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("OrganizerHomeViewModel", "Error retrieving event: " + e.getMessage());
                selectedEvent.setValue(null);
            }
        });
    }

    /**
     * Updates the event details in Firebase.
     *
     * @param event The Event object to be updated in Firebase.
     */
    public void updateEventInFirebase(Event event) {
        if (event != null) {
            firebaseHelper.updateEvent(event);
            Log.d("OrganizerHomeViewModel", "Event updated: " + event.getName());
        } else {
            Log.w("OrganizerHomeViewModel", "Attempted to update a null event");
        }
    }

    /**
     * Returns the LiveData object containing the selected event.
     *
     * @return A LiveData object wrapping the selected Event.
     */
    public LiveData<Event> getSelectedEvent() {
        return selectedEvent;
    }
}
