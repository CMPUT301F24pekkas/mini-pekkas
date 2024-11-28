package com.example.mini_pekkas.ui.event.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.databinding.FragmentWaitlistBinding;

public class WaitlistedEntrantsFragment extends Fragment {
    private FragmentWaitlistBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        binding = FragmentWaitlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}