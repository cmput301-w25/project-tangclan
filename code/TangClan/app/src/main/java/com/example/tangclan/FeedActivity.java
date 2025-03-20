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
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


//part of US 01.01.01, US 01.04.01, US 01.05.01 and US 01.06.01

/**
 * The class is responsible for displaying the mood event feed to the user.
 * It allows users to view the most recent mood events from participants they follow,
 * add a new mood event, and view detailed information about any mood event in the feed.

 */

//TODO make sure this screen is updated after the addition of a mood event from the add emotion fragments

//TODO fix the bug for loadfeed because of the List<MoodEvent> to following book, cause runtime error


/**
 * Represents the activity feed, with all MoodEvents of users that the session user follows
 * USER STORIES:
 *      US 01.04.01
 */

public class FeedActivity extends AppCompatActivity {
    //feed activitysssnn
    private ListView listViewFeed;
    private Feed feed;
    private MoodEventAdapter adapter;

    /**
     * Initializes the activity, sets up the user interface, loads the mood event feed,
     * and configures event listeners for adding and viewing mood events.
     *
     * @param savedInstanceState The saved instance state from a previous session, if any.
     */

    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if(currentUser == null) {
            startActivity(new Intent(FeedActivity.this, LoginOrSignupActivity.class));
            finish();
        }

        DatabaseBestie db = new DatabaseBestie();

        LoggedInUser loggedInUser = LoggedInUser.getInstance();

        db.getUser(currentUser.getUid(), user -> {
            loggedInUser.setEmail(user.getEmail());
            loggedInUser.setUsername(user.getUsername());
            loggedInUser.setPassword(user.getPassword());
            loggedInUser.setDisplayName(user.getDisplayName());
            loggedInUser.setAge(user.getAge());
            loggedInUser.setUid(currentUser.getUid());
            loggedInUser.initializeMoodEventBookFromDatabase(db);

            Log.d("FINALDEBUG", String.valueOf(loggedInUser.getMoodEventBook().getMoodEventCount()));
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_new);
        NavBarHelper.setupNavBar(this);

        listViewFeed = findViewById(R.id.listview_feed);

        FollowingBook followingBook = new FollowingBook();
        MoodEventBook moodEventBook = new MoodEventBook();

        feed = new Feed(followingBook, moodEventBook);

        loadFeed();

        ImageButton addEmotionButton = findViewById(R.id.fabAdd);
        addEmotionButton.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, AddEmotionActivity.class);
            startActivity(intent);
        });

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
            } //
        });

    }

    /**
     * Loads the mood event feed and updates the list adapter.
     *
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

        if (moodEvent.getReason().isPresent()) {
            details.append("Situation: ").append(moodEvent.getReason().get()).append("\n");
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