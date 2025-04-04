package com.example.tangclan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import android.Manifest;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class provides functionality for uploading images from the camera or gallery on the device.
 * It also has methods to help with image formats, as well as for reason validation.
 */
public class ImageHelper {
    private final Activity activity;
    private final ActivityResultLauncher<Intent> cameraLauncher;
    private final ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageUri;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    public ImageHelper(Activity activity,
                       ActivityResultLauncher<Intent> cameraLauncher,
                       ActivityResultLauncher<Intent> galleryLauncher) {
        this.activity = activity;
        this.cameraLauncher = cameraLauncher;
        this.galleryLauncher = galleryLauncher;
    }

    public void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Upload image from: ")
                .setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void launchCamera() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Create file for the full-size image
            File photoFile = createImageFile();
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".fileprovider",
                        photoFile);

                // Specify where to save the full image
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(cameraIntent);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error creating image file", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error launching camera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public Bitmap uriToBitmap(Uri uri) {
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public Bitmap base64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public boolean textValidation(String text) {
        if (text.length() > 200) {
            Toast.makeText(activity, "Text is too long!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Saves a bitmap to a PNG file in the app's external files directory.
     *
     * @param bitmap the bitmap to save
     * @return the absolute path to the saved file, or null if saving failed
     */
    public String saveBitmapToFile(Bitmap bitmap) {
        try {
            File outputDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Create a file with timestamp to ensure unique filenames
            File outputFile = new File(outputDir, "mood_image_" + System.currentTimeMillis() + ".png");

            // Save the bitmap as PNG (lossless)
            FileOutputStream fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            return outputFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
