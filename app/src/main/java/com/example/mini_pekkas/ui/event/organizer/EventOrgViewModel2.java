package com.example.mini_pekkas.ui.event.organizer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.R;

/**
 * ViewModel class for managing event data in the organizer's event view, including participant details.
 * Provides LiveData for dynamic event information that can be observed and updated in the UI.
 */
public class EventOrgViewModel2 extends ViewModel {

    // LiveData for all text fields that can be updated within the event view
    private final MutableLiveData<String> eventName;
    private final MutableLiveData<String> organizerName;
    private final MutableLiveData<String> location;
    private final MutableLiveData<String> eventDescription;
    private final MutableLiveData<String> eventDetails;

    // LiveData for participant details
    private final MutableLiveData<String> chosenPart;
    private final MutableLiveData<String> canceledPart;
    private final MutableLiveData<String> enrolledPart;

    // LiveData for the event image
    private final MutableLiveData<Integer> eventImage;

    /**
     * Initializes the ViewModel with default values for event details,
     * participant statistics, and placeholder images.
     */
    public EventOrgViewModel2() {
        // Initializing text fields as MutableLiveData
        eventName = new MutableLiveData<>();
        organizerName = new MutableLiveData<>();
        location = new MutableLiveData<>();
        eventDescription = new MutableLiveData<>();
        eventDetails = new MutableLiveData<>();
        chosenPart = new MutableLiveData<>();
        enrolledPart = new MutableLiveData<>();
        canceledPart = new MutableLiveData<>();

        // Initializing event image as MutableLiveData
        eventImage = new MutableLiveData<>();

        // Setting placeholder values for text fields
        eventName.setValue("A Community Event!");
        organizerName.setValue("Organizer Name");
        location.setValue("Location");
        eventDescription.setValue("Event description here.");
        eventDetails.setValue("Event details here.");
        chosenPart.setValue("123");
        canceledPart.setValue("123");
        enrolledPart.setValue("22/100");

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
     * Gets the LiveData object for the event name.
     *
     * @return A MutableLiveData<String> containing the event name.
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

    /**
     * Gets the LiveData object for the number of chosen participants.
     *
     * @return A MutableLiveData<String> containing the chosen participant count.
     */
    public MutableLiveData<String> getChosenPart() {
        return chosenPart;
    }

    /**
     * Gets the LiveData object for the number of canceled participants.
     *
     * @return A MutableLiveData<String> containing the canceled participant count.
     */
    public MutableLiveData<String> getCanceledPart() {
        return canceledPart;
    }

    /**
     * Gets the LiveData object for the number of enrolled participants.
     *
     * @return A MutableLiveData<String> containing the enrolled participant count.
     */
    public MutableLiveData<String> getEnrolledPart() {
        return enrolledPart;
    }
}
