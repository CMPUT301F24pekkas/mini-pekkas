package com.example.mini_pekkas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mini_pekkas.databinding.UserMainBinding;
import com.example.mini_pekkas.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    private UserMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = UserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.user_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_event, R.id.navigation_camera, R.id.navigation_notifications, R.id.navigation_profile)
                .build();

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // Replacement for fragment -> FragmentContainerView
        NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.user_main);
        assert navFragment != null;
        NavController navController = navFragment.getNavController();


        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.userView, navController);


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