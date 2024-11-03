package com.example.mini_pekkas.ui.event.organizer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentChoosePartBinding;
import com.example.mini_pekkas.databinding.FragmentEventOrg2Binding;

public class EventOrgFragment2 extends Fragment {

    private FragmentEventOrg2Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventOrgViewModel2 eventViewModel =
                new ViewModelProvider(this).get(EventOrgViewModel2.class);

        binding = FragmentEventOrg2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // binds the text views to the view model
        final TextView eventNameView = binding.eventNameView;
        final TextView organizerNameView = binding.organizerNameView;
        final TextView locationView = binding.locationView;
        final TextView eventDescriptionView = binding.eventDescriptionView;
        final TextView eventDetailsView = binding.eventDetailsView;
        final TextView chosenPartView = binding.chosenPartView;
        final TextView canceledPartView = binding.canceledAmountView;
        final TextView enrolledPartView = binding.enrolledAmountView;

        // updates text views when they are changed
        eventViewModel.getEventName().observe(getViewLifecycleOwner(), eventNameView::setText);
        eventViewModel.getOrganizerName().observe(getViewLifecycleOwner(), organizerNameView::setText);
        eventViewModel.getLocation().observe(getViewLifecycleOwner(), locationView::setText);
        eventViewModel.getEventDescription().observe(getViewLifecycleOwner(), eventDescriptionView::setText);
        eventViewModel.getEventDetails().observe(getViewLifecycleOwner(), eventDetailsView::setText);
        eventViewModel.getChosenPart().observe(getViewLifecycleOwner(), chosenPartView::setText);
        eventViewModel.getCanceledPart().observe(getViewLifecycleOwner(), canceledPartView::setText);
        eventViewModel.getEnrolledPart().observe(getViewLifecycleOwner(), enrolledPartView::setText);

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