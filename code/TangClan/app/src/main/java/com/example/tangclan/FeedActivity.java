package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

//part of US 01.01.01, US 01.04.01, US 01.05.01 and US 01.06.01

/**
 * The class is responsible for displaying the mood event feed to the user.
 * It allows users to view the most recent mood events from participants they follow,
 * add a new mood event, and view detailed information about any mood event in the feed.

 */

//TODO make sure this screen is updated after the addition of a mood event from the add emotion fragments

//TODO fix the bug for loadfeed because of the List<MoodEvent> to following book, cause runtime error

public class FeedActivity extends AppCompatActivity {
    private ListView listViewFeed;
    private Feed feed;
    private MoodEventAdapter adapter;
    /**
     * Initializes the activity, sets up the user interface, loads the mood event feed,
     * and configures event listeners for adding and viewing mood events.
     *
     * @param savedInstanceState The saved instance state from a previous session, if any.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed);

        listViewFeed = findViewById(R.id.listViewFeed);

        FollowingBook followingBook = new FollowingBook();
        MoodEventBook moodEventBook = new MoodEventBook();

        feed = new Feed(followingBook, moodEventBook);

        loadFeed();

        ImageButton addEmotionButton = findViewById(R.id.fabAdd);
        addEmotionButton.setOnClickListener(v -> {
            AddEmotionFragment addEmotionFragment = new AddEmotionFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, addEmotionFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        listViewFeed.setOnItemLongClickListener((parent, view, position, id) -> {
            MoodEvent moodEvent = feed.getFeedEvents().get(position);
            showMoodEventDetails(moodEvent);
            return true;
        });
    }

    /**
     * Loads the mood event feed and updates the list adapter.
     *
     */
    private void loadFeed() {
        feed.loadFeed();
        List<MoodEvent> feedEvents = feed.getFeedEvents();

        adapter = new MoodEventAdapter((Context) this, (FollowingBook) feedEvents);
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


        if (moodEvent.getTriggers().isPresent() && !moodEvent.getTriggers().isEmpty()) {
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
