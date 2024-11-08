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
import com.example.mini_pekkas.databinding.FragmentCameraBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private static final int CAMERA_REQUEST_CODE = 100;
    private Firebase firebaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseHelper = new Firebase(requireContext());

        binding.cameraButton.setOnClickListener(v -> openCamera());

        return root;
    }

    // Open the QR code scanner
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startQRCodeScanner();
        }
    }

    // start QR code scanning
    private void startQRCodeScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setPrompt("Scan a QR Code");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedData = result.getContents();
                Log.d("CameraFragment", "QR Code Scanned: " + scannedData);

                // Convert scanned data to Base64
                String base64QRCode = Base64.encodeToString(scannedData.getBytes(), Base64.NO_WRAP);
                Log.d("CameraFragment", "Base64 Encoded QR Code: " + base64QRCode);

                // fetch event details from Firebase using the Base64 string
                fetchEventFromFirebase(base64QRCode);
            } else {
                Toast.makeText(getContext(), "No QR Code detected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchEventFromFirebase(String base64QRCode) {
        firebaseHelper.getEventByQRCode(base64QRCode, new Firebase.EventRetrievalListener() {
            @Override
            public void onEventRetrievalCompleted(Event event) {
                if (event != null) {
                    Log.d("CameraFragment", "Event found: " + event.getName());
                    // Display event details or navigate to event details page
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
