package com.example.mini_pekkas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mini_pekkas.databinding.OrganizerMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class OrganizerActivity extends AppCompatActivity {
    private OrganizerMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OrganizerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.organizer_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_org_add, R.id.navigation_org_event, R.id.navigation_org_home,
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
}
