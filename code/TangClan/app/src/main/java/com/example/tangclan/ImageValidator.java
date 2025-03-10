package com.example.tangclan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

//for US 02.03.01

/**
 * This class provides functionality for validating image files.
 * It ensures that the image size is within an acceptable range and compresses the image if necessary.
 */
public class ImageValidator {
    // Maximum allowed image size in bytes (64 KB)
    private static final int MAX_IMAGE_SIZE = 65536;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ImageValidator() {
    }

    /**
     * Validates whether the image size is below the maximum allowed size (64 KB).
     *
     * @param context  the context to use for accessing the content resolver
     * @param imageUri the URI of the image to validate
     * @return true if the image size is valid, false otherwise
     */
    public static boolean isImageSizeValid(Context context, Uri imageUri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri)) {
            if (inputStream != null) {
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);


                byte[] compressedBytes = compressBitmapToSize(originalBitmap, MAX_IMAGE_SIZE);

                if (compressedBytes == null) {
                    Toast.makeText(context, "Image too large! Please select a smaller image.", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        } catch (IOException e) {
            Toast.makeText(context, "Error reading image", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    /**
     * Compresses a bitmap image to fit within the specified maximum size.
     * The image is compressed in iterations, reducing quality until the size is below the max size.
     *
     * @param bitmap   the bitmap image to compress
     * @param maxSize  the maximum allowed size in bytes
     * @return a byte array containing the compressed image, or null if the image cannot be compressed within the size limit
     */
    static byte[] compressBitmapToSize(Bitmap bitmap, int maxSize) {
        int quality = 100; // Start with max quality
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        do {
            outputStream.reset(); // Clear buffer before retrying
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            if (outputStream.toByteArray().length <= maxSize) {
                return outputStream.toByteArray();
            }
            quality -= 5;
        } while (quality > 0);

        return null;
    }
}