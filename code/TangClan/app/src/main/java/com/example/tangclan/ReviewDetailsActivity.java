package com.example.tangclan;

import static android.view.View.FIND_VIEWS_WITH_TEXT;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;


import java.util.ArrayList;


public class ReviewDetailsActivity extends AppCompatActivity {
    String selectedEmotion;
    String selectedSetting;
    ArrayList<String> collaborators;
    String reasonText;
    String reasonImage;

    boolean privacyOn;

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
        // TODO: save location: toggle state to enable permission, editText with address or both??
        Button confirmButton = findViewById(R.id.submit_details);
        ImageButton closeIcon = findViewById(R.id.cancel_edit);

        setDetails(emotionTextView, settingTextView, collaboratorTextView, reasonTextView, imageView, privacyToggle);

        // Listen for toggle
        privacyToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacyOn = !privacyOn; // switch
                privacyToggle.setChecked(privacyOn);
                savedDetails.putBoolean("privacy", privacyOn); // update bundle
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
        }
    }

    public void setDetails(AutoCompleteTextView emotionTextView,
                           AutoCompleteTextView settingTextView,
                           EditText collaboratorTextView,
                           EditText reasonTextView,
                           ImageButton imageView,
                           SwitchCompat privacyToggle) {
        emotionTextView.setText(selectedEmotion);
        if (selectedSetting != null) {
            settingTextView.setText(selectedSetting);
        }

        if (collaborators != null) {
            StringBuilder s = new StringBuilder();
            for (String collaborator : collaborators) {
                s.append(collaborator.trim()).append(", ");
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
    }
}