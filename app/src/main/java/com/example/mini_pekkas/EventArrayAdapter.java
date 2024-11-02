package com.example.mini_pekkas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

// Not complete yet

public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;
    EventArrayAdapter(Context context, ArrayList<Event> events){

        super(context, 0 , events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_event, parent, false);
        }

        // Initialize views from layout
        TextView eventNameView = convertView.findViewById(R.id.eventNameView);
        ImageView eventImageView = convertView.findViewById(R.id.eventImageView);
        ImageView qrImage = convertView.findViewById(R.id.qrImage);
        TextView organizerNameView = convertView.findViewById(R.id.organizerNameView);
        TextView locationView = convertView.findViewById(R.id.locationView);
        TextView eventDescriptionView = convertView.findViewById(R.id.eventDescriptionView);
        TextView eventDetailsView = convertView.findViewById(R.id.eventDetailsView);
        TextView startDateView = convertView.findViewById(R.id.startDateView);
        TextView currentWaitView = convertView.findViewById(R.id.currentWaitView);
        TextView priceView = convertView.findViewById(R.id.priceView);

        // Populate with event data
        eventNameView.setText(event.getName());
        organizerNameView.setText("Organizer: " + event.getEventHost().getName());
        locationView.setText("This is the location");
        eventDescriptionView.setText(event.getDescription());
        startDateView.setText(event.getStartDate());

        currentWaitView.setText(String.valueOf(event.getWaitlist().size()));

        priceView.setText(String.format(Locale.getDefault(), "$%d", event.getPrice()));

        return convertView;


    }
    public void addEvent(Event event){
        events.add(event);
        notifyDataSetChanged();
    }
    public void deleteEvent(int position){
        events.remove(position);
        notifyDataSetChanged();
    }

}
