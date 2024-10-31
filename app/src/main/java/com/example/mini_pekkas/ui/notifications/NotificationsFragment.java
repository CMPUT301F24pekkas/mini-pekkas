package com.example.mini_pekkas.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mini_pekkas.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listView = binding.textNotifications;

        // dummy data for testing
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


        notificationsViewModel.getNotification().observe(getViewLifecycleOwner(), text -> {
            notificationList.add(text);
            adapter.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
