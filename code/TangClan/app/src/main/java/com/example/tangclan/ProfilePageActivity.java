package com.example.tangclan;

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
    private ArrayAdapter<MoodEvent> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        usernameTextView = findViewById(R.id.username);
        nameTextView = findViewById(R.id.name);
        followersTextView = findViewById(R.id.followers);
        followingTextView = findViewById(R.id.following);
        profileArrayListView = findViewById(R.id.profile_array);


        databaseBestie = new DatabaseBestie();


        getCurrentUserProfile();


        processMoodEventData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getCurrentUserProfile();
    }

    private void getCurrentUserProfile() {

        userProfile = LoggedInUser.getInstance();

        usernameTextView.setText(userProfile.getUsername());
        nameTextView.setText(userProfile.getDisplayName());

        userProfile.initializeMoodEventBookFromDatabase(databaseBestie);

        setupProfileListView();


        databaseBestie.getFollowers(userProfile.getUid(), followers -> {
            runOnUiThread(() -> {
                followersTextView.setText(String.valueOf(followers.size()));
            });
        });

        databaseBestie.getFollowing(userProfile.getUid(), following -> {
            runOnUiThread(() -> {
                followingTextView.setText(String.valueOf(following.size()));
            });
        });
    }

    private void setupProfileListView() {
        if (userProfile != null && userProfile.getMoodEventBook() != null) {

            adapter = new ProfileHistoryAdapter(this, userProfile);


            profileArrayListView.setAdapter(adapter);


            ViewGroup.LayoutParams params = profileArrayListView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT; // Let it expand as needed
            profileArrayListView.setLayoutParams(params);
        }
    }

    private void processMoodEventData() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String selectedEmotion = bundle.getString("selectedEmotion");
            String selectedSituation = bundle.getString("selectedSituation");
            String reason = bundle.getString("reason");
            String imagePath = bundle.getString("imagePath");


            MoodEvent newMoodEvent;

            try {

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


                userProfile.post(newMoodEvent, databaseBestie);
                getCurrentUserProfile();



                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    setupProfileListView();
                }


                Toast.makeText(this, "Mood event added successfully!", Toast.LENGTH_SHORT).show();


                int count = userProfile.getMoodEventBook().getMoodEventList().size();
                Toast.makeText(this, "Total mood events: " + count, Toast.LENGTH_SHORT).show();

            } catch (IllegalArgumentException e) {
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

        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }
}
