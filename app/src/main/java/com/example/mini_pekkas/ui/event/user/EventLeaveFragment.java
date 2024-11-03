package com.example.mini_pekkas.ui.event.user;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.R;

import com.example.mini_pekkas.databinding.FragmentEventLeaveBinding;

public class EventLeaveFragment extends Fragment {

    private FragmentEventLeaveBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventLeaveViewModel eventLeaveViewModel =
                new ViewModelProvider(this).get(EventLeaveViewModel.class);

        binding = FragmentEventLeaveBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // binds the text views to the view model
        final TextView eventNameView = binding.eventNameView;
        final TextView organizerNameView = binding.organizerNameView;
        final TextView locationView = binding.locationView;
        final TextView eventDescriptionView = binding.eventDescriptionView;
        final TextView eventDetailsView = binding.eventDetailsView;
        final Button leaveWaitButton = binding.leaveButton;

        // updates text views when they are changed
        eventLeaveViewModel.getEventName().observe(getViewLifecycleOwner(), eventNameView::setText);
        eventLeaveViewModel.getOrganizerName().observe(getViewLifecycleOwner(), organizerNameView::setText);
        eventLeaveViewModel.getLocation().observe(getViewLifecycleOwner(), locationView::setText);
        eventLeaveViewModel.getEventDescription().observe(getViewLifecycleOwner(), eventDescriptionView::setText);
        eventLeaveViewModel.getEventDetails().observe(getViewLifecycleOwner(), eventDetailsView::setText);

        // binds the images to the view model
        final ImageView eventImage = binding.eventImageView;
        ImageView qrImageView = root.findViewById(R.id.qrImage);
        eventLeaveViewModel.getEventImage().observe(getViewLifecycleOwner(), eventImage::setImageResource);


        // placeholders for what the qr code will be
        qrImageView.setImageResource(R.drawable.camera);

        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}