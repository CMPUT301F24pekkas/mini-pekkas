package com.example.mini_pekkas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * FacilityArrayAdapter is a custom ArrayAdapter for displaying a list of facility objects in a ListView or GridView.
 *
 * This adapter inflates a custom layout for each facility, populating views with details such as facility name,
 * organizer name, location, description, start date, waitlist count, and price.
 *
 */
public class FacilityArrayAdapter extends ArrayAdapter<Facility> {
    /** The list of facility objects to display. */
    private ArrayList<Facility> facilities;
    /** The context in which this adapter is being used. */
    private Context context;

    /**
     * Constructs a new FacilityArrayAdapter.
     *
     * @param context The context in which the adapter is used.
     * @param facilities The list of Facility objects to display.
     */
    public FacilityArrayAdapter(Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
        this.facilities = facilities;
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
        Facility facility = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_facility, parent, false);
        }

        // Initialize views from layout
        TextView facilityTitleView = convertView.findViewById(R.id.facility_title_text);
        TextView facilityDescriptionView = convertView.findViewById(R.id.facility_description_text);
        ImageView facilityImageView = convertView.findViewById(R.id.facility_image_view);

        // Populate with facility data
        facilityTitleView.setText(facility.getName());
        Log.d("FacilityArrayAdapter", "Facility description: " + facility.getName() + facility.getDescription());
        facilityDescriptionView.setText(facility.getDescription() == null ? "No Description" : facility.getDescription());

        if (facility.getFacilityPhotoUrl()!= null && !facility.getFacilityPhotoUrl().isEmpty()) {
            Glide.with(context).load(facility.getFacilityPhotoUrl()).into(facilityImageView);
        }

        return convertView;
    }

    /**
     * Adds a new facility to the adapter's data set.
     *
     * @param facility The facility to add.
     */
    public void addFacility(Facility facility) {
        facilities.add(facility);
        notifyDataSetChanged();
    }

    /**
     * Removes an facility from the adapter's data set at the specified position.
     *
     * @param position The position of the facility to remove.
     */
    public void deleteFacility(int position) {
        facilities.remove(position);
        notifyDataSetChanged();
    }
}
