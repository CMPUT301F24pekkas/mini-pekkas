package com.example.mini_pekkas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.mini_pekkas.databinding.OrganizerMainBinding;
import com.example.mini_pekkas.notification.NotificationWorker;
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

        askNotificationPermission();    // Ask for notification permissions
    }


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

    /**
     * Registers a callback to handle the result of the permission request. If the permission is granted, the notification listener is initialized.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    // Initialize the notification listener
                    OneTimeWorkRequest notificationWorkRequest =
                            new OneTimeWorkRequest.Builder(NotificationWorker.class)
                                    .build();
                    WorkManager.getInstance(this).enqueue(notificationWorkRequest);
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
                // FCM SDK (and your app) can post notifications.
                // Initialize the notification listener
                OneTimeWorkRequest notificationWorkRequest =
                        new OneTimeWorkRequest.Builder(NotificationWorker.class)
                                .build();
                WorkManager.getInstance(this).enqueue(notificationWorkRequest);
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
