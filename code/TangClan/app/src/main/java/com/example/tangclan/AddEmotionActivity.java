package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AddEmotionActivity extends AppCompatActivity {

    private String selectedEmotion = null;
    private ImageButton selectedButton = null;

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
        btnCancel.setOnClickListener(v -> finish());

        // Set up next button to navigate to AddSocialSituationActivity
        Button btnNext = findViewById(R.id.btnNextEmotion);
        btnNext.setOnClickListener(v -> {
            if (selectedEmotion == null) {
                Toast.makeText(this, "Please select an emotion", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a bundle to carry the selected emotion
            Bundle bundle = new Bundle();
            bundle.putString("selectedEmotion", selectedEmotion);  // Add selectedEmotion to the bundle

            // Create an intent to start the next activity
            Intent intent = new Intent(AddEmotionActivity.this, AddSocialSituationActivity.class);

            // Attach the bundle to the intent
            intent.putExtras(bundle);

            // Start the activity
            startActivity(intent);  // Start AddSocialSituationActivity
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
        selectedButton = button;  // Keep track of the selected button
    }
}
