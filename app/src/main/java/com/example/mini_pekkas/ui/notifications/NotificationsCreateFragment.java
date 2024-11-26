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

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.Notifications;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentCreateNotificationBinding;
import com.example.mini_pekkas.ui.event.user.EventFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display and manage user notifications.
 * The notifications can be hidden if the user opts out.
 */
public class NotificationsCreateFragment extends Fragment {

    private FragmentCreateNotificationBinding binding;
    private ListView listView;
    private List<Notifications> notificationList;
    private static final String PREFS_NAME = "NotificationPreferences";
    private static final String KEY_OPT_OUT = "optOut";

    /**
     * Called to create the view for this fragment.
     * Initializes the list of notifications and handles user opt-out logic.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A bundle that provides data saved from a previous instance.
     * @return The root view for the fragment's layout.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentCreateNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
}


