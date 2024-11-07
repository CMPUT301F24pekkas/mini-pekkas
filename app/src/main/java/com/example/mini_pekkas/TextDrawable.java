package com.example.mini_pekkas;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
/**
 * TextDrawable is a custom Drawable class that draws a specified text centered on a canvas.
 * The background is set to a light gray color, with configurable text color and size.
 */
public class TextDrawable extends Drawable {
    /** The text to display on the drawable. */
    private final String text;
    /** Paint object used for rendering the text. */
    private final Paint textPaint;
    /**
     * Constructs a TextDrawable with the specified text.
     *
     * @param text The text to display on the drawable.
     */
    public TextDrawable(String text) {
        this.text = text;
        this.textPaint = new Paint();
        textPaint.setColor(Color.BLACK); // Change this to desired text color
        textPaint.setTextSize(100); // Adjust text size as needed
        textPaint.setTextAlign(Paint.Align.CENTER);
    }
    /**
     * Draws the text centered on the canvas with a light gray background.
     *
     * @param canvas The canvas on which the background and text are drawn.
     */
    @Override
    public void draw(Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();


        canvas.drawColor(Color.LTGRAY);


        float x = width / 2f;
        float y = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(text, x, y, textPaint);
    }
    /**
     * Sets the alpha transparency for the text.
     *
     * @param alpha The alpha value (0-255) to set for the text.
     */
    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
    }
    /**
     * Sets a color filter for the text.
     *
     * @param colorFilter The color filter to apply to the text paint.
     */
    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        textPaint.setColorFilter(colorFilter);
    }
    /**
     * Returns the opacity/transparency of the drawable.
     *
     * @return The opacity of the drawable
     */
    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }
}
