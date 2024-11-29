package com.example.mini_pekkas.ui.event.organizer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NumEntrantsViewModel extends ViewModel {
    private final MutableLiveData<Integer> number = new MutableLiveData<>();
    public NumEntrantsViewModel(){
        number.setValue(-1);
    }
    public void setNumber(int value) {
        number.setValue(value);
    }

    public LiveData<Integer> getNumber() {
        return number;
    }
}