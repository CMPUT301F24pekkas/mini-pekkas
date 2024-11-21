package com.example.mini_pekkas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// Not complete yet

/**
 * EventArrayAdapter is a custom ArrayAdapter for displaying a list of Event objects in a ListView or GridView.
 *
 * This adapter inflates a custom layout for each Event, populating views with details such as event name,
 * organizer name, location, description, start date, waitlist count, and price.
 *
 */
public class AdminEventArrayAdapter extends ArrayAdapter<Event> {
    /** The list of Event objects to display. */
    private ArrayList<Event> events;
    /** The context in which this adapter is being used. */
    private Context context;
    /**
     * Constructs a new EventArrayAdapter.
     *
     * @param context The context in which the adapter is used.
     * @param events The list of Event objects to display.
     */
    public AdminEventArrayAdapter(Context context, ArrayList<Event> events){

        super(context, 0 , events);
        this.events = events;
        this.context = context;
    }
    /**
     * Provides a view for an adapter view (ListView or GridView).
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_events, parent, false);
        }

        // Set the adminEventListView with all event data
        //title date description
        TextView eventNameView = convertView.findViewById(R.id.event_title);
        TextView dateNameView = convertView.findViewById(R.id.event_date);
        TextView descriptionView = convertView.findViewById(R.id.event_description);

        // Populate with event data
        eventNameView.setText(event.getName());
        //dateNameView.setText(event.getStartDate()); // TODO replace with date to string after replacement
        descriptionView.setText(event.getDescription());

        return convertView;
    }
    /**
     * Adds a new Event to the adapter's data set.
     *
     * @param event The Event to add.
     */
    public void addEvent(Event event){
        events.add(event);
        notifyDataSetChanged();
    }
    /**
     * Removes an Event from the adapter's data set at the specified position.
     *
     * @param position The position of the Event to remove.
     */
    public void deleteEvent(int position){
        events.remove(position);
        notifyDataSetChanged();
    }

}
