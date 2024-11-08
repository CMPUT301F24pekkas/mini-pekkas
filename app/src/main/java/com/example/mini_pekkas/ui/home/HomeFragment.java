package com.example.mini_pekkas.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentHomeBinding;

/**
 * Fragment that serves as the home screen for regular users, displaying basic event details.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    /**
     * Initializes the fragment's user interface.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI will attach to.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root view of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel for home screen
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Inflate the layout using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI components from the binding
        final TextView eventNameView = binding.homeUserEventView2;
        final TextView eventDescriptionView = binding.homeUserEventDescriptionView2;
        final TextView eventDayCountView = binding.dayUserCountView2;
        final Button eventViewButton = binding.homeUserEventViewButton2;

        // Set text views with data from the ViewModel
        homeViewModel.getEventName().observe(getViewLifecycleOwner(), eventNameView::setText);
        homeViewModel.getEventDescription().observe(getViewLifecycleOwner(), eventDescriptionView::setText);
        homeViewModel.getEventDayCount().observe(getViewLifecycleOwner(), eventDayCountView::setText);

        // Set an OnClickListener to navigate to the event view when the button is clicked
        eventViewButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Navigates to the event detail screen when the button is clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);
                navController.navigate(R.id.action_navigation_home_to_navigation_event);
            }
        });

        return root;
    }

    /**
     * Cleans up resources and references to avoid memory leaks when the fragment's view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
