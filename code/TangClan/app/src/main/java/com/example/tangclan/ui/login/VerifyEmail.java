package com.example.tangclan.ui.login;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tangclan.R;
import com.example.tangclan.TempFeedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && user.isEmailVerified()){
            Intent intent = new Intent(getApplicationContext(), TempFeedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.verify_link_sent);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();;

        TextView guideText = findViewById(R.id.a_verificat2);
        TextView continueText = findViewById(R.id.continue_text);
        TextView sendLinkText = findViewById(R.id.send_link);

        String currentUsersEmail = user.getEmail();

        guideText.setText("A verification link was sent to" + currentUsersEmail);

        if (!user.isEmailVerified()) {
            continueText.setText("Continue");
        }

        continueText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TempFeedActivity.class);
                startActivity(intent);
            }
        });

        sendLinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendEmailVerification();
            }
        });
    }
}