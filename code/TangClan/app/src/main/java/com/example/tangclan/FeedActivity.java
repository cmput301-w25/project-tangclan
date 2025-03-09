package com.example.tangclan;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;


import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private ListView listViewFeed;
    private Feed feed;
    private FollowingBook followingBook;
    private MoodEventBook moodEventBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed);

        listViewFeed = findViewById(R.id.listViewFeed);

        followingBook = new FollowingBook();
        moodEventBook = new MoodEventBook();

        feed = new Feed(followingBook);

        loadFeed();

        ImageButton addEmotionButton = findViewById(R.id.fabAdd);
        addEmotionButton.setOnClickListener(v -> {
            AddEmotionFragment addEmotionFragment = new AddEmotionFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, addEmotionFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Set OnItemLongClickListener to display MoodEvent details
        listViewFeed.setOnItemLongClickListener((parent, view, position, id) -> {
            MoodEvent moodEvent = feed.getFeedEvents().get(position);
            showMoodEventDetails(moodEvent);
            return true;
        });
    }

    private void loadFeed() {
        // Load the current user's mood events first
        feed.loadFeed();
        List<MoodEvent> feedEvents = feed.getFeedEvents();

        // Load the recent mood events from the people the user follows
        List<MoodEvent> followedUserEvents = followingBook.getRecentMoodEvents();
        feedEvents.addAll(followedUserEvents);

        // Use the custom MoodEventAdapter instead of the ArrayAdapter
        MoodEventAdapter adapter = new MoodEventAdapter(this, feedEvents);
        listViewFeed.setAdapter(adapter);
    }

    private void showMoodEventDetails(MoodEvent moodEvent) {
        StringBuilder details = new StringBuilder();
        details.append("Emotional State: ").append(moodEvent.getMoodEmotionalState()).append("\n");
        details.append("Mood Color: ").append(moodEvent.getMoodColor()).append("\n");
        details.append("Emoticon: ").append(moodEvent.getMoodEmoticon()).append("\n");

        if (moodEvent.getTriggers() != null && !moodEvent.getTriggers().isEmpty()) {
            details.append("Triggers: ").append(String.join(", ", moodEvent.getTriggers())).append("\n");
        } else {
            details.append("Triggers: N/A\n");
        }

        if (moodEvent.getSituation() != null) {
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