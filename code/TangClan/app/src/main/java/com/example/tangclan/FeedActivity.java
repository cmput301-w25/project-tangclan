package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class FeedActivity extends AppCompatActivity {
    //feed activitysss
    private ListView listViewFeed;
    private Feed feed;

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

    private void loadFeed() {
        feed.loadFeed();
        List<MoodEvent> feedEvents = feed.getFeedEvents();

        MoodEventAdapter adapter = new MoodEventAdapter((Context) this, (FollowingBook) feedEvents);
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
