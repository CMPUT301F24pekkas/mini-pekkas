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

/**
 * Fragment representing the organizer's view for a specific event.
 * Displays event details, participant statistics, and includes a QR code placeholder.
 */
public class EventOrgFragment2 extends Fragment {

    private FragmentEventOrg2Binding binding;

    /**
     * Initializes the fragment's view, setting up bindings to UI elements and ViewModel observers
     * to display event details and participant statistics dynamically.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment
     * @param container          The parent view to which the fragment's UI is attached
     * @param savedInstanceState Previously saved state information for the fragment
     * @return The root view of the fragment's layout
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventOrgViewModel2 eventViewModel =
                new ViewModelProvider(this).get(EventOrgViewModel2.class);

        binding = FragmentEventOrg2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Binds the text views to the view model
        final TextView eventNameView = binding.eventNameView;
        final TextView organizerNameView = binding.organizerNameView;
        final TextView locationView = binding.locationView;
        final TextView eventDescriptionView = binding.eventDescriptionView;
        final TextView eventDetailsView = binding.eventDetailsView;
        final TextView chosenPartView = binding.chosenPartView;
        final TextView canceledPartView = binding.canceledAmountView;
        final TextView enrolledPartView = binding.enrolledAmountView;

        // Observes LiveData in the view model to update text views with event and participant details
        eventViewModel.getEventName().observe(getViewLifecycleOwner(), eventNameView::setText);
        eventViewModel.getOrganizerName().observe(getViewLifecycleOwner(), organizerNameView::setText);
        eventViewModel.getLocation().observe(getViewLifecycleOwner(), locationView::setText);
        eventViewModel.getEventDescription().observe(getViewLifecycleOwner(), eventDescriptionView::setText);
        eventViewModel.getEventDetails().observe(getViewLifecycleOwner(), eventDetailsView::setText);
        eventViewModel.getChosenPart().observe(getViewLifecycleOwner(), chosenPartView::setText);
        eventViewModel.getCanceledPart().observe(getViewLifecycleOwner(), canceledPartView::setText);
        eventViewModel.getEnrolledPart().observe(getViewLifecycleOwner(), enrolledPartView::setText);

        // Binds the event image to the view model
        final ImageView eventImage = binding.eventImageView;
        ImageView qrImageView = root.findViewById(R.id.qrImage);
        eventViewModel.getEventImage().observe(getViewLifecycleOwner(), eventImage::setImageResource);

        // Placeholder for the QR code image
        qrImageView.setImageResource(R.drawable.camera);

        // Sets up the choose button to open a participant selection dialog

        return root;
    }

    /**
     * Called when the fragment's view is destroyed. Cleans up the binding to prevent memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}