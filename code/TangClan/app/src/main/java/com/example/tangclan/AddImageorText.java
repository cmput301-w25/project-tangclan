package com.example.tangclan;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddImageorText extends AppCompatActivity {

    private ImageView imageView;
    private ImageHelper imageHelper;
    private ImageValidator validor;
    private MoodEvent moodEvent;

    // launching the camera! Certain permissions will need to be adjusted here in the project, like
    // allowing the app to access the camera (and later, the gallery) on the phone / emulator. I will
    // deal with that after my lab (post-5:30PM, Friday, 14th Mar, 2025)
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri imageUri = imageHelper.getImageUri();
                    imageView.setImageURI(imageUri);
                }
            });

    // how we get the gallery open!
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    imageView.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_upload_picture);

        // initializing all the buttons and stuff on screen! the ids are a bit messed up but this
        // should be good enough to keep track of stuff.
        imageView = findViewById(R.id.imageView);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
        Button buttonNext = findViewById(R.id.btnNext); // goes to the next page
        Button buttonSaveText = findViewById(R.id.buttonSaveText);
        Button buttonSaveImage = findViewById(R.id.btnSave);
        TextView charCount = findViewById(R.id.charCount); // just a display of remaining characters for the reason
        TextInputEditText reason = findViewById(R.id.reason);

        // -----------------------------------------------------------------------------------------
        // IMAGE MANAGEMENT (whoop de do)
        // -----------------------------------------------------------------------------------------
        // creating an instance of the ImageHelper! (see that class for what it does)
        imageHelper = new ImageHelper(this, cameraLauncher, galleryLauncher);

        // setting up listeners for buttons onscreen
        buttonSelectImage.setOnClickListener(v -> imageHelper.showImagePickerDialog());
        buttonSaveImage.setOnClickListener(v -> {
            // Get the image URI
            Uri imageUri = imageHelper.getImageUri();

            // Validate the image size using the Validator
            if (validor.isImageSizeValid(this, imageUri)) {
                // Image is valid
                Toast.makeText(AddImageorText.this, "Image is valid!", Toast.LENGTH_SHORT).show();
                moodEvent.setImage(imageHelper.uriToBitmap(imageUri));
            } else {
                // Image is invalid
                Toast.makeText(AddImageorText.this, "Image is too large!", Toast.LENGTH_SHORT).show();
            }
        });

        // -----------------------------------------------------------------------------------------
        // REASON MANAGEMENT
        // -----------------------------------------------------------------------------------------
        // Set text watcher to monitor input
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

        // Handle button click for the REASON input
        buttonSaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = reason.getText().toString().trim();

                // Use ImageHelper's text size validation function
                boolean validatedInput = imageHelper.textValidation(userInput);

                if (!validatedInput) {
                    reason.setError("You're way over limit!");
                } else {
                    moodEvent.setReason(userInput); // ADD THIS 'String reason' ATTRIBUTE TO MOOD EVENT!
                    // MAKE REASON SETTER AND GETTER!!!!

                    Toast.makeText(AddImageorText.this, "Reason saved!", Toast.LENGTH_SHORT).show();
                }
            }
        }); // not dealing with no inputs since it's an optional thing for a Mood Event.

        // deal with this? i guess it tabs out to the feed?
        ImageView closeIcon = findViewById(R.id.closeIcon);

        // next button functionality (ALL OF THESE ARE OPTIONAL SO...
        // is this taken care of in the WizActivity technically?

} }
