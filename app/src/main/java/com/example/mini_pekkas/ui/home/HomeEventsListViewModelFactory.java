package com.example.mini_pekkas.ui.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory class for creating instances of the HomeEventsListViewModel.
 * This factory provides the necessary context to the ViewModel when it is created.
 */
public class HomeEventsListViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    /**
     * Constructor for the HomeEventsListViewModelFactory.
     * Initializes the context which will be passed to the ViewModel.
     *
     * @param context The context for accessing system services (e.g., device ID).
     */
    public HomeEventsListViewModelFactory(Context context) {
        this.context = context;
    }

    /**
     * Creates and returns an instance of the HomeEventsListViewModel.
     *
     * @param modelClass The ViewModel class to be created.
     * @param <T>        The type of ViewModel.
     * @return A new instance of HomeEventsListViewModel.
     * @throws IllegalArgumentException If the ViewModel class is not of type HomeEventsListViewModel.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeEventsListViewModel.class)) {
            return (T) new HomeEventsListViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

