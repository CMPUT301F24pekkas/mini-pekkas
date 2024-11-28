package com.example.mini_pekkas.ui.home;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory class for creating instances of the OrganizerEventsListViewModel.
 * This factory provides the necessary context to the ViewModel when it is created.
 */
public class OrganizerEventsListViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    /**
     * Constructor for the OrganizerEventsListViewModelFactory.
     * Initializes the context which will be passed to the ViewModel.
     *
     * @param context The context for accessing system services (e.g., device ID).
     */
    public OrganizerEventsListViewModelFactory(Context context) {
        this.context = context;
    }

    /**
     * Creates and returns an instance of the OrganizerEventsListViewModel.
     *
     * @param modelClass The ViewModel class to be created.
     * @param <T>        The type of ViewModel.
     * @return A new instance of OrganizerEventsListViewModel.
     * @throws IllegalArgumentException If the ViewModel class is not of type OrganizerEventsListViewModel.
     */
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

