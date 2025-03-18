package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;

public class AddSocialSituationActivity extends AppCompatActivity {

    private WizVIew wizVIew;  // ViewModel instance
    private String selectedEmotion;  // Store the passed emotion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_social_situation);

        // Retrieve the selected emotion from the previous activity using the Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedEmotion = extras.getString("selectedEmotion");
        }

        // Initialize ViewModel
        wizVIew = new ViewModelProvider(this).get(WizVIew.class);

        // Initialize UI elements
        EditText editTextSituation = findViewById(R.id.editTextSituation);
        ImageView closeIcon = findViewById(R.id.closeIcon);
        Button btnBack = findViewById(R.id.btnBackEnvironment);
        Button btnSave = findViewById(R.id.btnSaveEnvironment);

        // Close the activity when clicking the close icon
        closeIcon.setOnClickListener(v -> finish());

        // Navigate back
        btnBack.setOnClickListener(v -> finish());

        // Save and navigate forward
        btnSave.setOnClickListener(v -> {
            String[] socialSituationList = editTextSituation.getText().toString().trim().split(",");
            ArrayList<String> situation = new ArrayList<>(Arrays.asList(socialSituationList));

            if (situation.isEmpty()) {
                Toast.makeText(this, "Please enter a social situation", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the social situation in the ViewModel
            wizVIew.setSocialSituation(situation);


            // Create an intent to start the next activity
            Intent intent = new Intent(AddSocialSituationActivity.this, UploadPictureForMoodEventActivity.class);

            // Attach the bundle to the intent
            intent.putExtra("selectedEmotion", selectedEmotion);
            intent.putStringArrayListExtra("selectedSituation", situation);

            // Start the activity
            startActivity(intent);  // Start UploadPictureForMoodEventActivity
            finish();  // Close current activity
        });
    }
}
