package com.example.tangclan;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

public class ViewOtherProfileActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private Button followBtn;

    private String otherUsersID;
    private LoggedInUser loggedInUser;
    private DatabaseBestie db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page_new);
        NavBarHelper.setupNavBar(this);

        db = new DatabaseBestie();

        usernameTextView = findViewById(R.id.username);
        nameTextView = findViewById(R.id.nameDisplay);
        followersTextView = findViewById(R.id.follower_count);
        followingTextView = findViewById(R.id.following_count);

        Bundle profileDetails = getIntent().getExtras();
        if (profileDetails != null) {
            setUpProfileDetails(profileDetails);
            otherUsersID = profileDetails.getString("uid");
        }


        // Get current users following book and initialize it
        loggedInUser = LoggedInUser.getInstance();
        loggedInUser.initializeFollowingBookFromDatabase(db);


        // Set button text depending on current relationship
        followBtn = findViewById(R.id.button_edit_profile);
        setFollowButtonText();

        // get current user's id if not yet
        if (loggedInUser.getUid()== null) {
            loggedInUser.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.checkExistingRequest(loggedInUser.getUid(), otherUsersID, reqExists -> {
                    if (!reqExists) {
                        Log.d("VIEWINGPROFILEACTIVITY","request sent");
                        Toast.makeText(getApplicationContext(), "Follow request sent!", Toast.LENGTH_SHORT).show();
                        db.sendFollowRequest(loggedInUser.getUid(), otherUsersID, requestProcessed -> {
                            loggedInUser.getFollowingBook().addMyRequest(otherUsersID);
                            setFollowButtonText();
                        });
                        setFollowButtonText();
                    }
                });
            }
        });
    }

    public void setUpListView(String username, String uid) {
        ListView moodEventsListView = findViewById(R.id.listview_profile_history);
        db = DatabaseBestie.getInstance();

        db.getAllMoodEvents(uid, events -> {
            Collections.reverse(events);
            ProfileHistoryAdapter adapter = new ProfileHistoryAdapter(this, events, username);
            moodEventsListView.setAdapter(adapter);
        });
    }

    public void setUpProfileDetails(Bundle bundle) {
        String otherUsersID = bundle.getString("uid");
        String username = bundle.getString("username");
        String displayName = bundle.getString("displayName");

        usernameTextView.setText(username);
        nameTextView.setText(displayName);

        // use db to get the follower and following count of a user
        db.getFollowers(otherUsersID, fllwers -> {
            followersTextView.setText(String.valueOf(fllwers.size()));
        });
        db.getFollowing(otherUsersID, fllwing -> {
            followingTextView.setText(String.valueOf(fllwing.size()));
        });

        // set up profile picture
        String pfpStr = bundle.getString("pfp");
        if (pfpStr != null) {
            Log.d("pfpstr", pfpStr);
            ImageView pfp = findViewById(R.id.pfpView);
            byte[] decodedBytes = Base64.decode(pfpStr, Base64.DEFAULT);
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            pfp.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        }

        setUpListView(username, otherUsersID);
    }

    public void setFollowButtonText() {
        // Set button text depending on current relationship
        Button followBtn = findViewById(R.id.button_edit_profile);
        if (loggedInUser.getFollowingBook().getFollowers().contains(otherUsersID)) {
            followBtn.setText("  Following ");
            followBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            followBtn.setTextColor(Color.parseColor("#ffffff"));
        } else if (loggedInUser.getFollowingBook().getMyFollowRequests().contains(otherUsersID)) {
            followBtn.setText("   Pending   ");
            followBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            followBtn.setTextColor(Color.parseColor("#ffffff"));
        }
        else { followBtn.setText("   Follow   "); }
    }
}