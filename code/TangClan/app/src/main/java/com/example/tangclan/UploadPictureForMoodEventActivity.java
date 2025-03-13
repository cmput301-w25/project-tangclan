package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UploadPictureForMoodEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Make sure this enables the edge-to-edge UI
        setContentView(R.layout.fragment_upload_picture);  // Set the content to the correct XML layout

        // Apply window insets for the system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the Close button to finish the activity
        ImageView closeIcon = findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(v -> finish());  // Close the activity when clicked

        // Handle the Back button to navigate back to the previous activity
        Button btnBack = findViewById(R.id.btnBackEnvironment);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(v -> {
            uploadImage();
        });
    }


    private void uploadImage() {
        Intent intent = new Intent(UploadPictureForMoodEventActivity.this, ProfilePageActivity.class);
        startActivity(intent);
        finish();
    }
}
