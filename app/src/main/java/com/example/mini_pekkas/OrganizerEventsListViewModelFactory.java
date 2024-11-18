package com.example.mini_pekkas;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.OrganizerEventsViewModel;

public class OrganizerEventsViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public OrganizerEventsViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrganizerEventsViewModel.class)) {
            T classCast = modelClass.cast(new OrganizerEventsViewModel(context));
            assert classCast != null;
            return classCast;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

