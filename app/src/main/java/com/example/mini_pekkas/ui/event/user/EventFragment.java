package com.example.mini_pekkas.ui.event.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentEventBinding;

public class EventFragment extends Fragment {

    private FragmentEventBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventViewModel eventViewModel =
                new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
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