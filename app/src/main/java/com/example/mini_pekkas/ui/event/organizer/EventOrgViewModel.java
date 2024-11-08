package com.example.mini_pekkas.ui.event.organizer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.R;

/**
 * ViewModel class for managing event data in the organizer's event view.
 * Provides LiveData for dynamic event details that the UI can observe and update accordingly.
 */
public class EventOrgViewModel extends ViewModel {

    // LiveData for all the text fields that can be changed within the event view
    private final MutableLiveData<String> eventName;
    private final MutableLiveData<String> organizerName;
    private final MutableLiveData<String> location;
    private final MutableLiveData<String> eventDescription;
    private final MutableLiveData<String> eventDetails;

    // LiveData for the event image
    private final MutableLiveData<Integer> eventImage;

    /**
     * Initializes the ViewModel with default values for the event data,
     * including placeholders for text fields and the event image.
     */
    public EventOrgViewModel() {
        // Initializing all the text fields as MutableLiveData
        eventName = new MutableLiveData<>();
        organizerName = new MutableLiveData<>();
        location = new MutableLiveData<>();
        eventDescription = new MutableLiveData<>();
        eventDetails = new MutableLiveData<>();

        // Initializing the event image as MutableLiveData
        eventImage = new MutableLiveData<>();

        // Setting placeholder values for text fields
        eventName.setValue("A Community Event!");
        organizerName.setValue("Organizer Name");
        location.setValue("Location");
        eventDescription.setValue("Event description here.");
        eventDetails.setValue("Event details here.");

        // Setting a placeholder image
        eventImage.setValue(R.drawable.ic_launcher_background);
    }

    /**
     * Gets the LiveData object for the organizer's name.
     *
     * @return A MutableLiveData<String> containing the organizer's name.
     */
    public MutableLiveData<String> getOrganizerName() {
        return organizerName;
    }

    /**
     * Gets the LiveData object for the event's name.
     *
     * @return A MutableLiveData<String> containing the event's name.
     */
    public MutableLiveData<String> getEventName() {
        return eventName;
    }

    /**
     * Gets the LiveData object for the event location.
     *
     * @return A MutableLiveData<String> containing the event location.
     */
    public MutableLiveData<String> getLocation() {
        return location;
    }

    /**
     * Gets the LiveData object for the event description.
     *
     * @return A MutableLiveData<String> containing the event description.
     */
    public MutableLiveData<String> getEventDescription() {
        return eventDescription;
    }

    /**
     * Gets the LiveData object for additional event details.
     *
     * @return A MutableLiveData<String> containing additional event details.
     */
    public MutableLiveData<String> getEventDetails() {
        return eventDetails;
    }

    /**
     * Gets the LiveData object for the event image.
     *
     * @return A MutableLiveData<Integer> containing the resource ID of the event image.
     */
    public MutableLiveData<Integer> getEventImage() {
        return eventImage;
    }
}
