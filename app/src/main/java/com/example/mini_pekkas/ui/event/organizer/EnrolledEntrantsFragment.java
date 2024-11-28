package com.example.mini_pekkas.ui.event.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.UserArrayAdapter;
import com.example.mini_pekkas.databinding.FragmentEnrolledBinding;

import java.util.ArrayList;


public class EnrolledEntrantsFragment extends Fragment {
    private FragmentEnrolledBinding binding;
    private UserArrayAdapter enrolledArrayAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        binding = FragmentEnrolledBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //make ArrayAdapter

        enrolledArrayAdapter = new UserArrayAdapter(requireContext(), new ArrayList<>());
        binding.enrolledListView.setAdapter(enrolledArrayAdapter);
        return root;
    }
}
