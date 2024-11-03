package com.example.mini_pekkas.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // sets texts views
        final TextView eventNameView = binding.homeUserEventView2;
        final TextView eventDescriptionView = binding.homeUserEventDescriptionView2;
        final TextView eventDayCountView = binding.dayUserCountView2;
        final Button eventViewButton = binding.homeUserEventViewButton2;

        // set text views according to the view model
        homeViewModel.getEventName().observe(getViewLifecycleOwner(), eventNameView::setText);
        homeViewModel.getEventDescription().observe(getViewLifecycleOwner(), eventDescriptionView::setText);
        homeViewModel.getEventDayCount().observe(getViewLifecycleOwner(), eventDayCountView::setText);

        // when user clicks on view button, switch to event view
        eventViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);
                navController.navigate(R.id.action_navigation_home_to_eventLeaveFragment);
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