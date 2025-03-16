package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tangclan.FeedActivity;
import com.example.tangclan.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class NavigationBar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Set up the Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set a listener to handle item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    // Navigate to HomeActivity
                    startActivity(new Intent(NavigationBar.this, FeedActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_add) {
                    // Navigate to SearchActivity
                    startActivity(new Intent(NavigationBar.this, AddEmotionActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    // Navigate to ProfileActivity
                    startActivity(new Intent(NavigationBar.this, profileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}