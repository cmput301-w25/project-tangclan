package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class AddSocialSituationActivity extends AppCompatActivity {

    private WizVIew wizVIew;  // Declare the ViewModel instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_social_situation);  // Make sure your layout is correct

        // Initialize your ViewModel using ViewModelProvider
        wizVIew = new ViewModelProvider(this).get(WizVIew.class);

        // Initialize your activity view
        EditText editTextSituation = findViewById(R.id.editTextSituation);

        // Top bar close icon
        ImageView closeIcon = findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(v -> finish());  // Close the activity when clicked

        // Bottom row buttons
        Button btnBack = findViewById(R.id.btnBackEnvironment);
        btnBack.setOnClickListener(v -> navigateBack());

        Button btnSave = findViewById(R.id.btnSaveEnvironment);
        btnSave.setOnClickListener(v -> {
            String situation = editTextSituation.getText().toString().trim();
            if (!TextUtils.isEmpty(situation)) {
                // Save the social situation into your ViewModel
                wizVIew.setSocialSituation(situation);
            }

            // Now create the MoodEvent
            createMoodEvent();

            // Navigate to the next activity after saving the data
            navigateToUploadPictureForm();
        });
    }

    private void navigateBack() {
        // Add logic to navigate back to the previous screen (if needed)
        finish();  // Simply finish the activity (return to the previous screen)
    }

    private void createMoodEvent() {
        // Use the ViewModel to get the data and create a MoodEvent
        WizVIew vm = wizVIew; // Retrieve from ViewModel

        // You might have a MoodEventBook in your Activity or a global store
        MoodEventBook moodEventBook = new MoodEventBook();

        try {
            // Build the MoodEvent based on which fields are set
            if (!vm.getTriggers().isEmpty() && vm.getSocialSituation() != null) {
                MoodEvent moodEvent = new MoodEvent(vm.getEmotionalState(), vm.getTriggers(), vm.getSocialSituation());
                moodEventBook.addMoodEvent(moodEvent);
            } else if (!vm.getTriggers().isEmpty()) {
                MoodEvent moodEvent = new MoodEvent(vm.getEmotionalState(), vm.getTriggers());
                moodEventBook.addMoodEvent(moodEvent);
            } else if (vm.getSocialSituation() != null) {
                MoodEvent moodEvent = new MoodEvent(vm.getEmotionalState(), vm.getSocialSituation());
                moodEventBook.addMoodEvent(moodEvent);
            } else {
                MoodEvent moodEvent = new MoodEvent(vm.getEmotionalState());
                moodEventBook.addMoodEvent(moodEvent);
            }
        } catch (IllegalArgumentException e) {
            // Show an error message if the mood event was invalid
            // (e.g., invalid emotional state or invalid social situation)
        }
    }

    private void navigateToUploadPictureForm() {
        // Navigate to the UploadPictureForMoodEventActivity
        Intent intent = new Intent(AddSocialSituationActivity.this, UploadPictureForMoodEventActivity.class);
        startActivity(intent);
        finish();  // Optionally finish the current activity to prevent returning to it
    }
}
