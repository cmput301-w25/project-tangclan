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
import com.example.tangclan.FeedActivity;
import com.example.tangclan.LoggedInUser;
import com.example.tangclan.MainActivity;
import com.example.tangclan.Profile;
import com.example.tangclan.R;
import com.example.tangclan.TempFeedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * US 3.01.01
 * Shows the Sign Up Form.
 */
public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser currentUser;

    /**
     *  Checks the user detail fields for valid input. Creates an account when entered fields are all validated.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.page_signup);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseBestie bestie = DatabaseBestie.getInstance();

        TextView goToLogin = findViewById(R.id.already_hav);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logIn = new Intent(SignUpActivity.this, LogIn.class);
                startActivity(logIn);
                finish();
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
                String displayName, email, password, username;
                displayName = editName.getText().toString();
                email = editEmail.getText().toString();
                username = editUsername.getText().toString();
                password = editPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    editEmail.setError("Enter email");
                    return;
                }
                if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    editEmail.setError("Wrong format");
                    return;
                }

                if (TextUtils.isEmpty(username)) {
                    editUsername.setError("Enter username");
                    return;
                }
                if (!username.matches("^[a-z0-9_-]{4,}$")) {
                    editUsername.setError("Username must be at least 4 characters long and can only contain:\n" +
                            " - alphanumeric characters\n" +
                            " - hyphens\n" +
                            " - underscores\n");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editPassword.setError("Enter password");
                    return;
                }
                if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                    editPassword.setError("Password must be at least 8 characters long and must contain one of each:\n" +
                            " - Capital letter\n" +
                            " - Lowercase letter\n" +
                            " - One of the special characters: !,#,$,%,^,&,*,|\n");
                    return;
                }

                boolean passwordsMatch = password.equals(editConfirmedPassword.getText().toString());
                if (!passwordsMatch) {
                    editConfirmedPassword.setError("Entered password does not match");
                    return;
                }

                bestie.findEmailByUsername(username, em -> {
                    if (em != null) {
                        editUsername.setError("Username already exists");
                    } else {
                        bestie.checkEmailExists(email, ema -> {
                            if (ema != null) {
                                editEmail.setError("An account with this email already exists");
                            } else {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    bestie.addUser(uid, new Profile(displayName, username, password, email, null));  // replace null with default image

                                                    LoggedInUser loggedInUser = LoggedInUser.getInstance();
                                                    loggedInUser.setUid(uid);
                                                    loggedInUser.setEmail(email);
                                                    loggedInUser.setUsername(username);
                                                    loggedInUser.setPassword(password);
                                                    loggedInUser.setDisplayName(displayName);

                                                    Toast.makeText(SignUpActivity.this, "Welcome to Moodly!",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), VerifyEmail.class);
                                                    startActivity(intent);
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
        });
    }


    /*@Override

    /*
    @Override

    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if(currentUser != null) {
            startActivity(new Intent(SignUpActivity.this, FeedActivity.class));
            finish();
        }
    }*/


    /**
     *  Starts the Profile Setup sequence for a new user
     */

    /*@Override

    /*
    @Override

    protected void onResume() {
        super.onResume();
        // After user is asked to verify their account
        Intent intent = new Intent(SignUpActivity.this, FeedActivity.class); // Change to account Setup
        startActivity(intent);
        finish();

    }*/

}
