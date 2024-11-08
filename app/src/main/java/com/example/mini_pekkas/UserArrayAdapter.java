package com.example.mini_pekkas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * UserArrayAdapter is a custom ArrayAdapter for displaying a list of User objects in a ListView or GridView.
 *
 * This adapter inflates a custom layout for each User, populating views with details such as name,
 * email, phone, and facility.
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    /** The list of User objects to display. */
    private ArrayList<User> users;
    /** The context in which this adapter is being used. */
    private Context context;

    /**
     * Constructs a new UserArrayAdapter.
     *
     * @param context The context in which the adapter is used.
     * @param users   The list of User objects to display.
     */
    public UserArrayAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    /**
     * Provides a view for an adapter view (ListView or GridView).
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }


        TextView nameView = convertView.findViewById(R.id.userNameView);
        TextView emailView = convertView.findViewById(R.id.userEmailView);
        TextView phoneView = convertView.findViewById(R.id.userPhoneView);
        TextView facilityView = convertView.findViewById(R.id.userFacilityView);
        ImageView profileImageView = convertView.findViewById(R.id.userProfileImage);

        // Populate views with user data
        nameView.setText(user.getName() + " " + user.getLastname());
        emailView.setText("Email: " + user.getEmail());
        phoneView.setText("Phone: " + user.getPhone());
        facilityView.setText("Facility: " + user.getFacility());

        // Set profile image if available, otherwise use a default placeholder
        if (user.getProfilePhotoUrl() != null && !user.getProfilePhotoUrl().isEmpty()) {
            // Load image using your preferred library (e.g., Glide or Picasso)
            // Example: Glide.with(context).load(user.getProfilePhotoUrl()).into(profileImageView);
        } else {

        }

        return convertView;
    }

    /**
     * Adds a new User to the adapter's data set.
     *
     * @param user The User to add.
     */
    public void addUser(User user) {
        users.add(user);
        notifyDataSetChanged();
    }

    /**
     * Removes a User from the adapter's data set at the specified position.
     *
     * @param position The position of the User to remove.
     */
    public void deleteUser(int position) {
        users.remove(position);
        notifyDataSetChanged();
    }
}
