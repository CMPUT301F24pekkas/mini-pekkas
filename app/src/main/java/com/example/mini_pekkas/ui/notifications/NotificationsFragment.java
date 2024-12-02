package com.example.mini_pekkas.ui.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentNotificationOffBinding;
import com.example.mini_pekkas.databinding.FragmentNotificationsBinding;
import com.example.mini_pekkas.notification.Notifications;
import com.example.mini_pekkas.notification.NotificationsArrayAdapter;
import com.example.mini_pekkas.ui.event.user.EventFragment;

import java.util.List;

/**
 * Fragment to display and manage user notifications.
 * The notifications can be hidden if the user opts out.
 */
public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ListView listView;
    private List<Notifications> notificationList;
    private static final String PREFS_NAME = "NotificationPreferences";
    private static final String KEY_OPT_OUT = "optOut";

    /**
     * Called to create the view for this fragment.
     * Initializes the list of notifications and handles user opt-out logic.
     *
     * @param inflater The LayoutInflater object to inflate views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A bundle that provides data saved from a previous instance.
     * @return The root view for the fragment's layout.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.textNotifications;
        ImageButton optOutButton = binding.optOut;
        ImageButton optInButton = binding.optIn;
        optInButton.setVisibility(View.INVISIBLE);
        // Check if the user has opted out, if so, hide the notifications
        if (isUserOptedOut()) {
            listView.setVisibility(View.INVISIBLE);
            optInButton.setVisibility(View.VISIBLE);
        }

        // Fetch the user's notifications from firebase
        Firebase firebaseHelper = new Firebase(getContext());
        firebaseHelper.getThisUserNotifications(notifications -> {
            // Copy the notifications list to a local variable
            notificationList = notifications;

            // Set the adapter for the ListView
            NotificationsArrayAdapter adapter = new NotificationsArrayAdapter(getContext(), R.layout.list_notification, notificationList);
            listView.setAdapter(adapter);

            // Observe new notifications
            notificationsViewModel.getNotification().observe(getViewLifecycleOwner(), text -> {
                //notificationList.add(text);
                adapter.notifyDataSetChanged();
            });
        });

        // Handle opt-out button click
        optOutButton.setOnClickListener(v -> showOptOutDialog());
        optInButton.setOnClickListener(v -> {
            FragmentNotificationOffBinding notificationOffBinding = FragmentNotificationOffBinding.inflate(LayoutInflater.from(getContext()));
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(notificationOffBinding.getRoot());
            AlertDialog dialog = builder.create();
            notificationOffBinding.textView5.setText("Opt in notifications?");
            notificationOffBinding.textView11.setText("Would you like to opt back in to notifications from organizers and admins?");
            notificationOffBinding.optOutNotifications.setText("Opt In");
            dialog.show();

            Button optInConfirmButton = notificationOffBinding.optOutNotifications;
            Button cancelButton = notificationOffBinding.cancelOptOutNotifications;

            optInConfirmButton.setOnClickListener(view -> {
                listView.setVisibility(View.VISIBLE);
                optOutButton.setVisibility(View.VISIBLE);
                saveOptOutPreference(false);

                // Remove notification permission on device
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API level 26 and above
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, "com.example.mini_pekkas");
                    startActivity(intent);
                } else { // API level below 26
                    // For older versions, you might need to open the app's settings page
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", "com.example.mini_pekkas", null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                dialog.dismiss();
            });
            cancelButton.setOnClickListener(view -> dialog.dismiss());
        });

        return root;
    }

    /**
     * Called when the view for the fragment is destroyed.
     * Cleans up the binding object to avoid memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Displays the opt-out dialog allowing the user to opt-out of notifications.
     */
    private void showOptOutDialog() {
        // Inflate and create the opt-out dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_notification_off, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        Button optOutButton = dialogView.findViewById(R.id.optOutNotifications);
        Button cancelButton = dialogView.findViewById(R.id.cancelOptOutNotifications);

        // Opt-out button listener to hide ListView and save preference
        optOutButton.setOnClickListener(v -> {
            listView.setVisibility(View.INVISIBLE);
            optOutButton.setVisibility(View.INVISIBLE);
            saveOptOutPreference(true);

            // Remove notification permission on device
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API level 26 and above
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, "com.example.mini_pekkas");
                startActivity(intent);
            } else { // API level below 26
                // For older versions, you might need to open the app's settings page
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", "com.example.mini_pekkas", null);
                intent.setData(uri);
                startActivity(intent);
            }


            dialog.dismiss();
        });

        // Cancel button listener to dismiss dialog
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Saves the user's opt-out preference to SharedPreferences.
     *
     * @param optOut A boolean indicating if the user has opted out of notifications.
     */
    private void saveOptOutPreference(boolean optOut) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_OPT_OUT, optOut);
        editor.apply();
    }

    /**
     * Checks if the user has opted out of receiving notifications.
     *
     * @return true if the user has opted out, false otherwise.
     */
    private boolean isUserOptedOut() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_OPT_OUT, false);
    }

    /**
     * Navigates to a different fragment based on the selected notification.
     * TODO add in all other fragments we may need
     * TODO with fragments like EventFragment, we may need to fetch more data like the event itself
     * @param position The position of the selected notification in the ListView.
     */
    private void navigateToFragment(int position) {
        String fragmentDestination = notificationList.get(position).getFragmentDestination();

        if (fragmentDestination != null) {
            FragmentManager fragmentManager = getParentFragmentManager(); // Get FragmentManager from Activity
            Fragment fragment = null;
            switch (fragmentDestination) {
                case "EventFragment":
                    fragment = new EventFragment();
                    // Pass in any other data
                    break;
                // ... other cases ...
            }

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.text_notifications, fragment) // TODO the R.id may be wrong - ryan
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}
