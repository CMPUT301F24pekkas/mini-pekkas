package com.example.mini_pekkas;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class TextDrawable extends Drawable {
    private final String text;
    private final Paint textPaint;

    public TextDrawable(String text) {
        this.text = text;
        this.textPaint = new Paint();
        textPaint.setColor(Color.BLACK); // Change this to desired text color
        textPaint.setTextSize(100); // Adjust text size as needed
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();


        canvas.drawColor(Color.LTGRAY);


        float x = width / 2f;
        float y = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(text, x, y, textPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        textPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }
}
