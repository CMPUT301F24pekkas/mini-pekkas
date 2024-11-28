package com.example.mini_pekkas.ui.event.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Event;

/**
 * ViewModel class for sharing event-related data between fragments or activities.
 * This class holds LiveData for QR code data and event details, allowing them to be observed and updated.
 */
public class SharedEventViewModel extends ViewModel {
    private final MutableLiveData<String> qrCodeData = new MutableLiveData<>();
    private final MutableLiveData<Event> eventDetails = new MutableLiveData<>();
    private final MutableLiveData<String> navigationSource = new MutableLiveData<>();

    /**
     * Setter for navigation source
     * @param source
     */
    public void setNavigationSource(String source) {
        navigationSource.setValue(source);
    }

    /**
     * Getter for navigation source
     * @return
     */
    public LiveData<String> getNavigationSource() {
        return navigationSource;
    }
    /**
     * Getter for the QR code data.
     *
     * @return The MutableLiveData containing the QR code data
     */
    public MutableLiveData<String> getQrCodeData() {
        return qrCodeData;
    }

    /**
     * Sets the QR code data.
     *
     * @param qrCode The QR code data to set
     */
    public void setQrCodeData(String qrCode) {
        qrCodeData.setValue(qrCode);
    }

    /**
     * Getter for the event details.
     *
     * @return The MutableLiveData containing the event details
     */
    public MutableLiveData<Event> getEventDetails() {
        return eventDetails;
    }

    /**
     * Sets the event details.
     *
     * @param event The Event object to set
     */
    public void setEventDetails(Event event) {
        eventDetails.setValue(event);
    }
}
