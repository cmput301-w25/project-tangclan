package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

public class BackAgeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_set_up_not_18);

        Button NextButton=findViewById(R.id.button_logi);
        NextButton.setOnClickListener(view -> {
            Intent intent = new Intent(BackAgeActivity.this, LoginOrSignupActivity.class);
            startActivity(intent);

        });



    }
}