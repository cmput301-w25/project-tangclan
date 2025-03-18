package com.example.tangclan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchUsersActivity extends AppCompatActivity {

    RecyclerView userCardRecycler;
    ArrayList<Profile> profileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_users);

        userCardRecycler = findViewById(R.id.search_users_result);
        userCardRecycler.setLayoutManager(new LinearLayoutManager(this));

        // add to profiles from db to list

        ProfileRecyclerView profileRecyclerView = new ProfileRecyclerView(this, profileList);
        userCardRecycler.setAdapter(profileRecyclerView);
    }
}