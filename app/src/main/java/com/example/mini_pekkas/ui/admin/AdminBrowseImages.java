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

public class AdminBrowseImages extends Fragment {

    private FragmentAdminBrowseImagesBinding binding; // Declare binding variable
    private Firebase firebaseHelper;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
//    private ArrayList<Uri> imageList;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by nullifying the binding when view is destroyed
    }
}