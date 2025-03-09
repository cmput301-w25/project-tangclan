package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tangclan.ui.login.LogIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverActivity extends AppCompatActivity {

    private static final String TAG = "RecoverActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseAuth mAuth;
        DatabaseBestie bestie;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forgot_password);
        bestie = DatabaseBestie.getInstance();

        mAuth = FirebaseAuth.getInstance();
        EditText editEmail = findViewById(R.id.email_addre);
        Button goToReset = findViewById(R.id.cont_to_reset);
        TextView backToLogin = findViewById(R.id.back_to_log);

        goToReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    editEmail.setError("Enter email");
                    return;
                }
                if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    editEmail.setError("Wrong format");
                    return;
                }


                bestie.checkEmailExists(email, em -> {
                    if (em==null) {
                        Toast.makeText(RecoverActivity.this, "Couldn't find an account with this email", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = getIntent();
                        String emailLink = mAuth.ge;

                        if (mAuth.isSignInWithEmailLink(emailLink)) {
                            Log.e(TAG, "emailLink is sign in with email");
                            mAuth.signInWithEmailLink(email, emailLink)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // user is signed in
                                                Intent goToSetNewPass = new Intent(getApplicationContext(), ResetPassword.class);
                                                startActivity(goToSetNewPass);
                                            } else {
                                                Log.e(TAG, "Error signing in with email link", task.getException());
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecoverActivity.this, LogIn.class);
                startActivity(intent);
            }
        });
    }
}