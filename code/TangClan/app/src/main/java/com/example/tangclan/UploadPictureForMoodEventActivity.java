package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class UploadPictureForMoodEventActivity extends AppCompatActivity {

    private String selectedEmotion;
    private String selectedSituation;
    private EditText editTextReason;
    private String imagePath = null; // Optional image path (null if not uploaded)

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

        // Retrieve passed data from AddSocialSituationActivity
        selectedEmotion = getIntent().getStringExtra("selectedEmotion");
        selectedSituation = getIntent().getStringExtra("selectedSituation");

        // Initialize UI components
        // User writes a reason for the mood event
        ImageView closeIcon = findViewById(R.id.closeIcon);
        Button btnBack = findViewById(R.id.btnBackEnvironment);
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        Button btnNext = findViewById(R.id.buttonSaveText);
        TextInputEditText editTextReason = findViewById(R.id.text203).findViewById(R.id.editTextReason);


        // Close button action
        closeIcon.setOnClickListener(v -> finish());

        // Back button action
        btnBack.setOnClickListener(v -> finish());

        // Upload image button (Optional)
        btnUploadImage.setOnClickListener(v -> selectImage());

        // Proceed to ProfilePageActivity
        btnNext.setOnClickListener(v -> {
            String reason = editTextReason.getText().toString().trim();

            if (TextUtils.isEmpty(reason)) {
                Toast.makeText(this, "Please enter a reason for your mood event", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass all collected data to ProfilePageActivity
            Intent intent = new Intent(UploadPictureForMoodEventActivity.this, ProfilePageActivity.class);
            intent.putExtra("selectedEmotion", selectedEmotion);
            intent.putExtra("selectedSituation", selectedSituation);
            intent.putExtra("reason", reason);
            if (imagePath != null) {
                intent.putExtra("imagePath", imagePath);
            }
            startActivity(intent);
            finish();  // Close the current activity
        });
    }
    //

    // This function simulates selecting an image (You need to implement actual image selection logic)
    private void selectImage() {
        // Example: Set a dummy image path
        imagePath = "/storage/emulated/0/Pictures/mood_event.jpg";
        Toast.makeText(this, "Image selected!", Toast.LENGTH_SHORT).show();
    }
}
