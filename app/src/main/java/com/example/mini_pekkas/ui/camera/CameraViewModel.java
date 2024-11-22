package com.example.mini_pekkas.ui.camera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for the Camera Fragment.
 * This ViewModel is responsible for managing and providing data to the Camera fragment.
 * The CameraViewModel class extends ViewModel to store and manage UI-related data
 */

public class CameraViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructor for CameraViewModel.
     * Initializes the MutableLiveData object with a default value
     */
    public CameraViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is camera fragment");
    }

    /**
     * Provides access to the text message as Livedata.
     *
     * @return a LiveData object containing the text message.
     */

    public LiveData<String> getText() {
        return mText;
    }
}