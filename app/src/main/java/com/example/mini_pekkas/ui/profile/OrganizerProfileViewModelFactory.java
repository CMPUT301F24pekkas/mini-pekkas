package com.example.mini_pekkas.ui.profile;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class OrganizerProfileViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public OrganizerProfileViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrganizerProfileViewModel.class)) {
            return (T) new OrganizerProfileViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
