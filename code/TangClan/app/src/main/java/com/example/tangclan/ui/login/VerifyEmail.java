package com.example.tangclan.ui.login;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tangclan.R;
import com.example.tangclan.TempFeedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class VerifyEmail extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;

    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.page_verify);

        user = mAuth.getCurrentUser();
        generateCodeAndSend();

        EditText editCode = findViewById(R.id.verificatio);
        Button verifyButton = findViewById(R.id.button_veri);
        TextView resend = findViewById(R.id.resend_code);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code != null) {
                    generateCodeAndSend();
                }
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeEntered = editCode.getText().toString();
                if (codeEntered.length() != 6) {
                    editCode.setError("Enter 6-digit code");
                } else if (parseInt(codeEntered) == parseInt(code)) {
                    Intent intent = new Intent(getApplicationContext(), TempFeedActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void generateCodeAndSend() {
        code = generateVerificationCode();
        if (code != null) {
            Toast.makeText(getApplicationContext(), "Sent new code!",
                    Toast.LENGTH_SHORT).show();
            sendVerificationCodeEmail(code);
        } else {
            Toast.makeText(getApplicationContext(), "Error generating new code :(",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a 6-digit code
        return String.valueOf(code);
    }

    public void sendVerificationCodeEmail(String code) {
        if (user != null) {
            String email = user.getEmail();

            String subject = "Your Verification Code";
            String message = "Your verification code is: " + code;

            JavaMailAPI javaMailAPI = new JavaMailAPI(email, subject, message);
            new Thread(() -> javaMailAPI.sendEmail()).start(); // Run in a separate thread
        }
    }
}