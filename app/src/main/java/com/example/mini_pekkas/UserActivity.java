package com.example.mini_pekkas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mini_pekkas.databinding.UserMainBinding;
import com.example.mini_pekkas.notification.NotificationForegroundService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

/**
 * UserActivity is the main activity for Users, setting up the navigation controller
 * and Navigation Bar for user-specific activities. This activity initializes and configures the navigation components and handles
 * Firebase authentication for the current user. If the user does not exist, it creates
 * a temporary user with default values.
 */
public class UserActivity extends AppCompatActivity {

    private UserMainBinding binding;
    /**
     * Initializes the UserActivity, sets up navigation, and checks if a user exists in Firebase.
     * If the user does not exist, a temporary user with default values is created and initialized.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = UserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.user_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_user_organizer, R.id.navigation_notifications, R.id.navigation_profile)
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

        askNotificationPermission();    // Ask for notification permissions
    }
    /**
     * Handles the "up" navigation action in the action bar.
     *
     * @return true if the navigation action is handled, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.user_main);
        navController.navigate(R.id.action_global_navigation_home);
        return true;
    }

    /**
     * Registers a callback to handle the result of the permission request. If the permission is granted, the notification listener is initialized.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Initialize the notification listener
                    Intent serviceIntent = new Intent(this, NotificationForegroundService.class);
                    ContextCompat.startForegroundService(this, serviceIntent);

                } else {
                    // Inform user that that your app will not show notifications. Make a toast
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Asks for notification permission. If the permission is already granted, the notification listener is initialized.
     */
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Initialize the notification listener
                Intent serviceIntent = new Intent(this, NotificationForegroundService.class);
                ContextCompat.startForegroundService(this, serviceIntent);
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
                //       This should be done if time permits
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}