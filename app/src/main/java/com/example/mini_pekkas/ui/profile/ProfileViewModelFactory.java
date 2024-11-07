package com.example.mini_pekkas.ui.profile;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
/**
 * Factory class for creating instances of ProfileViewModel with a specified context.
 * This enables dependency injection of a Context object into the ProfileViewModel.
 */
public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    /**
     * Initializes a new instance of ProfileViewModelFactory.
     *
     * @param context The context to pass into ProfileViewModel instances created by this factory.
     */
    public ProfileViewModelFactory(Context context) {
        this.context = context;
    }
    /**
     * Creates a new instance of ProfileViewModel if the specified model class is assignable from
     * ProfileViewModel. Throws an IllegalArgumentException for unknown ViewModel classes.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of ProfileViewModel.
     * @throws IllegalArgumentException if the modelClass is not ProfileViewModel.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
