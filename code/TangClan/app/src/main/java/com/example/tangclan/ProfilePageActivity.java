package com.example.tangclan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tangclan.R;

import java.io.File;

public class ProfilePageActivity extends AppCompatActivity {

    private TextView emotionTextView;
    private TextView situationTextView;
    private TextView reasonTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private ImageView moodImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_mood_event); // Ensure this matches your layout XML file

        // Initialize views
        emotionTextView = findViewById(R.id.username_emotional_state);
        situationTextView = findViewById(R.id.situation);
        reasonTextView = findViewById(R.id.reason);
        dateTextView = findViewById(R.id.date_text);
        timeTextView = findViewById(R.id.time_text);
        moodImageView = findViewById(R.id.mood_event_image);

        // Retrieve data passed from UploadPictureForMoodEventActivity
        Intent intent = getIntent();
        String selectedEmotion = intent.getStringExtra("selectedEmotion");
        String selectedSituation = intent.getStringExtra("selectedSituation");
        String reason = intent.getStringExtra("reason");
        String imagePath = intent.getStringExtra("imagePath");
        String date = intent.getStringExtra("date"); // Assuming you pass the date string
        String time = intent.getStringExtra("time"); // Assuming you pass the time string

        // Display the data
        if (selectedEmotion != null) {
            emotionTextView.setText("Emotion: " + selectedEmotion);
        }
        if (selectedSituation != null) {
            situationTextView.setText("Situation: " + selectedSituation);
        }
        if (reason != null) {
            reasonTextView.setText("Reason: " + reason);
        }
        if (date != null) {
            dateTextView.setText("Date: " + date);
        }
        if (time != null) {
            timeTextView.setText("Time: " + time);
        }

        // Display the image if available
        if (imagePath != null) {
            // Load the image from the provided path (assuming it's a local file path or URI)
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                moodImageView.setImageBitmap(bitmap);
            }
        }
    }
}
