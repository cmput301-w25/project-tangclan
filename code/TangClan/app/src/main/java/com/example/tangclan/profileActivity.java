package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executors;

public class profileActivity extends AppCompatActivity {
    TextView usernameText;
    TextView NameText;
    TextView FollowersText;
    TextView FollowingText;

    // we get the Profile from an intent from another activity
    private Profile profile1;
    private ProfileHistoryAdapter profileHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameText = findViewById(R.id.username);
        NameText = findViewById(R.id.name);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("profileActivity", "Extras are null");
            Toast.makeText(this, "Error: No data received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        profile1 = (Profile) extras.get("Key1");
        if (profile1 == null) {
            Log.e("profileActivity", "Profile is null");
            Toast.makeText(this, "Error: Invalid profile data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        usernameText.setText(profile1.getUsername());
        NameText.setText(profile1.getDisplayName());

        MoodEvent moodEvent = (MoodEvent) getIntent().getSerializableExtra("moodEvent");
        if (moodEvent != null) {
            profile1.getMoodEventBook().addMoodEvent(moodEvent);
            Log.d("profileActivity", "MoodEvent added: " + moodEvent.toString());
        } else {
            Log.d("profileActivity", "No MoodEvent received");
        }

        Button EditProfileButton = findViewById(R.id.edit_profil_button);
        EditProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(profileActivity.this, editprofileActivity.class);
            intent.putExtra("Key1", profile1);
            startActivity(intent);
        });

        ImageButton addEmotionButton = findViewById(R.id.fabAdd);
        addEmotionButton.setOnClickListener(v -> {
            Log.d("profileActivity", "Add emotion button clicked");
            startActivity(new Intent(profileActivity.this, WizActivity.class));
        });

        // Initialize database and adapter
        DatabaseBestie databaseWrapper = new DatabaseBestie();
        Executors.newSingleThreadExecutor().execute(() -> {
            profile1.initializeMoodEventBookFromDatabase(databaseWrapper);
            runOnUiThread(() -> {
                profileHistoryAdapter = new ProfileHistoryAdapter(this, profile1);
                ListView moodHistoryList = findViewById(R.id.profile_array);
                if (moodHistoryList != null) {
                    moodHistoryList.setAdapter(profileHistoryAdapter);
                } else {
                    Log.e("profileActivity", "ListView not found");
                }
            });
        });

        // NAVBAR setup
        ImageView homeIcon = findViewById(R.id.imgHome);
        homeIcon.setOnClickListener(view -> {
            startActivity(new Intent(profileActivity.this, FeedActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when the activity resumes
        profileHistoryAdapter.notifyDataSetChanged();
    }
}