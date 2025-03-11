package com.example.tangclan.ui.login;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tangclan.FeedActivity;
import com.example.tangclan.R;
import com.example.tangclan.TempFeedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * US 3.01.01
 * Asks the user to verify their email, if their email is not yet verified.
 * Allows users to be directed to their Feed instead.
 */
public class VerifyEmail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    /**
     * Checks if the the user's email is already verified. If so, it skips to the feed.
     */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && user.isEmailVerified()){
            Log.d("VerifyEmail", "skipping verification");
            Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * If the user's email is not verified, it prompts them to send a verification link.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.verify_link_sent);
        Log.d("VerifyEmail", "we out here (verification step)");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();;

        TextView guideText = findViewById(R.id.a_verificat2);
        TextView continueText = findViewById(R.id.continue_text);
        TextView sendLinkText = findViewById(R.id.send_link);

        String currentUsersEmail = user.getEmail();

        guideText.setText("Please verify your email:" + currentUsersEmail);

        if (!user.isEmailVerified()) {
            continueText.setText("Continue");
        }

        continueText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // skip verification
                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sendLinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendEmailVerification();
                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}