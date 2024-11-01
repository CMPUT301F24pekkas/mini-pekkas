package com.example.mini_pekkas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mini_pekkas.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
         */

        // Initialize Firebase class
        Firebase firebaseHelper = new Firebase(this);

        // Finds the user document for the given device. Makes a new one if it doesn't exist
        // Creates a new user Class. See implementation in User.java
        firebaseHelper.getUser(new Firebase.OnDocumentRetrievedListener() {
            @Override
            public void onDocumentRetrieved(DocumentSnapshot documentSnapshot) {
                User thisUser = new User(documentSnapshot.getData());
            }

            // All my default error handler just prints the error. Not necessary to implement
            @Override
            public void onError(Exception e) {
                Firebase.OnDocumentRetrievedListener.super.onError(e);
            }
        });

        // Finds a list of events in users-in-events collection
        // Replace eventList with your own event list
        firebaseHelper.getWaitlist(new Firebase.OnDocumentListRetrievedListener() {
            @Override
            public void onDocumentsRetrieved(List<DocumentSnapshot> documentSnapshots) {
                ArrayList<Event> eventList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    Event event = new Event(documentSnapshot.getData());
                    eventList.add(event);
                };
            }
        });

        // Checks if the current user is an admin from the admins collection
        firebaseHelper.isAdmin(new Firebase.AdminCheckListener() {
            @Override
            public void onAdminCheckComplete(boolean isAdmin) {
                if (isAdmin) {
                    // User is an admin. Render admin UI
                } else {
                    // User is not an admin. Render user UI
                }
            }
        });
    }

}