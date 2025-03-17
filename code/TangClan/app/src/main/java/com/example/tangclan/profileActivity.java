package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity class for the profile view with mood history
 * RELATED USER STORIES:
 *      US 01.04.01
 *      US 03.01.01
 *      US 01.05.01
 *              TODO: connect to fragment to be able to edit mood details in history
 *              TODO: set a mechanism for updating on database
 *      US 1.06.01
 *              TODO: longclick mechanism to be able to delete mood event
 *              TODO: remove from database
 *
 *      TODO: handle collaborators
 */
public class profileActivity extends AppCompatActivity {
    TextView usernameText;
    TextView NameText;
    TextView FollowersText;
    TextView FollowingText;

    // we get the Profile from an intent from another activity

    //Profile profile= new Profile("Person1", "aBcd123*","shaian@ualberta.ca","21");


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
        LoggedInUser loggedInUser = LoggedInUser.getInstance();

        usernameText = findViewById(R.id.username);
        NameText = findViewById(R.id.name);

        /* Commenting this out and all calls using Bundle and using the global instance of
        Logged In user instead -Alissa

        Bundle extras = getIntent().getExtras();//Got profile object from previous activity
        assert extras != null;
        Profile profile1= (Profile) extras.get("Key1");
        */

        // assert profile1 != null;
        // usernameText.setText(profile1.getUsername());
        // NameText.setText(profile1.getDisplayName());

        usernameText.setText(loggedInUser.getUsername());
        NameText.setText(loggedInUser.getDisplayName());
        Button EditProfileButton =(Button) findViewById(R.id.edit_profil_button);

        EditProfileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profileActivity.this, editprofileActivity.class);
                // intent.putExtra("Key1",profile1);
                startActivity(intent);
            }
        });


        // instantiate the Profile's MoodEventBook
        DatabaseBestie databaseWrapper = new DatabaseBestie();
        // profile1.initializeMoodEventBookFromDatabase(databaseWrapper);

        loggedInUser.initializeMoodEventBookFromDatabase(databaseWrapper);

        // set up the adapter to connect to the ListView
        ProfileHistoryAdapter profileHistoryAdapter = new ProfileHistoryAdapter(this, loggedInUser);
        ListView moodHistoryList = findViewById(R.id.profile_array);

        // set the Adapter for the moodHistoryList
        moodHistoryList.setAdapter(profileHistoryAdapter);

        // Get the underlying list from the logged-in user's MoodEventBook
        ArrayList<MoodEvent> moodHistoryData = loggedInUser.getMoodEventBook().getAllMoodEvents();


        // DELETE / EDIT / CANCEL operations on LongPress for Mood Events
        moodHistoryList.setOnItemLongClickListener((parent, view, position, id) -> {
            MoodEvent post = moodHistoryData.get(position); // Access from the data list

            new AlertDialog.Builder(view.getContext())
                    .setMessage("Do you want to edit or delete this mood event?")
                    .setPositiveButton("Edit", (dialog, which) -> {
                        // Open EditMoodEvent fragment (your teammates handle this)
                    })
                    .setNegativeButton("Delete", (dialog, which) -> {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Are you sure you want to delete this mood event?")
                                .setMessage("This action cannot be undone.")
                                .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                    // Remove item from the data list, NOT the ListView itself
                                    moodHistoryData.remove(position);
                                    profileHistoryAdapter.notifyDataSetChanged(); // Notify adapter of changes
                                    Toast.makeText(view.getContext(), "Mood Event Deleted", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("No", null)
                                .show();
                    })
                    .setNeutralButton("Cancel", null)
                    .show();

            return true; // Indicate that the long press event is consumed
        });

        // NAVBAR
        ImageView pinIcon = findViewById(R.id.imgMap);
        ImageView homeIcon = findViewById(R.id.imgHome);
        ImageView searchIcon = findViewById(R.id.imgSearch);
        ImageView profileIcon = findViewById(R.id.imgProfile); // do nothing but change color to white

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profileActivity.this, FeedActivity.class));
                finish();
            }
        });
    }
}