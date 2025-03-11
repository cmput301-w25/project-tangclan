package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IntroPostLoginActivity extends AppCompatActivity {

    Button IntroNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_login);
         IntroNext=(Button) findViewById(R.id.IntroNextButton);

        IntroNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(IntroPostLoginActivity.this, SetupBirthdayActivity.class);
                startActivity(intent);
            }
        });
    }

    }
