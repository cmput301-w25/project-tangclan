package com.example.tangclan;

import android.app.AlertDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
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
    private ArrayAdapter<MoodEvent> adapter;//
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        NavBarHelper.setupNavBar(this);

        networkManager = new NetworkManager(getApplicationContext());

        // Initialize views
        usernameTextView = findViewById(R.id.username);
        nameTextView = findViewById(R.id.name);
        followersTextView = findViewById(R.id.followers);
        followingTextView = findViewById(R.id.following);
        profileArrayListView = findViewById(R.id.mood_history_list);

        // Initialize database helper
        databaseBestie = new DatabaseBestie();

        // Process incoming mood event data if it exists
        getCurrentUserProfile();
        setupProfileListView();
        processMoodEventData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list whenever the activity is resumed
        networkManager.registerNetworkMonitor();
        getCurrentUserProfile();
        setupProfileListView();
    }

    @Override
    protected void onPause()  {
        networkManager.unregisterNetworkMonitor();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        networkManager.unregisterNetworkMonitor();
        super.onDestroy();

    }

    private void getCurrentUserProfile() {

        // Retrieve the current logged-in user profile using the Singleton instance
        userProfile = LoggedInUser.getInstance();

        // This method should retrieve the current user's profile

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
                            Bundle moodDetails = getMoodEventBundle(post);
                            EditFragment form = EditFragment.newInstance(moodDetails);
                            getSupportFragmentManager()
                                    .beginTransaction().add(R.id.edit_form_container, form).commit();

                        })
                        .setNegativeButton("Delete", (dialog, which) -> {
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle("Are you sure you want to delete this mood event?")
                                    .setMessage("This action cannot be undone.")
                                    .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                        // Remove item from the data list, NOT the ListView itself
                                        userProfile.getMoodEventBook().deleteMoodEvent(post);
                                        adapter.remove(post);

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
        if (getIntent().getExtras() != null) {
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
                    if (newMoodEvent.getMood() != null) {
                        userProfile.post(newMoodEvent, databaseBestie);
                    }

                    setupProfileListView();


                    // Show success message
                    Toast.makeText(this, "Mood event added successfully!", Toast.LENGTH_SHORT).show();

                    // Log the number of mood events for debugging
                    int count = userProfile.getMoodEventBook().getMoodEventList().size();

                } catch (IllegalArgumentException e) {
                    // Handle invalid input
                    Toast.makeText(this, "Error creating mood event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
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

    public Bundle getMoodEventBundle(MoodEvent post) {
        String mid = post.getMid();
        String month = post.userFormattedDate().substring(3);
        String emotion = post.getMoodEmotionalState();
        String collaborators = getStringOfCollaborators(post);
        String reason = post.getReason().orElse("");
        byte[] imgBytes = getImageBytes(post.getImage());
        boolean useLoc = false;  // TODO: implement location once MoodEvent has the field

        Bundle args = new Bundle();
        args.putString("mid", mid);
        args.putString("month", month);
        args.putString("emotion", emotion);
        args.putString("social situation", collaborators);
        args.putString("reason", reason);
        args.putByteArray("image", imgBytes);
        args.putBoolean("location permission", useLoc);

        return args;
    }

    public String getStringOfCollaborators(MoodEvent post) {
        StringBuilder collaboratorsStr = new StringBuilder();
        Optional<ArrayList<String>> collaborators = post.getCollaborators();
        collaborators.ifPresent(list -> {
            for (String item: list ) {
                collaboratorsStr.append(item);
                collaboratorsStr.append(",");
            }
        });
        return collaboratorsStr.toString();
    }

    public byte[] getImageBytes(Bitmap img) {
        byte[] imageBytes;
        if (img != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            imageBytes = outputStream.toByteArray();
        } else {
            imageBytes = null;
        }
        return imageBytes;
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
