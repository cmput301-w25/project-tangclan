package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private ListView listViewFeed;
    private Feed feed;
    private MoodEventAdapter adapter;

    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // If no user is logged in, redirect to the login/signup activity
            startActivity(new Intent(FeedActivity.this, LoginOrSignupActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed);
        NavBarHelper.setupNavBar(this);

        // Logout button
        Button logout = findViewById(R.id.logout_butt);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                // Clear the LoggedInUser instance on logout
                LoggedInUser loggedInUser = LoggedInUser.getInstance();
                LoggedInUser.resetInstance();

                // Redirect to the login/signup activity
                Intent intent = new Intent(FeedActivity.this, LoginOrSignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize the ListView
        listViewFeed = findViewById(R.id.listViewFeed);

        // Retrieve the LoggedInUser instance
        LoggedInUser loggedInUser = LoggedInUser.getInstance();

        // Ensure the LoggedInUser instance is properly populated
        if (loggedInUser.getUsername() == null) {
            // If the LoggedInUser instance is not populated, redirect to login
            startActivity(new Intent(FeedActivity.this, LoginOrSignupActivity.class));
            finish();
            return;
        }

        // Get the user's FollowingBook and MoodEventBook from the LoggedInUser instance
        FollowingBook followingBook = loggedInUser.getFollowingBook();
        MoodEventBook moodEventBook = loggedInUser.getMoodEventBook();

        // Initialize the Feed with the user's FollowingBook and MoodEventBook
        feed = new Feed(followingBook, moodEventBook);

        // Load the feed
        loadFeed();

        // Add Emotion button
        ImageButton addEmotionButton = findViewById(R.id.fabAdd);
        addEmotionButton.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, AddEmotionActivity.class);
            startActivity(intent);
        });

        // Long click listener for mood event details
        listViewFeed.setOnItemLongClickListener((parent, view, position, id) -> {
            MoodEvent moodEvent = feed.getFeedEvents().get(position);
            showMoodEventDetails(moodEvent);
            return true;
        });

        // NAVBAR
        ImageView pinIcon = findViewById(R.id.imgMap);
        ImageView homeIcon = findViewById(R.id.imgHome); // do nothing but change color to white
        ImageView searchIcon = findViewById(R.id.imgSearch);
        ImageView profileIcon = findViewById(R.id.imgProfile);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedActivity.this, ProfilePageActivity.class));
                finish();
            }
        });
    }

    /**
     * Loads the mood event feed and updates the list adapter.
     */
    private void loadFeed() {
        feed.loadFeed();
        FollowingBook followingBook = feed.getFollowingBook();  // Assuming you have this getter in Feed class.

        adapter = new MoodEventAdapter(this, followingBook);  // Pass followingBook instead of feedEvents
        listViewFeed.setAdapter(adapter);
    }

    /**
     * Displays the details of a selected mood event in an alert dialog.
     *
     * @param moodEvent The mood event whose details are to be displayed.
     */
    private void showMoodEventDetails(MoodEvent moodEvent) {
        StringBuilder details = new StringBuilder();
        details.append("Emotional State: ").append(moodEvent.getMoodEmotionalState()).append("\n");
        details.append("Mood Color: ").append(moodEvent.getMood().getColor(getBaseContext()).toString()).append("\n");
        details.append("Emoticon: ").append(moodEvent.getMoodEmotionalState()).append("emote\n");

        if (moodEvent.getTriggers().isPresent() && moodEvent.getTriggers().isPresent()) {
            details.append("Triggers: ").append(String.join(", ", moodEvent.getTriggers().get())).append("\n");
        } else {
            details.append("Triggers: N/A\n");
        }

        if (moodEvent.getSituation().isPresent()) {
            details.append("Situation: ").append(moodEvent.getSituation()).append("\n");
        } else {
            details.append("Situation: N/A\n");
        }

        if (moodEvent.hasGeolocation()) {
            details.append("Location: Lat: ").append(moodEvent.getLatitude()).append(", Lon: ").append(moodEvent.getLongitude()).append("\n");
        } else {
            details.append("Location: N/A\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Mood Event Details")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }
}