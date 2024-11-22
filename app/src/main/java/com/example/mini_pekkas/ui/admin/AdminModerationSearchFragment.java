package com.example.mini_pekkas.ui.admin;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.mini_pekkas.databinding.FragmentAdminModerationSearchBinding; // Import binding
import com.example.mini_pekkas.Firebase;

/**
 * Fragment for performing moderation-related searches in the Admin interface.
 */
public class AdminModerationSearchFragment extends Fragment {

    private FragmentAdminModerationSearchBinding binding; // Declare binding variable

    /**
     * Called to initialize the fragment's view hierarchy.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's view.
     * @param container          The parent view group that this fragment's view will be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAdminModerationSearchBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Return the root view from binding
    }

    /**
     * Cleans up resources and prevents memory leaks by nullifying the binding.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by nullifying the binding when view is destroyed
    }
}