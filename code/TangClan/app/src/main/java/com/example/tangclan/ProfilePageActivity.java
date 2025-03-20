package com.example.tangclan;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProfilePageActivity extends AppCompatActivity implements EditFragment.FragmentListener {

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

            // DELETE / EDIT / CANCEL operations on LongPress for Mood Events
            profileArrayListView.setOnItemLongClickListener((parent, view, position, id) -> {
                MoodEvent post = adapter.getItem(position); // Access from the data list
                String mid = post.getMid();

                String postDate = post.userFormattedDate();
                String month = postDate.substring(3);

                new AlertDialog.Builder(view.getContext())
                        .setMessage("Do you want to edit or delete this mood event?")
                        .setPositiveButton("Edit", (dialog, which) -> {
                            // Edit mood

                            String emotion = post.getMoodEmotionalState();

                            StringBuilder situation = new StringBuilder();
                            Optional<ArrayList<String>> collaborators = post.getCollaborators();
                            collaborators.ifPresent(list -> {
                                for (String item: list ) {
                                    situation.append(item);
                                    situation.append(",");
                                }
                            });

                            Optional<String> optReason = post.getReason();
                            String reason = optReason.orElse("");

                            // get Image bytes
                            Bitmap bitmap = post.getImage();
                            byte[] imageBytes;
                            if (bitmap != null) {
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                imageBytes = outputStream.toByteArray();
                            } else {
                                imageBytes = null;
                            }

                            boolean useLoc = false;

                            EditFragment form = EditFragment.newInstance(mid,  month, emotion, situation.toString(), reason, imageBytes, useLoc);
                            getSupportFragmentManager()
                                    .beginTransaction().add(R.id.edit_form_container, form).commit();

                            // userProfile.getMoodEventBook().updateMoodEvents();
                        })
                        .setNegativeButton("Delete", (dialog, which) -> {
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle("Are you sure you want to delete this mood event?")
                                    .setMessage("This action cannot be undone.")
                                    .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                        // Remove item from the data list, NOT the ListView itself
                                        adapter.remove(post);

                                        // delete from mood event book and database
                                        databaseBestie.getMoodEventByMid(post.getMid(),month, (event, emot) -> {
                                            userProfile.getMoodEventBook().deleteMoodEvent(event);
                                        });

                                        adapter.notifyDataSetChanged(); // Notify adapter of changes

                                        databaseBestie.deleteMoodEvent(post.getMid(), month);
                                        Toast.makeText(view.getContext(), "Mood Event Deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
                return true; // Indicate that the long press event is consumed
            });

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

    @Override
    public void onFragmentFinished() {
        System.out.println("FRagment done!");
        userProfile.getMoodEventBook().updateMoodEvents(); // update mood event book
        // update adapter
        String event_id, event_month;
        for (int i = 0; i < adapter.getCount(); i++) {
            int pos = i;
            MoodEvent event = adapter.getItem(i);
            event_id = event.getMid();
            event_month = event.userFormattedDate().substring(3);
            databaseBestie.getMoodEventByMid(event_id, event_month, (updatedEvent, emot) -> {
                adapter.remove(event);

                event.setCollaborators(updatedEvent.getCollaborators().orElse(new ArrayList<>()));
                event.setReason(updatedEvent.getReason().orElse(""));
                event.setImage(updatedEvent.getImage());
                event.setMood(emot);
                adapter.insert(event,pos);


            });
        }
        adapter.notifyDataSetChanged();

    }
}
