package com.example.mini_pekkas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mini_pekkas.databinding.OrganizerMainBinding;
import com.example.mini_pekkas.ui.event.organizer.EventChooseUsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
/**
 * OrganizerActivity is the main activity for organizers, setting up the navigation controller
 * and Navigation Bar for organizer-specific views.
 */
public class OrganizerActivity extends AppCompatActivity {
    private OrganizerMainBinding binding;

    /**
     * Called when the activity is first created.
     * Sets up the bottom navigation view, app bar configuration, and navigation controller.
     *
     * @param savedInstanceState The state of the activity saved during a previous configuration change.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OrganizerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.organizer_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_org_add, R.id.navigation_org_camera, R.id.navigation_org_home,
                R.id.navigation_org_profile, R.id.navigation_org_notifications)
                .build();

        // Get the NavController
        NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.organizer_main);
        assert navFragment != null;
        NavController navController = navFragment.getNavController();

        // Set up the action bar and bottom navigation
        NavigationUI.setupActionBarWithNavController(OrganizerActivity.this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.organizerView, navController);
    }

    /**
     * Handles the "Up" navigation in the action bar.
     * @return True if the "Up" navigation was handled, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.organizer_main);
        // Check if the current destination is a EventChooseUsersFragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.organizer_main);
        if (currentFragment instanceof EventChooseUsersFragment) {
            // Handle the "Up" navigation differently for this fragment
            navController.popBackStack(R.id.navigation_org_event_details, false); // Go back to eventDetails
            return true;
        }
        return navController.navigateUp();
    }
}
