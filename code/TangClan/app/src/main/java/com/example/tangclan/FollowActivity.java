package com.example.tangclan;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FollowRequestAdapter adapter;
    private User currentUser;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_requests);
        NavBarHelper.setupNavBar(this);

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            Log.e("FollowActivity", "User not logged in!");
            finish(); // Close activity if no user is logged in
            return;
        }

        // Get the logged-in user's UID
        String userUid = firebaseUser.getUid();
        Log.d("FollowActivity", "Logged-in user UID: " + userUid);

        // Initialize currentUser with the logged-in UID
        currentUser = new User();
        currentUser.setUid(userUid);

        recyclerView = findViewById(R.id.recyclerViewFollowRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list
        adapter = new FollowRequestAdapter(new ArrayList<>(), new FollowingBook());
        recyclerView.setAdapter(adapter);

        // Fetch and display pending follow requests
        currentUser.getPendingFollowRequests(userUid, requests -> {
            List<String> followRequestUids = new ArrayList<>();

        });
    }
}
