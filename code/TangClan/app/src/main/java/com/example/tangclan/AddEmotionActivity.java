package com.example.tangclan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddEmotionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not set any content view for the activity
        // Instead, just add the fragment directly

        if (savedInstanceState == null) {
            // Only add the fragment if this is the first creation
            AddEmotionFragment addEmotionFragment = new AddEmotionFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, addEmotionFragment)
                    .commit();
        }
    }
}