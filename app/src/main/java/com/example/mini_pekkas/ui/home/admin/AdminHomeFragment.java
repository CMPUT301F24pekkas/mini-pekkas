package com.example.mini_pekkas.ui.home.admin;

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
import com.example.mini_pekkas.ui.home.HomeViewModel;

/**
 * Fragment that serves as the home screen for regular users, displaying basic event details.
 */
public class AdminHomeFragment extends Fragment {

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
        com.example.mini_pekkas.ui.home.admin.AdminHomeViewModel homeViewModel = new ViewModelProvider(this).get(AdminHomeViewModel.class);

        // Inflate the layout using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI components from the binding
        final TextView eventNameView = binding.homeUserEventView2;
//        final TextView eventDescriptionView = binding.homeUserEventDescriptionView2;
//        final TextView eventDayCountView = binding.dayUserCountView2;
//        final Button eventViewButton = binding.homeUserEventViewButton2;

        // Set text views with data from the ViewModel
        homeViewModel.getTest().observe(getViewLifecycleOwner(), eventNameView::setText);

        // TODO set any important x views

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
