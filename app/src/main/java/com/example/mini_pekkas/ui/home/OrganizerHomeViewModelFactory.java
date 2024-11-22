package com.example.mini_pekkas.ui.home;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory class for creating instances of the OrganizerHomeViewModel.
 * This factory is used to inject the context into the ViewModel upon creation.
 */
public class OrganizerHomeViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    /**
     * Constructor for the factory.
     * Initializes the factory with the provided context.
     *
     * @param context The context used for creating the OrganizerHomeViewModel
     */
    public OrganizerHomeViewModelFactory(Context context) {
        this.context = context;
    }

    /**
     * Creates and returns an instance of the specified ViewModel class.
     * If the requested ViewModel class is OrganizerHomeViewModel, it will be created and returned.
     *
     * @param modelClass    The class of the ViewModel to be created
     * @param <T>           The type of the ViewModel
     * @return A new instance of the requested ViewModel class
     * @throws IllegalArgumentException If the ViewModel class is unknown
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrganizerHomeViewModel.class)) {
            return (T) new OrganizerHomeViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
