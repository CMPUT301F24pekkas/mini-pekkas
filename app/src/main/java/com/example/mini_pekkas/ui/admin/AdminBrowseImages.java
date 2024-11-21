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
import com.example.mini_pekkas.databinding.FragmentAdminBrowseImagesBinding;

public class AdminBrowseImages extends Fragment {

    private FragmentAdminBrowseImagesBinding binding; // Declare binding variable
    private Firebase firebaseHelper;
    private RecyclerView recycleView;
//    private ArrayAdapter<Uri> adapter;
//    private ArrayList<Uri> imageList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAdminBrowseImagesBinding.inflate(inflater, container, false);
        firebaseHelper = new Firebase(requireContext());

        recycleView = binding.imageGridRecyclerView;

        // From the list of images, set the view to display all images
        firebaseHelper.getAllImages(images -> {
            // Set up the recycleView
            recycleView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

            // Make image adapter TODO make custom image adapter and list xml
//            ArrayAdapter<Uri> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, images);
//            recycleView.setAdapter(adapter);
        });


        return binding.getRoot(); // Return the root view from binding
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by nullifying the binding when view is destroyed
    }
}