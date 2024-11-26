package com.example.mini_pekkas.ui.admin;

import static com.example.mini_pekkas.QRCodeGenerator.generateQRCode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.QrCodeAdapter;
import com.example.mini_pekkas.databinding.FragmentAdminBrowseImagesBinding;

import java.util.ArrayList;

/**
 * Fragment class for browsing and managing images in the Admin interface.
 * Retrieves and displays images stored in Firebase in a grid layout using a RecyclerView.
 */
public class AdminBrowseQrCodes extends Fragment {

    private FragmentAdminBrowseImagesBinding binding; // Declare binding variable
    private Firebase firebaseHelper;
    private RecyclerView recyclerView;
    private QrCodeAdapter qrCodeAdapter;
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
        firebaseHelper.getAllEvents(events ->  {
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            ArrayList<Bitmap> images = new ArrayList<>();
            // For every base64 QR code, convert it to a URI and add it to the list
            for (Event event : events) {
                String base64String = event.getQrCode();

                // Generated QR code defined in QRCodeGenerator
                Bitmap bit = generateQRCode(base64String, 300, 300);
                images.add(bit);
            }

            // Make image adapter
            qrCodeAdapter = new QrCodeAdapter(requireContext(), images, events);

            recyclerView.setAdapter(qrCodeAdapter);
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