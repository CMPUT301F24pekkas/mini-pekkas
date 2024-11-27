package com.example.mini_pekkas.ui.event.user;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mini_pekkas.AdminActivity;
import com.example.mini_pekkas.MainActivity;
import com.example.mini_pekkas.OrganizerActivity;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.UserActivity;
import com.example.mini_pekkas.databinding.FragmentUserOrganizerBinding;
import com.example.mini_pekkas.ui.home.HomeFragment;
import com.example.mini_pekkas.ui.home.HomeViewModel;

public class UserOrganizerFragment extends Fragment {
    private FragmentUserOrganizerBinding binding;

    /**
     * Initializes the fragment's user interface.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI will attach to.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentUserOrganizerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Handle button click to switch to OrganizerActivity
        final Button confirmFacilityButton = binding.confirmNewFacilityButton;
        confirmFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use requireActivity() to get the parent activity context
                Intent adminIntent = new Intent(requireActivity(), OrganizerActivity.class);
                startActivity(adminIntent);
            }
        });

        return root;
    }
    /**
     * Cleans up resources and references to avoid memory leaks when the fragment's view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
