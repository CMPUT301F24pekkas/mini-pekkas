package com.example.mini_pekkas.ui.event.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mini_pekkas.Event;

public class SharedEventViewModel extends ViewModel {
    private final MutableLiveData<String> qrCodeData = new MutableLiveData<>();
    private final MutableLiveData<Event> eventDetails = new MutableLiveData<>();

    public MutableLiveData<String> getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCode) {
        qrCodeData.setValue(qrCode);
    }

    public MutableLiveData<Event> getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(Event event) {
        eventDetails.setValue(event);
    }
}
