package com.example.tangclan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageValidator {

    private static final int MAX_IMAGE_SIZE = 65536; // 64 KB

    private ImageValidator() {
    }


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