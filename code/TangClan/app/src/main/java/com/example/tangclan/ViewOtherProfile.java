package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewOtherProfile extends AppCompatActivity {

    ImageView pfp;
    TextView usernameView, displayNameView, fllwr_ct, fllwing_ct;
    Button followButt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherprofile);

        pfp = findViewById(R.id.pfp_otherprofile);
        usernameView = findViewById(R.id.username_otherprofile);
        displayNameView = findViewById(R.id.displayname_otherprofile);
        followButt = findViewById(R.id.follow_status);
        fllwr_ct = findViewById(R.id.fllwr_count);
        fllwing_ct = findViewById(R.id.fllwing_count);

        Intent intent = getIntent();
        String usernameText = intent.getExtras().getString("username");
        String displayNameText = intent.getExtras().getString("display name");
        byte[] pfpImage = intent.getExtras().getByteArray("profile image");

        usernameView.setText(usernameText);
        displayNameView.setText(displayNameText);
        // turn pfpImage byte[] to pfpImage
        pfp.setImageBitmap(pfpImage);

        // set follow Button text depending on logged in users follow status

        // set follower/following count to however many :/
    }
}