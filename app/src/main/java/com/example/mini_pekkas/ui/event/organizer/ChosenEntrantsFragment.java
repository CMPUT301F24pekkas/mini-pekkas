package com.example.mini_pekkas.ui.event.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.databinding.FragmentChosenBinding;


public class ChosenEntrantsFragment extends Fragment {
    private FragmentChosenBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        binding = FragmentChosenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}
