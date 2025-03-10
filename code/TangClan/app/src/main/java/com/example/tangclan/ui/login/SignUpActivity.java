package com.example.tangclan.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tangclan.DatabaseBestie;
import com.example.tangclan.Profile;
import com.example.tangclan.R;
import com.example.tangclan.TempFeedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseBestie bestie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.page_signup);

        mAuth = FirebaseAuth.getInstance();
        bestie = DatabaseBestie.getInstance();

        TextView goToLogin = findViewById(R.id.already_hav);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logIn = new Intent(SignUpActivity.this, LogIn.class);
                startActivity(logIn);
            }
        });

        EditText editName = findViewById(R.id.full_name);
        EditText editEmail = findViewById(R.id.email_addre);
        EditText editUsername = findViewById(R.id.username);
        EditText editPassword = findViewById(R.id.password);
        EditText editConfirmedPassword = findViewById(R.id.confirm_pas);
        Button signUpButton = findViewById(R.id.button_signup);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, username;
                email = editEmail.getText().toString();
                username = editUsername.getText().toString();
                password = editPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    editEmail.setError("Enter email");
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    editUsername.setError("Enter username");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editPassword.setError("Enter password");
                    return;
                }
                boolean passwordsMatch = password.equals(editConfirmedPassword.getText().toString());
                if (!passwordsMatch) {
                    editConfirmedPassword.setError("Entered password does not match");
                    return;
                }
                bestie.findEmailByUsername(username, e -> {
                    if (e != null) {
                        editUsername.setError("Username already exists");
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            bestie.addUser(new Profile(username, password, email,-1));
                                            Toast.makeText(SignUpActivity.this, "Welcome to Moodly!",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), TempFeedActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Could not create account
                                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }
}