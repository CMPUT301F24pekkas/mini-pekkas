package com.example.mini_pekkas;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
/**
 * QRCodeGenerator creates a QR code and returns it as a bitmap
 */
public class QRCodeGenerator {

    /**
     * Generates a QR code as a Bitmap image from the specified text input.
     *
     * @param text The text to encode within the QR code.
     * @param width The width of the QR code image in pixels.
     * @param height The height of the QR code image in pixels.
     * @return A Bitmap image of the generated QR code, or null if an error occurs.
     */
    public static Bitmap generateQRCode(String text, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bmp; // Return the generated Bitmap
        } catch (WriterException e) {
            e.printStackTrace();
            return null; // Return null if there's an error
        }
    }
}
