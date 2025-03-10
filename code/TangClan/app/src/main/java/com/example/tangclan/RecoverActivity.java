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

import com.example.tangclan.ui.login.LogIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        Button sendReset = findViewById(R.id.send_password_reset);
        TextView backToLogin = findViewById(R.id.back_to_log);

        sendReset.setOnClickListener(new View.OnClickListener() {
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
                        mAuth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.e(TAG,"password sent");
                                        Toast.makeText(RecoverActivity.this, "Reset Password link has been sent to your registered email", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RecoverActivity.this, LogIn.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RecoverActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
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