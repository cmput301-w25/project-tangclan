package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tangclan.ui.login.LogIn;
import com.example.tangclan.ui.login.SignUpActivity;

public class LoginOrSignupActivity extends AppCompatActivity {

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
            startActivity(new Intent(LoginOrSignupActivity.this, LogIn.class));
            finish();
        });
    }
}