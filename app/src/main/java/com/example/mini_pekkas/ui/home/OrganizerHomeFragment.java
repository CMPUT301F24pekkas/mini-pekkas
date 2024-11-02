package com.example.mini_pekkas.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.databinding.FragmentOrganizerHomeBinding;

public class OrganizerHomeFragment extends Fragment {

    private FragmentOrganizerHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrganizerHomeViewModel homeViewModel =
                new ViewModelProvider(this).get(OrganizerHomeViewModel.class);

        binding = FragmentOrganizerHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}