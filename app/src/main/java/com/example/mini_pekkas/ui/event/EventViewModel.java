package com.example.mini_pekkas.ui.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is event fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}