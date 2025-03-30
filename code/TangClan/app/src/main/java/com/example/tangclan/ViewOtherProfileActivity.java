package com.example.tangclan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewOtherProfileActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private Button followBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page_new);
        NavBarHelper.setupNavBar(this);

        Bundle profileDetails = getIntent().getExtras();

        usernameTextView = findViewById(R.id.username);
        nameTextView = findViewById(R.id.nameDisplay);
        followersTextView = findViewById(R.id.follower_count);
        followingTextView = findViewById(R.id.following_count);

        if (profileDetails != null) {
            setUpProfileDetails(profileDetails);
        }

        // Get current users following book and initialize it

        Button followBtn = findViewById(R.id.button_edit_profile);
        // If user hasn't requested to follow set text to follow
        // If the user has requested to follow, set text to pending
        // if user is following, set text to following, show user's posts
        followBtn.setText("    Follow   ");

        // use db to get the follower and following count of a user
        // set the counts



    }

    public void setUpProfileDetails(Bundle bundle) {
        usernameTextView.setText(bundle.getString("username"));
        nameTextView.setText(bundle.getString("displayName"));

        // set up profile picture
    }
}