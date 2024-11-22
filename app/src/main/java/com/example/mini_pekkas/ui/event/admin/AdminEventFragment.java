package com.example.mini_pekkas.ui.event.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentEventBinding;
import com.example.mini_pekkas.ui.event.user.EventViewModel;

/**
 * Fragment that displays details of an event in the Admin interface.
 * This fragment binds data from the EventViewModel to the UI elements.
 * It also handles displaying a QR code.
 * TODO need to be completed
 */
public class AdminEventFragment extends Fragment {
    private FragmentEventBinding binding;
    private EventViewModel eventViewModel;
    private Firebase firebaseHelper;

    /**
     * Called when the fragment's view is created.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's view.
     * @param container          The parent view group that this fragment's view will be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state, if any.
     * @return The root view of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        firebaseHelper = new Firebase(requireContext());


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
        eventViewModel.getFacility().observe(getViewLifecycleOwner(), locationView::setText);
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

    /**
     * Cleans up resources and prevents memory leaks by nullifying the binding.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
