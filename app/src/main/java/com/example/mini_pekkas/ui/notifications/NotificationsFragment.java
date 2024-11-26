package com.example.mini_pekkas.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.Notifications;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentNotificationsBinding;
import com.example.mini_pekkas.ui.event.user.EventFragment;

import java.util.ArrayList;
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

        // Check if the user has opted out, if so, hide the notifications
        if (isUserOptedOut()) {
            listView.setVisibility(View.INVISIBLE);
        }

        // Dummy data for testing
        ArrayList<String> notificationList = new ArrayList<>();
        notificationList.add("Header (Category) (Date)\nSupporting line text lorem ipsum dolor sit amet.");
        notificationList.add("Header (Category) (Date)\nSupporting line text lorem ipsum dolor sit amet.");
        notificationList.add("Header (Category) (Date)\nSupporting line text lorem ipsum dolor sit amet.");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                notificationList
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            navigateToFragment(position);
        });

        // Observe new notifications
        notificationsViewModel.getNotification().observe(getViewLifecycleOwner(), text -> {
            notificationList.add(text);
            adapter.notifyDataSetChanged();
        });

        // Handle opt-out button click
        optOutButton.setOnClickListener(v -> showOptOutDialog());

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
            saveOptOutPreference(true);  // Save opt-out preference
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
