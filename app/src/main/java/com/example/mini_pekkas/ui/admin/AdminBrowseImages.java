package com.example.mini_pekkas.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.ImageAdapter;
import com.example.mini_pekkas.databinding.FragmentAdminBrowseImagesBinding;

/**
 * Fragment class for browsing and managing images in the Admin interface.
 * Retrieves and displays images stored in Firebase in a grid layout using a RecyclerView.
 */
public class AdminBrowseImages extends Fragment {

    private FragmentAdminBrowseImagesBinding binding; // Declare binding variable
    private Firebase firebaseHelper;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
//    private ArrayList<Uri> imageList;

    /**
     * Called to initialize the fragment's view hierarchy.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's view.
     * @param container          The parent view group that this fragment's view will be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAdminBrowseImagesBinding.inflate(inflater, container, false);
        firebaseHelper = new Firebase(requireContext());

        recyclerView = binding.imageGridRecyclerView;

        // From the list of images, set the view to display all images
        firebaseHelper.getAllImages(images -> {
            // Set up the recycleView
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

            // Make image adapter
            imageAdapter = new ImageAdapter(requireContext(), images);

            // Set the adapter after images are retrieved
            recyclerView.setAdapter(imageAdapter);
        });


        return binding.getRoot(); // Return the root view from binding
    }

    /**
     * Cleans up resources and prevents memory leaks by nullifying the binding.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by nullifying the binding when view is destroyed
    }
}