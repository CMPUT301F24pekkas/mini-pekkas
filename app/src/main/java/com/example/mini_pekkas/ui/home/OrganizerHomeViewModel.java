package com.example.mini_pekkas.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.Event;

import android.content.Context;

import java.util.List;

public class OrganizerHomeViewModel extends ViewModel {

    private final MutableLiveData<Event> selectedEvent;
    private final Firebase firebaseHelper;

    public OrganizerHomeViewModel(Context context) {
        selectedEvent = new MutableLiveData<>();
        firebaseHelper = new Firebase(context);
    }

    // load the event using eventID from Firebase
    private void loadEvent(String eventID) {
        firebaseHelper.getEvent(eventID, new Firebase.EventRetrievalListener() {
            @Override
            public void onEventRetrievalCompleted(Event event) {
                selectedEvent.setValue(event);
            }

            @Override
            public void onError(Exception e) {
                // Handle error (event not found or Firebase error)
                selectedEvent.setValue(null);
            }
        });
    }

    // update the event in Firebase
    public void updateEventInFirebase(Event event) {
        firebaseHelper.updateEvent(event);
    }


    // Getters
    public LiveData<Event> getSelectedEvent() {
        return selectedEvent;
    }
}
