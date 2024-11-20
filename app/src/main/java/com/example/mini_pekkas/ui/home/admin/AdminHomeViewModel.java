package com.example.mini_pekkas.ui.home.admin;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminHomeViewModel extends ViewModel {

    private final MutableLiveData<String> test;


    public AdminHomeViewModel() {
        test = new MutableLiveData<>();

        test.setValue("A test value!");
    }

    public MutableLiveData<String> getTest() {
        return test;
    }

}