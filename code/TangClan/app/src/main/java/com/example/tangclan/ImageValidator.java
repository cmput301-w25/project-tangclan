package com.example.tangclan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import com.example.tangclan.Profile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

public class ImageValidator {
    //image validation class
    private static final int MAX_IMAGE_SIZE = 65536; // 64 KB

    // Callback interface for updating Profile
    public interface ImageValidationCallback {
        void onImageValidated(Blob imageBlob);
    }

    private ImageValidator() {
    }

    public static boolean isImageSizeValid(Context context, Uri imageUri, Profile profile) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri)) {
            if (inputStream != null) {
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

                byte[] compressedBytes = compressBitmapToSize(originalBitmap, MAX_IMAGE_SIZE);
                if (compressedBytes == null) {
                    Toast.makeText(context, "Image too large! Please select a smaller image.", Toast.LENGTH_LONG).show();
                    return false;
                }

                // Convert byte array to Blob
                try {
                    Blob imageBlob = new SerialBlob(compressedBytes);
                    profile.setProfilePic(imageBlob);  // Update the profile's image
                    return true;
                } catch (SQLException e) {
                    Toast.makeText(context, "Error processing image", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            Toast.makeText(context, "Error reading image", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    static byte[] compressBitmapToSize(Bitmap bitmap, int maxSize) {
        int quality = 100;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        do {
            outputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            if (outputStream.toByteArray().length <= maxSize) {
                return outputStream.toByteArray();
            }
            quality -= 5;
        } while (quality > 0);

        return null;
    }
}
