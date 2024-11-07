package com.example.mini_pekkas.ui.profile;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
/**
 * Factory class for creating an instance of {@link OrganizerProfileViewModel}.
 * Supplies the required {@link Context} for the ViewModel.
 */
public class OrganizerProfileViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public OrganizerProfileViewModelFactory(Context context) {
        this.context = context;
    }
    /**
     * Creates a new instance of the given {@link ViewModel} class.
     *
     * @param modelClass Class of the ViewModel to create.
     * @param <T>        The type parameter for the ViewModel.
     * @return An instance of {@link OrganizerProfileViewModel} if the class is assignable;
     * throws an exception otherwise.
     * @throws IllegalArgumentException if the ViewModel class is unknown.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrganizerProfileViewModel.class)) {
            return (T) new OrganizerProfileViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
