package com.example.mini_pekkas.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for managing the notification text in the app.
 * This class stores and provides the notification data to the UI.
 */
public class NotificationsViewModel extends ViewModel {


    private final MutableLiveData<String> mText;

    /**
     * Constructor for NotificationsViewModel.
     * Initializes the MutableLiveData field.
     */
    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
    }

    /**
     * Gets the notification text.
     *
     * @return A LiveData object containing the notification text
     */
    public LiveData<String> getNotification() {
        return mText;
    }
}