package com.example.mini_pekkas.ui.event.organizer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentCreateQrBinding;
import com.example.mini_pekkas.databinding.FragmentEventOrgBinding;
import com.example.mini_pekkas.databinding.FragmentChoosePartBinding;


public class EventOrgFragment extends Fragment {

    private FragmentEventOrgBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventOrgViewModel eventViewModel =
                new ViewModelProvider(this).get(EventOrgViewModel.class);

        binding = FragmentEventOrgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // binds the text views to the view model
        final TextView eventNameView = binding.eventNameView;
        final TextView organizerNameView = binding.organizerNameView;
        final TextView locationView = binding.locationView;
        final TextView eventDescriptionView = binding.eventDescriptionView;
        final TextView eventDetailsView = binding.eventDetailsView;

        // updates text views when they are changed
        eventViewModel.getEventName().observe(getViewLifecycleOwner(), eventNameView::setText);
        eventViewModel.getOrganizerName().observe(getViewLifecycleOwner(), organizerNameView::setText);
        eventViewModel.getLocation().observe(getViewLifecycleOwner(), locationView::setText);
        eventViewModel.getEventDescription().observe(getViewLifecycleOwner(), eventDescriptionView::setText);
        eventViewModel.getEventDetails().observe(getViewLifecycleOwner(), eventDetailsView::setText);

        // binds the images to the view model
        final ImageView eventImage = binding.eventImageView;
        ImageView qrImageView = root.findViewById(R.id.qrImage);
        eventViewModel.getEventImage().observe(getViewLifecycleOwner(), eventImage::setImageResource);

        //place holder for the qr code
        qrImageView.setImageResource(R.drawable.camera);

        ImageButton chooseButton = binding.chooseButton;
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create fragment for choosing participants after clicking the choose button
                FragmentChoosePartBinding choosePartBinding = FragmentChoosePartBinding.inflate(LayoutInflater.from(getContext()));
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(choosePartBinding.getRoot());
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}