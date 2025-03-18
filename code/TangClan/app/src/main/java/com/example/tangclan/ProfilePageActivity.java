package com.example.tangclan;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class ProfilePageActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private ListView profileArrayListView;
    private Profile userProfile;
    private DatabaseBestie databaseBestie;
    private ArrayAdapter<MoodEvent> adapter;

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
        profileArrayListView = findViewById(R.id.profile_array);

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
        getCurrentUserProfile();
        setupProfileListView();
    }

    private void getCurrentUserProfile() {
        // Retrieve the current logged-in user profile using the Singleton instance
        userProfile = LoggedInUser.getInstance();

        // Initialize the mood event book if it doesn't exist
        if (userProfile.getMoodEventBook() == null) {
            userProfile.setMoodEventBook(new MoodEventBook());
        }

        // Fetch the user's past mood events from the database
        initializeMoodEventBookFromDatabase();

        // Set the user information in the UI
        usernameTextView.setText(userProfile.getUsername());
        nameTextView.setText(userProfile.getDisplayName());
    }

    private void initializeMoodEventBookFromDatabase() {
        if (userProfile != null) {
            Log.d("ProfilePageActivity", "Fetching MoodEvents from database");
            userProfile.initializeMoodEventBookFromDatabase(databaseBestie);

            // Log the number of MoodEvents after fetching
            Log.d("ProfilePageActivity", "Number of MoodEvents: " + userProfile.getMoodEventBook().getMoodEventList().size());
        }
    }
    private void setupProfileListView() {
        if (userProfile != null && userProfile.getMoodEventBook() != null) {
            ArrayList<MoodEvent> moodEvents = (ArrayList<MoodEvent>) userProfile.getMoodEventBook().getMoodEventList();
            Log.d("ProfilePageActivity", "Setting up list view with " + moodEvents.size() + " events");

            // If you're reusing the adapter, update its data
            if (adapter != null) {
                adapter.clear();
                adapter.addAll(moodEvents);
                adapter.notifyDataSetChanged();
            } else {
                // Create a new adapter
                adapter = new ProfileHistoryAdapter(this, userProfile);
                profileArrayListView.setAdapter(adapter);
            }
        }
    }

    private void processMoodEventData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String selectedEmotion = bundle.getString("selectedEmotion");
            ArrayList<String> selectedSituation = bundle.getStringArrayList("selectedSituation");
            String reason = bundle.getString("reason");
            String imagePath = bundle.getString("imagePath");


            // Validate required fields
            if (selectedEmotion == null) {
                Toast.makeText(this, "Error: Emotion cannot be null", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                MoodEvent newMoodEvent;
                if (selectedSituation != null && !selectedSituation.isEmpty()) {
                    newMoodEvent = new MoodEvent(selectedEmotion, selectedSituation);
                } else {
                    newMoodEvent = new MoodEvent(selectedEmotion);
                }

                if (reason != null && !reason.isEmpty()) {
                    newMoodEvent.setReason(reason);
                }

                if (imagePath != null) {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        newMoodEvent.setImage(bitmap);
                    }
                }

                // Add the mood event to the user's mood event book
                userProfile.post(newMoodEvent, databaseBestie);
                setupProfileListView();

                // Save the updated profile to the database
                saveProfileToDatabase();

                // Notify the adapter of data changes
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                Toast.makeText(this, "Mood event added successfully!", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Error creating mood event: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveProfileToDatabase() {

    }

    public void goToEditProfile(View view) {
        // Handle edit profile button click
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }
}