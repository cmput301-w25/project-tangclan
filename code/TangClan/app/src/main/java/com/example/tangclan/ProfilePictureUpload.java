package com.example.tangclan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tangclan.BirthdayCheckScreen;

import de.hdodenhof.circleimageview.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfilePictureUpload extends AppCompatActivity {
    //profilepictureupload activity that
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CROP_IMAGE_REQUEST = 3;

    private Button doLaterButton;
    private Button buttonNext;

    private CircleImageView profilePicture;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);

        profilePicture = findViewById(R.id.profile_picture);
        doLaterButton = findViewById(R.id.button_veri);
        buttonNext = findViewById(R.id.button_next);

        profilePicture.setOnClickListener(view -> showImageSourceDialog());

        doLaterButton.setOnClickListener(view -> navigateToNextScreen());

        buttonNext.setOnClickListener(view -> navigateToNextScreen());
    }

    private void navigateToNextScreen(){
        Intent intent = new Intent(ProfilePictureUpload.this, BirthdayCheckScreen.class);
        startActivity(intent);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                captureImageFromCamera();
            } else {
                pickImageFromGallery();
            }
        });
        builder.show();
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);  // Use PNG instead of JPEG

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CapturedImage", null);

        if (path == null) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }

        return Uri.parse(path);
    }

    private void captureImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null) {
                imageUri = data.getData();
                // Validate the image size before continuing
                if (ImageValidator.isImageSizeValid(this, imageUri)) {
                    startCropActivity(imageUri);
                }
            } else if (requestCode == CAMERA_REQUEST && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    imageUri = getImageUri(photo);
                    if (imageUri != null) {

                        if (ImageValidator.isImageSizeValid(this, imageUri)) {
                            startCropActivity(imageUri);
                        }
                    } else {
                        Toast.makeText(this, "Failed to process captured image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CROP_IMAGE_REQUEST) {
                handleCropResult(data);
            }
        }
    }

    private void startCropActivity(Uri sourceUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(sourceUri, "image/*");

        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 500);
        cropIntent.putExtra("outputY", 500);
        cropIntent.putExtra("return-data", true);
        cropIntent.putExtra("circleCrop", true);  // This works on some devices


        if (cropIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cropIntent, CROP_IMAGE_REQUEST);
        } else {
            Toast.makeText(this, "Cropping not supported, displaying original image", Toast.LENGTH_SHORT).show();
            displaySelectedImage(sourceUri);
        }
    }

    private void handleCropResult(Intent data) {
        if (data.getExtras() != null && data.getExtras().getParcelable("data") != null) {
            Bitmap croppedBitmap = data.getExtras().getParcelable("data");
            displaySelectedBitmap(croppedBitmap);
        } else if (data.getData() != null) {
            Uri croppedImageUri = data.getData();
            displaySelectedImage(croppedImageUri);
        } else {
            Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
            if (imageUri != null) {
                displaySelectedImage(imageUri);
            }
        }
    }

    private void displaySelectedImage(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= 28) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }

            runOnUiThread(() -> {
                profilePicture.setImageBitmap(bitmap);
                Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void displaySelectedBitmap(Bitmap bitmap) {
        profilePicture.setImageBitmap(bitmap);
        Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
    }
}
