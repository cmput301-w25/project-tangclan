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
import com.example.tangclan.R;
import com.example.tangclan.RecoverActivity;
import com.example.tangclan.TempFeedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

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
                Intent signUp = new Intent(LogIn.this, SignUpActivity.class);
                startActivity(signUp);
                finish();
            }
        });

        TextView forgotPassText = findViewById((R.id.forgot_pass));
        forgotPassText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPass = new Intent(LogIn.this, RecoverActivity.class);
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
                username = editUsername.getText().toString();
                password = editPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    editUsername.setError("Enter username");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editPassword.setError("Enter password");
                    return;
                }

                // find corresponding email
                bestie.findEmailByUsername(username, (email) -> {
                    if (email == null) {
                        editUsername.setError("Cannot find an account with that username");
                    } else {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            bestie.updatePasswordSameAsFirebaseAuth(email,password);
                                            Intent intent = new Intent(getApplicationContext(), VerifyEmail.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(LogIn.this, "Authentication failed.",
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