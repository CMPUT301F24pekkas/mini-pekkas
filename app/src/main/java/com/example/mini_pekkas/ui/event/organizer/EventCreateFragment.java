package com.example.mini_pekkas.ui.event.organizer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.mini_pekkas.databinding.FragmentCreateEventBinding;
import com.example.mini_pekkas.databinding.FragmentCreateQrBinding;


// TODO:
//  - Save the event onto the firebase!!!!
//  - Create a QR code and replace the image with the qr code
//  figure out how to save the hash code for qr code
//  - I didn't put any prompts for restrictions yet,
//  like if the user inputted something incorrectly
//  - Pull up the start and end dates by using the built-in Android Studio
//  - Same thing with the time, I think it's like some kind of Dialogue Fragment
//  - I put in code so that when you press add, the QR code dialogue shows up
//  I didn't put anything for the cancel button though
//  - When you confirm on the QR dialogue it should go to the EventOrgFragment view
//  - We can figure out geo location later
// xmls used in this class:
// fragment_create_event.xml, fragment_create_qr.xml

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
                // create fragment for qr dialogue and inflate after pressing on the add button
                FragmentCreateQrBinding qrBinding = FragmentCreateQrBinding.inflate(LayoutInflater.from(getContext()));
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(qrBinding.getRoot());
                AlertDialog dialog = builder.create();
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