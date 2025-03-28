package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AddEmotionActivity extends AppCompatActivity {

    private String selectedEmotion = null;
    private ImageButton selectedButton = null;
    private Bundle savedDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_emotion);

        // Initialize buttons
        ImageButton[] emotionButtons = {
                findViewById(R.id.emotionHappy),
                findViewById(R.id.emotionCalm),
                findViewById(R.id.emotionSurprised),
                findViewById(R.id.emotionDisgusted),
                findViewById(R.id.emotionAngry),
                findViewById(R.id.emotionConfused),
                findViewById(R.id.emotionTerrfied),
                findViewById(R.id.emotionNoIdea),
                findViewById(R.id.emotionAshamed),
                findViewById(R.id.emotionSad),
                findViewById(R.id.emotionAnxious)
        };

        savedDetails = getIntent().getExtras();
        if (savedDetails != null) {
            // load saved details
            String savedEmotion = savedDetails.getString("emotion");
            selectEmotion(savedEmotion, emotionButtons[getButtonIndex(savedEmotion)]);  // preselect saved emotion
        } else {
            // make new bundle
            savedDetails = new Bundle();
            savedDetails.putString("emotion", null);
            savedDetails.putString("setting", null);
            savedDetails.putStringArrayList("collaborators", null);
            savedDetails.putString("reason", null);
            savedDetails.putString("image", null);
            savedDetails.putString("location", null);
            savedDetails.putBoolean("privacy", false);
        }

        // Emotion labels corresponding to the buttons
        String[] emotionNames = {
                "happy", "calm", "surprised", "disgusted", "angry",
                "confused", "terrified", "no_idea", "ashamed", "sad", "anxious"
        };

        // Set onClick listeners for each emotion button
        for (int i = 0; i < emotionButtons.length; i++) {
            final String emotion = emotionNames[i];
            final ImageButton button = emotionButtons[i];
            button.setOnClickListener(v -> selectEmotion(emotion, button));
        }

        // Set up cancel button to finish the activity
        Button btnCancel = findViewById(R.id.btnCancelEmotion);
        btnCancel.setOnClickListener(v -> {
            startActivity(new Intent(AddEmotionActivity.this, FeedActivity.class));
            finish();
        });

        // Set up next button to navigate to AddSocialSituationActivity
        Button btnNext = findViewById(R.id.btnNextEmotion);
        btnNext.setOnClickListener(v -> {
            if (selectedEmotion == null) {
                Toast.makeText(this, "Please select an emotion", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create an intent to start the next activity
            Intent intent = new Intent(AddEmotionActivity.this, AddSocialSituationActivity.class);
            // Attach the bundle to the intent
            intent.putExtras(savedDetails);

            // Start the activity
            startActivity(intent);  // Start AddSocialSituationActivity
            finish();
        });

        // Set up the close button (ImageView) to navigate to FeedActivity
        ImageView closeIcon = findViewById(R.id.closeIcon);  // Use the correct ID for your close button
        closeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(AddEmotionActivity.this, FeedActivity.class);
            startActivity(intent);  // Start FeedActivity
            finish();  // Finish the current activity to exit
        });
    }

    // Helper method to handle emotion selection
    private void selectEmotion(String emotion, ImageButton button) {
        // If another button was previously selected, reset its style
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(0);  // Reset background
        }

        // Set new selected button's background or style
        button.setBackgroundResource(R.drawable.selected_button_background); // Use a custom background to highlight

        selectedEmotion = emotion;
        savedDetails.putString("emotion", selectedEmotion); // update bundle
        selectedButton = button;  // Keep track of the selected button
    }

    private int getButtonIndex(String emotion) {
        emotion = emotion.toLowerCase().trim();
        switch (emotion) {
            case "happy":
                return 0;
            case "calm":
                return 1;
            case "surprise":
                return 2;
            case "disgusted":
                return 3;
            case "angry":
                return 4;
            case "confused":
                return 5;
            case "terrified":
                return 6;
            case "no idea":
                return 7;
            case "ashamed":
                return 8;
            case "sad":
                return 9;
            case "anxious":
                return 10;
        }
        return -1;
    }
}
