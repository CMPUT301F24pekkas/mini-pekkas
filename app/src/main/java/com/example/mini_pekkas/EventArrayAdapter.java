package com.example.mini_pekkas;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

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
        return super.getView(position, convertView, parent);
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
