package com.example.mini_pekkas.ui.home;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class OrganizerHomeViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public OrganizerHomeViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrganizerHomeViewModel.class)) {
            return (T) new OrganizerHomeViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
