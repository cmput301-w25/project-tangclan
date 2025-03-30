package com.example.tangclan;

import android.graphics.BitmapFactory;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

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
                        Toast.makeText(getApplicationContext(), "Follow request sent to "+ usernameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                        db.sendFollowRequest(loggedInUser.getUid(), otherUsersID, requestProcessed -> {
                        });
                        setFollowButtonText();
                    }
                });
            }
        });
    }

    public void setUpProfileDetails(Bundle bundle) {
        usernameTextView.setText(bundle.getString("username"));
        nameTextView.setText(bundle.getString("displayName"));

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
            ImageView pfp = findViewById(R.id.pfpView);
            byte[] decodedBytes = Base64.decode(pfpStr, Base64.DEFAULT);
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            pfp.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        }
    }

    public void setFollowButtonText() {
        loggedInUser.initializeFollowingBookFromDatabase(db);
        // Set button text depending on current relationship
        Button followBtn = findViewById(R.id.button_edit_profile);
        if (loggedInUser.getFollowingBook().getFollowers().contains(otherUsersID)) {
            followBtn.setText("  Following ");
        } else if (loggedInUser.getFollowingBook().getFollowRequests().contains(otherUsersID)) {
            followBtn.setText("   Pending   ");
        }
        else { followBtn.setText("   Follow   "); }
    }
}