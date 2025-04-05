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
import com.example.tangclan.LoggedInUser;
import com.example.tangclan.R;
import com.example.tangclan.RecoverActivity;
import com.example.tangclan.SetupBirthdayActivity;
import com.example.tangclan.TempFeedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * US 3.01.01
 * Shows the Login Form. Checks the username and password fields to allow user to Log In.
 */
public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.page_login);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseBestie bestie = DatabaseBestie.getInstance();

        TextView goToSignUp = findViewById(R.id.don_t_have_);
        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp = new Intent(LogInActivity.this, SetupBirthdayActivity.class);
                startActivity(signUp);
                finish();
            }
        });

        TextView forgotPassText = findViewById((R.id.forgot_pass));
        forgotPassText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPass = new Intent(LogInActivity.this, RecoverActivity.class);
                startActivity(forgotPass);
                finish();
            }
        });

        EditText editUsername = findViewById(R.id.username);
        EditText editPassword = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, password;
                username = editUsername.getText().toString().toLowerCase();
                password = editPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    editUsername.setError("Enter username");
                    return;
                }
                //TO DO: Restrict username to be at least 4 characters, no spaces, and only allow certain special characters
                if (TextUtils.isEmpty(password)) {
                    editPassword.setError("Enter password");
                    return;
                }
                System.out.println("we made it here");

                // Find Corresponding email to Log In
                bestie.findProfileByUsername(username, (profile) -> {
                    System.out.println("and here");
                    if (profile == null) {
                        editUsername.setError("Cannot find an account with that username");
                    } else {
                        mAuth.signInWithEmailAndPassword(profile.getEmail(), password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            if (!password.equals(profile.getPassword())) {
                                                bestie.updatePasswordSameAsFirebaseAuth(profile.getEmail(), password);
                                            }
                                            LoggedInUser loggedInUser = LoggedInUser.getInstance();
                                            loggedInUser.setUid(profile.getUid());
                                            loggedInUser.setEmail(profile.getEmail());
                                            loggedInUser.setUsername(profile.getUsername());
                                            loggedInUser.setPassword(profile.getPassword());
                                            loggedInUser.setDisplayName(profile.getDisplayName());
                                            loggedInUser.setAge(profile.getAge());
                                            loggedInUser.setProfilePic(profile.getProfilePic());

                                            Intent intent = new Intent(getApplicationContext(), VerifyEmailActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        if (mAuth.getCurrentUser() == null) {
                            editPassword.setError("Wrong Password");
                        }
                    }
                });
            }
        });
    }
}