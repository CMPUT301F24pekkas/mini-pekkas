package com.example.mini_pekkas.ui.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.Event;
import com.example.mini_pekkas.Firebase;
import com.example.mini_pekkas.R;
import com.example.mini_pekkas.databinding.FragmentCameraBinding;
import com.example.mini_pekkas.ui.event.user.SharedEventViewModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.os.Bundle;



/**
 * Fragment to handle the QR code scanning functionality.
 * The user can scan a QR code using the device camera, and based on the scanned code,
 * the event details are fetched from Firebase.
 */
public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private static final int CAMERA_REQUEST_CODE = 100;
    private Firebase firebaseHelper;

    /**
     * Called when the fragment's view is created. Initializes the Firebase helper and sets up
     * the button click listener to open the camera for scanning QR codes.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The container for the fragment's UI.
     * @param savedInstanceState The saved instance state bundle.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseHelper = new Firebase(requireContext());

        // Button listener to open the camera when clicked
        binding.cameraButton.setOnClickListener(v -> openCamera());

        return root;
    }

    /**
     * Opens the camera to scan a QR code. If the app doesn't have permission to use the camera,
     * it will request permission from the user.
     */
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startQRCodeScanner();
        }
    }

    /**
     * Starts the QR code scanner using the ZXing library.
     */
    private void startQRCodeScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setPrompt("Scan a QR Code");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    /**
     * Handles the result from the QR code scanner. If a valid QR code is scanned,
     * it converts the scanned data to Base64 and fetches the event details from Firebase.
     *
     * @param requestCode The request code passed to {@link android.app.Activity#onActivityResult(int, int, Intent)}.
     * @param resultCode  The result code returned by the child activity.
     * @param data        An Intent containing the result data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedData = result.getContents();
                Log.d("CameraFragment", "Raw QR Code Data: " + scannedData);

                SharedEventViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedEventViewModel.class);
                sharedViewModel.setQrCodeData(scannedData);

                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.action_navigation_camera_to_navigation_event);

            } else {
                Toast.makeText(getContext(), "No QR Code detected", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Fetches the event associated with the provided Base64-encoded QR code from Firebase.
     *
     * @param base64QRCode The Base64-encoded QR code string.
     */
    private void fetchEventFromFirebase(String qrCodeData) {
        Log.d("CameraFragment", "Fetching event with raw QR data: " + qrCodeData);
        firebaseHelper.getEventByQRCode(qrCodeData, new Firebase.EventRetrievalListener() {
            @Override
            public void onEventRetrievalCompleted(Event event) {
                if (event != null) {
                    Log.d("CameraFragment", "Event found: " + event.getName());
                    Toast.makeText(getContext(), "Event Found: " + event.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("CameraFragment", "No event found for this QR code");
                    Toast.makeText(getContext(), "No event found for this QR code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("CameraFragment", "Error retrieving event: " + e.getMessage());
            }
        });
    }


    /**
     * Handles the result of the camera permission request. If granted, it opens the camera,
     * otherwise it shows a message to the user explaining that the permission is required.
     *
     * @param requestCode  The request code passed to {@link android.app.Activity#onRequestPermissionsResult(int, String[], int[])}.
     * @param permissions  The permissions that were requested.
     * @param grantResults The results for the permissions requested.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Cleans up resources when the fragment's view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
