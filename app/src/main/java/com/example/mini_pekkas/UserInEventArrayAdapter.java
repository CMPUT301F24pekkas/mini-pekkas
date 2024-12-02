package com.example.mini_pekkas;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModel;
import com.example.mini_pekkas.ui.home.OrganizerEventsListViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

/**
 * UserInEventArrayAdapter is a custom ArrayAdapter for displaying a list of User objects in a ListView or GridView.
 *
 * This adapter inflates a custom layout for each User, populating views with details such as name,
 * email, phone, and facility.
 */
public class UserInEventArrayAdapter extends ArrayAdapter<User> {
    /** The list of User objects to display. */
    private ArrayList<User> users;
    private OrganizerEventsListViewModel organizerEventsListViewModel;
    private Firebase firebasehelper;
    private int pendingCallbacks = 0;
    /** The context in which this adapter is being used. */
    private Context context;

    /**
     * Constructs a new UserInEventArrayAdapter.
     *
     * @param context The context in which the adapter is used.
     * @param users   The list of User objects to display.
     */
    public UserInEventArrayAdapter(Activity activity, Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
        organizerEventsListViewModel = new ViewModelProvider((ViewModelStoreOwner) activity, new OrganizerEventsListViewModelFactory(activity))
                .get(OrganizerEventsListViewModel.class);
        firebasehelper = new Firebase(context);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_user_in_event, parent, false);
        }

        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);  // Hide it initially
        deleteButton.setClickable(false);  // Make it unclickable initially

        TextView nameView = convertView.findViewById(R.id.userNameView);
        TextView statusView = convertView.findViewById(R.id.userStatusView);
        ImageView profileImageView = convertView.findViewById(R.id.userProfileImage);
        // Populate views with user data
        nameView.setText(user.getName() + " " + user.getLastname());
        //find status of user and set it
        Event event = organizerEventsListViewModel.getSelectedEvent().getValue();
        assert event != null;
        //get title if chosen
        TextView ListTitleView = parent.getRootView().findViewById(R.id.listTitle);
        assert ListTitleView != null;
        String listTitle = ListTitleView.getText().toString();
        Log.d("CancelEntrant", "list title: " + listTitle);
        //if it's the chosen entrants page make delete buttons appear
        if(listTitle.equals("Chosen Entrants")){
            Firebase.DataRetrievalListener statuslistener = new Firebase.DataRetrievalListener() {
                @Override
                public void onRetrievalCompleted(String status) {
                    Log.d("user", "userID " + user.getUserID() + "status: " + status);

                    if(status.equals("pending")){
                        deleteButton.setClickable(true);
                        deleteButton.setVisibility(View.VISIBLE);
                        statusView.setText("not enrolled");
                        Firebase.InitializationListener deleteUserListener = new Firebase.InitializationListener() {
                            @Override
                            public void onInitialized() {
                                deleteButton.setClickable(true);
                                deleteButton.setVisibility(View.VISIBLE);
                                deleteUser(position);
                                checkIfAllCallbacksCompleted();
                            }
                        };
                        deleteButton.setOnClickListener(v->{
                            firebasehelper.cancelUser(user.getUserID(), event.getId(), deleteUserListener);
                        });
                    }
                    else{
                        statusView.setText("enrolled");
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.d("CancelEntrant", "Error occurred: " + e.getMessage());
                }

            };
            firebasehelper.getUserStatusInEvent(user,event, statuslistener);
            try {
                Thread.sleep(0,10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            Firebase.DataRetrievalListener statuslistener = new Firebase.DataRetrievalListener() {
                @Override
                public void onRetrievalCompleted(String status) {
                    if(!Objects.equals(status, "enrolled")){
                        statusView.setText("not enrolled");
                    }
                    else{
                        statusView.setText("enrolled");
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.d("user", "Error occurred: " + e.getMessage());
                }

            };
            firebasehelper.getStatusInEvent(event, statuslistener);
        }
        // Set profile image if available, otherwise use a default placeholder
        if (user.getProfilePhotoUrl() != null && !user.getProfilePhotoUrl().isEmpty()) {
            // Load profile image
            Glide.with(context).load(user.getProfilePhotoUrl()).into(profileImageView);
        } else {
            // Placeholder image or just nothing?
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
    public void addUsers(ArrayList<User> usersToAdd) {
        users.addAll(usersToAdd);
        notifyDataSetChanged();
    }


    private void checkIfAllCallbacksCompleted() {
        pendingCallbacks--; // Decrement the counter when a callback completes

        if (pendingCallbacks == 0) {
            // Both listeners have finished, you can proceed with further actions
            Log.d("user", "Both listeners finished processing");
            // For example, you can update the UI or trigger other logic here
        }
    }

}
