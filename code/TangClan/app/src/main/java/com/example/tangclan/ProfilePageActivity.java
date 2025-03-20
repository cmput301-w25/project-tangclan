package com.example.tangclan;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class ProfilePageActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private ListView profileArrayListView;
    private Profile userProfile;
    private DatabaseBestie databaseBestie;
    private ArrayAdapter<MoodEvent> adapter;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        NavBarHelper.setupNavBar(this);

        // Initialize views
        usernameTextView = findViewById(R.id.username);
        nameTextView = findViewById(R.id.name);
        followersTextView = findViewById(R.id.followers);
        followingTextView = findViewById(R.id.following);
        profileArrayListView = findViewById(R.id.mood_history_list);

        // Initialize database helper
        databaseBestie = new DatabaseBestie();


        // Get current user profile
        getCurrentUserProfile();
        setupProfileListView();


        // Process incoming mood event data if it exists
        processMoodEventData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list whenever the activity is resumed
        getCurrentUserProfile();
        setupProfileListView();
    }

    private void getCurrentUserProfile() {
        // This method should retrieve the current user's profile
        // For now, we'll create a dummy profile for testing
        userProfile = LoggedInUser.getInstance();

        // Initialize the mood event book if it doesn't exist
        // Set the user information in the UI
        usernameTextView.setText(userProfile.getUsername());
        nameTextView.setText(userProfile.getDisplayName());

        // Setup the ListView after profile is loaded
        setupProfileListView();
    }

    private void setupProfileListView() {
        if (userProfile != null && userProfile.getMoodEventBook() != null) {
            // Create a custom adapter using ProfileHistoryAdapter which has all the proper formatting
            adapter = new ProfileHistoryAdapter(this, userProfile);

            // Set the adapter on the ListView
            profileArrayListView.setAdapter(adapter);

            // Adjust ListView height if needed
            ViewGroup.LayoutParams params = profileArrayListView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT; // Let it expand as needed
            profileArrayListView.setLayoutParams(params);
        }
    }

    private void processMoodEventData() {
        // Retrieve the Bundle data passed from UploadPictureForMoodEventActivity
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String selectedEmotion = bundle.getString("selectedEmotion");
            ArrayList<String> selectedSituation = bundle.getStringArrayList("selectedSituation");
            String reason = bundle.getString("reason");
            String imagePath = bundle.getString("imagePath");

            // Create a new MoodEvent
            MoodEvent newMoodEvent;

            try {
                // Create the mood event based on available data
                if (selectedSituation != null && !selectedSituation.isEmpty()) {
                    newMoodEvent = new MoodEvent(selectedEmotion, selectedSituation);
                } else {
                    newMoodEvent = new MoodEvent(selectedEmotion);
                }

                // Set reason if available
                if (reason != null && !reason.isEmpty()) {
                    newMoodEvent.setReason(reason);
                }

                // Set image if available
                if (imagePath != null) {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        newMoodEvent.setImage(bitmap);
                    }
                }

                // Add the mood event to the user's mood event book
                userProfile.post(newMoodEvent, databaseBestie);

                // Save the updated profile to the database
                saveProfileToDatabase();

                // Force refresh the ListView by recreating the adapter
                setupProfileListView();

                // Show success message
                Toast.makeText(this, "Mood event added successfully!", Toast.LENGTH_SHORT).show();

                // Log the number of mood events for debugging
                int count = userProfile.getMoodEventBook().getMoodEventList().size();
                Toast.makeText(this, "Total mood events: " + count, Toast.LENGTH_SHORT).show();

            } catch (IllegalArgumentException e) {
                // Handle invalid input
                Toast.makeText(this, "Error creating mood event: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveProfileToDatabase() {
        // This method should save the updated profile to your database
        // For now, we'll just simulate successful saving
        // databaseBestie.saveUser(userProfile);
    }

    public void goToEditProfile(View view) {
        // Handle edit profile button click
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }

}