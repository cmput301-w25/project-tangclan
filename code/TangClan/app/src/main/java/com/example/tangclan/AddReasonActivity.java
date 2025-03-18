package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddReasonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reason); // Link to the XML layout

        // Initialize the Cancel button and set its listener
        Button btnCancel = findViewById(R.id.btnCancelEmotion);
        btnCancel.setOnClickListener(v -> {
            // Handle cancel action (if necessary)
            finish();  // Finish the activity, returning to the previous screen
        });

        // Initialize the Next button and set its listener
        Button btnNext = findViewById(R.id.btnNextEmotion);
        btnNext.setOnClickListener(v -> {
            // Proceed to the next activity
            Toast.makeText(this, "Proceeding to the Add Social Situation", Toast.LENGTH_SHORT).show();

            // Create an Intent to navigate to AddSocialSituationActivity
            Intent intent = new Intent(AddReasonActivity.this, AddSocialSituationActivity.class);
            startActivity(intent);  // Start the new activity
            finish();  // Optionally finish the current activity to prevent the user from coming back to it
        });
    }
}
