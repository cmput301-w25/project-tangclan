package com.example.tangclan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class UploadPictureForMoodEventActivity extends AppCompatActivity {

    private String selectedEmotion;

    private String savedReason;
    private String savedImg;
    private ArrayList<String> selectedSituation;
    private ImageView imageView;
    private ImageHelper imageHelper;
    private Uri imageUri = null;
    private Bitmap selectedImage = null;

    // Camera launcher
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageUri = imageHelper.getImageUri();
                    imageView.setImageURI(imageUri);
                    selectedImage = imageHelper.uriToBitmap(imageUri);
                }
            });

    // Gallery launcher
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageView.setImageURI(imageUri);
                    selectedImage = imageHelper.uriToBitmap(imageUri);
                }
            });


    private String imagePath = null; // Optional image path (null if not uploaded)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_upload_picture);

        // Initialize UI components
        imageView = findViewById(R.id.imageView);
        Button buttonNext = findViewById(R.id.btnNext);
        TextView charCount = findViewById(R.id.charCount);
        TextInputEditText reason = findViewById(R.id.reason);
        ImageView closeIcon = findViewById(R.id.closeIcon);
        Button btnBack = findViewById(R.id.btnBackEnvironment);

        // Initialize ImageHelper
        imageHelper = new ImageHelper(this, cameraLauncher, galleryLauncher);

        // Retrieve passed data from AddSocialSituationActivity using the Bundle
        Bundle savedDetails = getIntent().getExtras();
        if (savedDetails != null) {
            savedReason = savedDetails.getString("reason");
            savedImg = savedDetails.getString("image");
            // load saved details
            if (savedReason != null) {
                reason.setText(savedReason);
            }
            if (savedImg != null){
                imageView.setImageBitmap(imageHelper.base64ToBitmap(savedImg));
            }
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reason.getText() != null) {
                    savedDetails.putString("reason", reason.getText().toString().trim());  // not null since first fragment handles this
                }
                if (validImage()) {
                    if (selectedImage != null) {
                        byte[] imgBytes = ImageValidator.compressBitmapToSize(selectedImage);
                        savedDetails.putString("image", Base64.encodeToString(imgBytes, Base64.DEFAULT));
                    }
                }
                Intent intent = new Intent(UploadPictureForMoodEventActivity.this, AddSocialSituationActivity.class);
                intent.putExtras(savedDetails);
                startActivity(intent);
                finish();
            }
        });
        // Set up image selection
        imageView.setOnClickListener(v -> imageHelper.showImagePickerDialog());

        // Set text watcher for character count
        reason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int remainingChars = 200 - s.length();

                if (remainingChars <= 50) {
                    charCount.setVisibility(View.VISIBLE);
                    charCount.setText(remainingChars + " characters remaining");
                } else {
                    charCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Next button (proceed to profile page)
        buttonNext.setOnClickListener(v -> {
            String userInput = reason.getText().toString().trim();

            // Create a bundle to carry all the collected data
            Bundle bundle = new Bundle();
            bundle.putString("selectedEmotion", selectedEmotion);

            bundle.putStringArrayList("selectedSituation", selectedSituation);

            bundle.putString("reason", String.valueOf(reason));

            //


            // Add reason if valid
            if (imageHelper.textValidation(userInput)) {
                bundle.putString("reason", userInput);
                savedDetails.putString("reason", TextUtils.isEmpty(userInput) ? null : userInput);
            } else {
                reason.setError("You're way over limit!");
                return;
            }

            // Check for valid image or no image
            if (!validImage()){
                return;
            }
            // Check if there is an image
            if (selectedImage != null) {
                String imagePath = imageHelper.saveBitmapToFile(selectedImage);
                if (imagePath != null) {
                    bundle.putString("imagePath", imagePath);
                }
                byte[] imgBytes = ImageValidator.compressBitmapToSize(selectedImage);
                savedDetails.putString("image", Base64.encodeToString(imgBytes, Base64.DEFAULT));
            }
            // Create an intent to start the next activity
            Intent intent = new Intent(UploadPictureForMoodEventActivity.this, ReviewDetailsActivity.class);
            intent.putExtras(savedDetails);
            startActivity(intent);
            finish();
        });

        // Close button action
        closeIcon.setOnClickListener(v -> {
            startActivity(new Intent(UploadPictureForMoodEventActivity.this, FeedActivity.class));
            finish();
        });
    }

    public boolean validImage() {
        if (imageUri != null) {
            // Using the static method from ImageValidator
            if (ImageValidator.isImageSizeValid(this, imageUri)) {
                selectedImage = imageHelper.uriToBitmap(imageUri);
                Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(this, "Image is too large!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;  // no image selected
    }
}