package com.example.tangclan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

public class UploadPictureForMoodEventActivity extends AppCompatActivity {

    private String selectedEmotion;
    private String selectedSituation;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_upload_picture);

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve passed data from AddSocialSituationActivity using the Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedEmotion = extras.getString("selectedEmotion");
            selectedSituation = extras.getString("selectedSituation");
        }

        // Initialize UI components
        imageView = findViewById(R.id.imageView);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
        Button buttonSaveImage = findViewById(R.id.btnSave);
        Button buttonSaveText = findViewById(R.id.buttonSaveText);
        Button buttonNext = findViewById(R.id.btnNext);
        TextView charCount = findViewById(R.id.charCount);
        TextInputEditText reason = findViewById(R.id.reason);
        ImageView closeIcon = findViewById(R.id.closeIcon);

        // Initialize ImageHelper
        imageHelper = new ImageHelper(this, cameraLauncher, galleryLauncher);

        // Set up image selection
        buttonSelectImage.setOnClickListener(v -> imageHelper.showImagePickerDialog());

        // Save image button functionality
        buttonSaveImage.setOnClickListener(v -> {
            if (imageUri != null) {
                // Using the static method from ImageValidator
                if (ImageValidator.isImageSizeValid(this, imageUri)) {
                    selectedImage = imageHelper.uriToBitmap(imageUri);
                    Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Image is too large!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            }
        });

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

        // Save text button functionality
        buttonSaveText.setOnClickListener(v -> {
            String userInput = reason.getText().toString().trim();

            boolean validatedInput = imageHelper.textValidation(userInput);

            if (!validatedInput) {
                reason.setError("You're way over limit!");
            } else {
                Toast.makeText(this, "Reason saved!", Toast.LENGTH_SHORT).show();
            }
        });

        // Next button (proceed to profile page)
        buttonNext.setOnClickListener(v -> {
            String userInput = reason.getText().toString().trim();

            // Create a bundle to carry all the collected data
            Bundle bundle = new Bundle();
            bundle.putString("selectedEmotion", selectedEmotion);
            bundle.putString("selectedSituation", selectedSituation);

            // Add reason if valid
            if (!TextUtils.isEmpty(userInput) && imageHelper.textValidation(userInput)) {
                bundle.putString("reason", userInput);
            }

            // Add image if selected
            if (selectedImage != null) {
                String imagePath = imageHelper.saveBitmapToFile(selectedImage);
                if (imagePath != null) {
                    bundle.putString("imagePath", imagePath);
                }
            }

            // Create an intent to start the next activity
            Intent intent = new Intent(UploadPictureForMoodEventActivity.this, ProfilePageActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });

        // Close button action
        closeIcon.setOnClickListener(v -> finish());
    }
}