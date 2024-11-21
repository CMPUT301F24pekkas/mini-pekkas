package com.example.mini_pekkas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NotificationsArrayAdapter extends ArrayAdapter<Notifications>{
    private Context context;
    private int resource;

    public NotificationsArrayAdapter(Context context, int resource, List<Notifications> notifications) {
        super(context, resource, notifications);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notifications notifications = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        TextView titleTextView = convertView.findViewById(R.id.notificationTitle);
        TextView descriptionTextView = convertView.findViewById(R.id.notificationDescription);
        TextView dateTextView = convertView.findViewById(R.id.notificationDate);
        TextView priorityTextView = convertView.findViewById(R.id.notificationPriority);

        titleTextView.setText(notifications.getTitle());
        descriptionTextView.setText(notifications.getDescription());
        dateTextView.setText(notifications.getDate().toString());
        priorityTextView.setText(String.valueOf(notifications.getPriority()));

        return convertView;
    }

}
