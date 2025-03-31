package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tangclan.ui.login.LogInActivity;
import com.example.tangclan.ui.login.SignUpActivity;

/**
 * US 3.01.01
 * Prompts the user to Log In or Sign Up
 */
public class LoginOrSignupActivity extends AppCompatActivity {

    /**
     * Directs users to their chosen activity (Log in or Sign up)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.page_login_signup);

        Button goToSignUp = findViewById(R.id.signup_button);
        goToSignUp.setOnClickListener(view -> {
            startActivity(new Intent(LoginOrSignupActivity.this, SignUpActivity.class));
            finish();
        });

        Button goToLogin = findViewById(R.id.login_button);
        goToLogin.setOnClickListener(view -> {
            startActivity(new Intent(LoginOrSignupActivity.this, LogInActivity.class));
            finish();
        });
    }
}