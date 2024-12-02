package com.example.mini_pekkas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * This class is used to display images in a recycler view
 */
public class QrCodeAdapter extends RecyclerView.Adapter<QrCodeAdapter.ImageViewHolder> {

    private final List<Bitmap> images;
    private final Context context;
    private QrCodeAdapter adapter; // Add adapter reference
    private List<Event> events; // Add events list>

    public QrCodeAdapter(Context context, List<Bitmap> images, List<Event> events) {
        this.context = context;
        this.images = images;
        this.adapter = this;
        this.events = events;
    }

    /**
     * Remove an item from the list. Used in delete button click listener
     * @param position The position of the item to be removed
     */
    public void removeItem(int position) {
        images.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Called when RecyclerView needs a new {@link ImageViewHolder} of the given type to represent
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_image, parent, false);
        return new ImageViewHolder(view, images, adapter, events);
    }

    /**
     * Load the image into the view holder, and set the progress bar visibility
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Bitmap imageBitmap = images.get(position);
        // Reset visibility before loading image
        holder.progressBar.setVisibility(View.VISIBLE);
        // Render Images in set Box sizes
        Glide.with(context)
                .load(imageBitmap)
                .centerCrop()
                .error(R.drawable.camera) // Set an error image
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle image loading failure
                        holder.progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image loaded successfully
                        holder.progressBar.setVisibility(View.GONE); // Hide progress bar on success
                        return false;
                    }
                })
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    /**
     * This class is used to display images in a recycler view. Set the image view and progress bar
     * Also set the delete button
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public List<Bitmap> images;
        public ImageView imageView;
        public ProgressBar progressBar; // Add ProgressBar
        public FloatingActionButton deleteButton;
        public Firebase firebaseHelper;
        public QrCodeAdapter adapter;
        public List<Event> events;

        public ImageViewHolder(@NonNull View itemView, List<Bitmap> images, QrCodeAdapter adapter, List<Event> events) {
            super(itemView);
            this.images = images;
            imageView = itemView.findViewById(R.id.imageView);
            progressBar = itemView.findViewById(R.id.loadingProgressBar); // Initialize progressBar
            deleteButton = itemView.findViewById(R.id.deleteImage);
            firebaseHelper = new Firebase(itemView.getContext());
            this.adapter = adapter;
            this.events = events;

            // Set the delete button click listener
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Hashcode")
                            .setMessage("Are you sure you want to delete this QR code? This will also delete the associated event")
                            .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                                // Delete QR code logic
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    Event event = events.get(position);
                                    // Delete the event, update the adapter
                                    firebaseHelper.deleteEvent(event);
                                    adapter.removeItem(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

        }
    }
}