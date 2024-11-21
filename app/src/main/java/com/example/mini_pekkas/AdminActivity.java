package com.example.mini_pekkas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mini_pekkas.databinding.AdminMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * AdminActivity is the main activity for organizers, setting up the navigation controller
 * and special navigation bar for admin-specific views only
 */
public class AdminActivity extends AppCompatActivity {
    private AdminMainBinding binding;
    @Override
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState The state of the activity saved during a previous configuration change.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.admin_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.adminBrowseEvents,
                R.id.adminBrowseFacilities,
                R.id.adminBrowseImages,
                R.id.adminBrowseProfiles
        ).build();

        // Get the NavController
        NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.admin_main);
        assert navFragment != null;
        NavController navController = navFragment.getNavController();

        // Set up the action bar and bottom navigation
        NavigationUI.setupActionBarWithNavController(AdminActivity.this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.adminView, navController);
    }
}
