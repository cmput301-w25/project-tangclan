package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.tangclan.ui.login.LogIn;
import com.example.tangclan.ui.login.SignUpActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class FeedActivity extends AppCompatActivity {
    //feed activitysssnn
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
        if(currentUser != null) {
            startActivity(new Intent(FeedActivity.this, LoginOrSignupActivity.class));
            finish();
        }
    }

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

        // NAVBAR
        ImageView pinIcon = findViewById(R.id.imgMap);
        ImageView homeIcon = findViewById(R.id.imgHome); // do nothing but change color to white
        ImageView searchIcon = findViewById(R.id.imgSearch);
        ImageView profileIcon = findViewById(R.id.imgProfile);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedActivity.this, profileActivity.class));
                finish();
            }
        });

    }

    private void loadFeed() {
        feed.loadFeed();
        List<MoodEvent> feedEvents = feed.getFeedEvents();

        adapter = new MoodEventAdapter((Context) this, feedEvents);
        listViewFeed.setAdapter(adapter);
    }

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
