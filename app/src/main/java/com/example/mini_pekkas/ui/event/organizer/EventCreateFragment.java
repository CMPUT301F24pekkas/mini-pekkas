package com.example.mini_pekkas.ui.event.organizer;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mini_pekkas.QRCodeGenerator;
import com.example.mini_pekkas.databinding.FragmentCreateEventBinding;
import com.example.mini_pekkas.databinding.FragmentCreateQrBinding;

public class EventCreateFragment extends Fragment {

    private FragmentCreateEventBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // button to show qr confirm dialogue
        Button addButton = binding.addEventButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate the fragment
                FragmentCreateQrBinding qrBinding = FragmentCreateQrBinding.inflate(LayoutInflater.from(getContext()));
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(qrBinding.getRoot());
                AlertDialog dialog = builder.create();

                // generate the qr code
                Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode("https://example.com", 300, 300); // Replace with your desired URL
                if (qrCodeBitmap != null) {
                    qrBinding.qrDialogueImageView.setImageBitmap(qrCodeBitmap); // Set the generated QR code to the ImageView
                }

                dialog.show();
            }

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}