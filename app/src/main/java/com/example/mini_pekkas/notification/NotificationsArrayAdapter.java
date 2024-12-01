package com.example.mini_pekkas.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mini_pekkas.R;

import java.text.DateFormat;
import java.util.List;

/**
 * The NotificationsArrayAdapter is a custom adapter used to display a list of notifications in a ListView.
 * It binds the notification data to the views in each item of the list.
 */
public class NotificationsArrayAdapter extends ArrayAdapter<Notifications>{
    private Context context;
    private int resource;

    /**
     * Constructor for NotificationsArrayAdapter.
     *
     * @param context       The context in which the adapter is being used, typically an Activity or Fragment.
     * @param resource      The layout resource ID for the list item.
     * @param notifications A list of Notifications objects that will be displayed in the ListView.
     */
    public NotificationsArrayAdapter(Context context, int resource, List<Notifications> notifications) {
        super(context, resource, notifications);
        this.context = context;
        this.resource = resource;
    }

    /**
     * Gets a view for the notification at the specified position in the list.
     *
     * @param position    The position of the notification in the list.
     * @param convertView The recycled view (if available) that can be reused.
     * @param parent      The parent view that the list item will be attached to.
     * @return The view for the notification at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notifications notifications = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        TextView titleTextView = convertView.findViewById(R.id.header_text);
        TextView descriptionTextView = convertView.findViewById(R.id.supporting_text);
        TextView dateTextView = convertView.findViewById(R.id.header_date);

        titleTextView.setText(notifications.getTitle());
        descriptionTextView.setText(notifications.getDescription());
        // User date format to display date
        String dateString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(notifications.getDate());
        dateTextView.setText(dateString);

        return convertView;
    }

}
