package com.example.mini_pekkas.ui.event.admin;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.mini_pekkas.databinding.FragmentAdminBrowseProfilesBinding; // Import binding
import com.example.mini_pekkas.Firebase;

public class AdminBrowseProfile extends Fragment {

    private FragmentAdminBrowseProfilesBinding binding; // Declare binding variable

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAdminBrowseProfilesBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Return the root view from binding
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by nullifying the binding when view is destroyed
    }
}