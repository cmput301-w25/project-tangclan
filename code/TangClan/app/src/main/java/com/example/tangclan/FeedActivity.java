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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private ListView listViewFeed;
    private Feed feed;
    private MoodEventAdapter adapter;
    private TextView followingTab;
    private TextView forYouTab;
    private View rectangleFollowing;
    private View rectangleForYou;

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

        // Initialize tab views for feed switching
        followingTab = findViewById(R.id.following);
        forYouTab = findViewById(R.id.for_you);
        rectangleFollowing = findViewById(R.id.rectangle_1);
        rectangleForYou = findViewById(R.id.rectangle_2);

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

        // Set up tab click listeners
        setupTabListeners();

        // Load the following feed by default (3 most recent events)
        loadFollowingFeed();

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
     * Sets up tab listeners for switching between Following and For You feeds
     */
    private void setupTabListeners() {
        rectangleFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFollowingFeed();
                // Update UI to show Following tab is active
                rectangleFollowing.setBackgroundResource(R.drawable.rectangle_1); // Active style
                rectangleForYou.setBackgroundResource(R.drawable.rectangle_2); // Inactive style
            }
        });

        rectangleForYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFullFeed();
                // Update UI to show For You tab is active
                rectangleFollowing.setBackgroundResource(R.drawable.rectangle_2); // Inactive style
                rectangleForYou.setBackgroundResource(R.drawable.rectangle_1); // Active style
            }
        });
    }

    /**
     * Loads only the 3 most recent mood events from followed users
     */
    private void loadFollowingFeed() {
        // Load just the 3 most recent events from followed users
        feed.loadRecentFollowingFeed();

        if (feed.getFeedEvents().isEmpty()) {
            Toast.makeText(this, "No recent mood events from people you follow", Toast.LENGTH_SHORT).show();
        }

        adapter = new MoodEventAdapter(this, feed.getFeedEvents());
        listViewFeed.setAdapter(adapter);
    }

    /**
     * Loads the full mood event feed
     */
    private void loadFullFeed() {
        feed.loadFeed();
        adapter = new MoodEventAdapter(this, feed.getFeedEvents());
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

        if (moodEvent.getTriggers().isPresent() && !moodEvent.getTriggers().get().isEmpty()) {
            details.append("Triggers: ").append(String.join(", ", moodEvent.getTriggers().get())).append("\n");
        } else {
            details.append("Triggers: N/A\n");
        }

        if (moodEvent.getSituation().isPresent()) {
            details.append("Situation: ").append(moodEvent.getSituation().get()).append("\n");
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

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the feed when coming back to this activity
        loadFollowingFeed();
    }
}