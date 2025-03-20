package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.ImageView;

import android.widget.TextView;

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
 */
public class ProfileActivity extends AppCompatActivity {
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

        usernameText = findViewById(R.id.username);
        NameText = findViewById(R.id.name);

        Bundle extras = getIntent().getExtras();//Got profile object from previous activity
        if (extras == null) {
            Log.e("profileActivity", "Extras are null");
            return;  // Or show an error message
        }

        Profile profile1 = (Profile) extras.get("Key1");
        if (profile1 == null) {
            Log.e("profileActivity", "Profile is null");
            return;  // Or show an error message
        }
        usernameText.setText(profile1.getUsername());
        NameText.setText(profile1.getDisplayName());
        Button EditProfileButton =(Button) findViewById(R.id.edit_profil_button);

        ImageButton addEmotionButton = findViewById(R.id.fabAdd);
        addEmotionButton.setOnClickListener(v -> {
            Log.d("profileActivity", "Add emotion button clicked");
            Intent intent = new Intent(ProfileActivity.this, AddEmotionActivity.class);
            startActivity(intent);
        });


        EditProfileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {



                Intent intent = new Intent(ProfileActivity.this, editprofileActivity.class);
                intent.putExtra("Key1",profile1);
                startActivity(intent);
            }


        });


        // instantiate the Profile's MoodEventBook
        DatabaseBestie databaseWrapper = new DatabaseBestie();
        profile1.initializeMoodEventBookFromDatabase(databaseWrapper);

        // set up the adapter to connect to the ListView
        ProfileHistoryAdapter profileHistoryAdapter = new ProfileHistoryAdapter(this, profile1);
        ListView moodHistoryList = findViewById(R.id.mood_history_list);

        // set the Adapter for the moodHistoryList
        moodHistoryList.setAdapter(profileHistoryAdapter);




        // NAVBAR
        ImageView pinIcon = findViewById(R.id.imgMap);
        ImageView homeIcon = findViewById(R.id.imgHome);
        ImageView searchIcon = findViewById(R.id.imgSearch);
        ImageView profileIcon = findViewById(R.id.imgProfile); // do nothing but change color to white

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, FeedActivity.class));
                finish();
            }
        });

    }



}