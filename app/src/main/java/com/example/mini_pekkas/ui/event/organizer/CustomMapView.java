package com.example.mini_pekkas.ui.event.organizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view for displaying a map with markers and current location.
 */
public class CustomMapView extends View {

    private Paint markerPaint;
    private List<PointF> markers = new ArrayList<>();
    private PointF currentLocation;

    /**
     * Constructor for CustomMapView.
     * @param context The context
     * @param attrs attributes of map view
     */
    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        markerPaint = new Paint();
        markerPaint.setColor(Color.RED);
        markerPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Add a marker to the map.
     * @param latitude latitude of marker
     * @param longitude longitude of marker
     */
    public void addMarker(double latitude, double longitude) {
        markers.add(convertToCanvasPoint(latitude, longitude));
        invalidate(); // Redraw the view
    }

    /**
     * Update the current location on the map.
     * @param latitude the latitude to be changed
     * @param longitude the longitude to be changed
     */
    public void updateCurrentLocation(double latitude, double longitude) {
        currentLocation = convertToCanvasPoint(latitude, longitude);
        invalidate(); // Redraw the view
    }

    /**
     * Convert latitude and longitude to canvas coordinates.
     * @param latitude latitude of marker
     * @param longitude longitude of marker
     * @return PointF representing the canvas coordinates
     */
    private PointF convertToCanvasPoint(double latitude, double longitude) {
        // Convert lat/lon to x/y canvas coordinates
        float x = (float) (longitude * 10); // Example conversion
        float y = (float) (latitude * 10);
        return new PointF(x, y);
    }

    /**
     * Draw the markers and current location on the canvas.
     * @param canvas canvas to draw on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw markers
        for (PointF marker : markers) {
            canvas.drawCircle(marker.x, marker.y, 10, markerPaint);
        }

        // Draw current location
        if (currentLocation != null) {
            markerPaint.setColor(Color.BLUE);
            canvas.drawCircle(currentLocation.x, currentLocation.y, 15, markerPaint);
        }
    }
}