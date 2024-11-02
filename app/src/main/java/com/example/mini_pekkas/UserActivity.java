package com.example.mini_pekkas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mini_pekkas.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_event, R.id.navigation_camera, R.id.navigation_notifications, R.id.navigation_profile)
                .build();

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // Replacement for fragment -> FragmentContainerView
        NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        assert navFragment != null;
        NavController navController = navFragment.getNavController();


        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        /*
        What follows below are firebase functions I have to test in main because it needs special permissions
        Clean and remove this calls when you are done testing. They should be called in their respective UI fragment
        You can also let Android studio compress these into lambda functions for readability. I left them in here for clarity
        TODO Feel free to replace with your own functions
         */

        // Initialize Firebase class
        Firebase firebaseHelper = new Firebase(this);

        firebaseHelper.checkThisUserExist(exist -> {
            if (!exist) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", "temp");
                map.put("email", "realemail@@tttt");
                map.put("phone", "7801116666");
                map.put("facility", "Ymca");
                map.put("isOrganizer", true);
                // map.put("isAdmin", isAdmin);
                map.put("profilePhoto", "rer");

                User tempUser = new User(map);
                firebaseHelper.InitializeThisUser(tempUser, () -> {
                    // User successfully initialized. Perform any necessary actions here.
                    User epicUser = firebaseHelper.getThisUser();

                });
            } else {
                // User already exist
                User epicUser = firebaseHelper.getThisUser();

            }
        });
    }

}