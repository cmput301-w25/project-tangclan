package com.example.tangclan;

import static android.view.View.FIND_VIEWS_WITH_TEXT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;


import com.google.firebase.firestore.FieldValue;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;


public class ReviewDetailsActivity extends AppCompatActivity {
    String selectedEmotion;
    String selectedSetting;
    ArrayList<String> collaborators;
    String reasonText;
    String reasonImage;

    boolean privacyOn;
    boolean locationOn;
    GeoPoint receivedPoint;
    String locationString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_details_fragment);

        // get bundle
        Bundle savedDetails = getIntent().getExtras();
        loadSavedDetails(savedDetails);

        // find Views
        AutoCompleteTextView emotionTextView = findViewById(R.id.choose_emotion);
        AutoCompleteTextView settingTextView = findViewById(R.id.choose_social_situation);
        EditText collaboratorTextView = findViewById(R.id.edit_social_situation);
        EditText reasonTextView = findViewById(R.id.edit_reasonwhy);
        ImageButton imageView = findViewById(R.id.image_reasonwhy);
        SwitchCompat privacyToggle = findViewById(R.id.privacy_toggle);
        TextView locationDisplay = findViewById(R.id.location_display);
        SwitchCompat locationToggle = findViewById(R.id.use_location_switch);

        Button confirmButton = findViewById(R.id.submit_details);
        ImageView closeIcon = findViewById(R.id.closeIcon);

        setDetails(emotionTextView, settingTextView, collaboratorTextView, reasonTextView, imageView, privacyToggle, locationToggle, locationDisplay);

        // Listen for toggle
        privacyToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacyOn = !privacyOn; // switch
                privacyToggle.setChecked(privacyOn);
                savedDetails.putBoolean("privacy", privacyOn); // update bundle
            }
        });

        locationToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // turn it all on
                locationOn = !locationOn;
                locationToggle.setChecked(locationOn);

                if (locationToggle.isChecked()) { // If the toggle is turned on
                    // Show a prompt to confirm
                    new AlertDialog.Builder(ReviewDetailsActivity.this)  // Use the current activity context
                            .setMessage("Attach location to mood event?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // If user selects Yes, don't change previous settings
                                // open the AddLocationActivity

                                Intent intent = new Intent(ReviewDetailsActivity.this, AddLocationActivity.class);
                                intent.putExtras(savedDetails);
                                startActivity(intent);
                                savedDetails.putBoolean("location", true);

                                double lat = getIntent().getDoubleExtra("latitude", 0.0);
                                double lon = getIntent().getDoubleExtra("longitude", 0.0);
                                String locationName = getIntent().getStringExtra("locationName");

                                if (locationName != null) {
                                    locationDisplay.setText(locationName);
                                    locationDisplay.setVisibility(View.VISIBLE);
                                } else {
                                    locationDisplay.setVisibility(View.GONE);
                                }
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                // If user selects No, turn off the toggle and locationOn
                                locationToggle.setChecked(false);
                                locationOn = !locationOn;
                                savedDetails.putBoolean("location", false);
                                locationDisplay.setVisibility(View.GONE);
                            })
                            .show();
                } else {
                    // If toggle is turned off (user clicked "No" or it was toggled off)
                    savedDetails.putBoolean("location", false);
                    locationDisplay.setVisibility(View.GONE);
                }
            }
        });

        // Go back to fragments
        emotionTextView.setOnClickListener(view -> changeScreen(AddEmotionActivity.class, savedDetails));
        settingTextView.setOnClickListener(view -> changeScreen(AddSocialSituationActivity.class, savedDetails));
        collaboratorTextView.setFocusable(false);
        collaboratorTextView.setOnClickListener(view -> changeScreen(AddSocialSituationActivity.class, savedDetails));
        reasonTextView.setFocusable(false);
        reasonTextView.setOnClickListener(view -> changeScreen(UploadPictureForMoodEventActivity.class, savedDetails));
        imageView.setOnClickListener(view -> changeScreen(UploadPictureForMoodEventActivity.class, savedDetails));
        confirmButton.setOnClickListener(view -> changeScreen(ProfilePageActivity.class, savedDetails));

        closeIcon.setOnClickListener(v -> {
            startActivity(new Intent(ReviewDetailsActivity.this, FeedActivity.class));
            finish();
        });
    }


    public void changeScreen(Class<?> detailScreen, Bundle savedDetails) {
        Intent intent = new Intent(ReviewDetailsActivity.this, detailScreen);
        intent.putExtras(savedDetails);
        startActivity(intent);
        finish();
    }

    public void loadSavedDetails(Bundle savedDetails) {
        if (savedDetails != null) {
            selectedEmotion = savedDetails.getString("emotion");
            selectedSetting = savedDetails.getString("setting");
            collaborators = savedDetails.getStringArrayList("collaborators");
            reasonText = savedDetails.getString("reason");
            reasonImage = savedDetails.getString("image");
            privacyOn = savedDetails.getBoolean("privacy");
            locationOn = savedDetails.getBoolean("location");

            double lat = savedDetails.getDouble("latitude");
            double lon = savedDetails.getDouble("longitude");
            receivedPoint = new GeoPoint(lat, lon);

            locationString = savedDetails.getString("locationName");
        }
    }

    public void setDetails(AutoCompleteTextView emotionTextView,
                           AutoCompleteTextView settingTextView,
                           EditText collaboratorTextView,
                           EditText reasonTextView,
                           ImageButton imageView,
                           SwitchCompat privacyToggle,
                           SwitchCompat locationToggle,
                           TextView locationDisplay) {
        emotionTextView.setText(selectedEmotion);
        if (selectedSetting != null) {
            settingTextView.setText(selectedSetting);
        }

        if (collaborators != null) {
            StringBuilder s = new StringBuilder();
            if (!collaborators.get(0).isEmpty()) {
                for (String collaborator : collaborators) {
                    s.append(collaborator.trim()).append(", ");
                }
            }
            collaboratorTextView.setText(s);
        }

        if (reasonText != null){
            reasonTextView.setText(reasonText);
        }

        if (reasonImage != null){
            byte[] decodedBytes = Base64.decode(reasonImage, Base64.DEFAULT);
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(imgBitmap);
        }

        privacyToggle.setChecked(privacyOn);

        locationToggle.setChecked(locationOn);

        if (locationString != null && locationToggle.isChecked()) {
            locationDisplay.setText(locationString);
        }
    }
}