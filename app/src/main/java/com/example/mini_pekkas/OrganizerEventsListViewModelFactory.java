package com.example.mini_pekkas;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class OrganizerEventsListViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public OrganizerEventsListViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrganizerEventsListViewModel.class)) {
            T classCast = modelClass.cast(new OrganizerEventsListViewModel(context));
            assert classCast != null;
            return classCast;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

