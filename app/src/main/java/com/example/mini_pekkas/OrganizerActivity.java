package com.example.mini_pekkas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mini_pekkas.databinding.OrganizerMainBinding;
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
     * Navigates to the home screen for the organizer.
     *
     * @return true to indicate that the navigation is handled.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.organizer_main);
        navController.navigate(R.id.action_global_navigation_org_home);
        return true;
    }
}
