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

public class FollowActivity extends AppCompatActivity implements FollowRequestAdapter.handleFollowRequest {

    private RecyclerView recyclerView;
    private FollowRequestAdapter adapter;
    private LoggedInUser currentUser;
    private FirebaseAuth auth;

    private DatabaseBestie db;
    List<String> requestList;

    private FollowingBook followingBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_requests);
        NavBarHelper.setupNavBar(this);

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        db = new DatabaseBestie();

        if (firebaseUser == null) {
            Log.e("FollowActivity", "User not logged in!");
            finish(); // Close activity if no user is logged in
            return;
        }

        // Get the logged-in user's UID
        String userUid = firebaseUser.getUid();
        Log.d("FollowActivity", "Logged-in user UID: " + userUid);

        // Initialize currentUser with the logged-in UID
        currentUser = LoggedInUser.getInstance();
        currentUser.initializeFollowingBookFromDatabase(db);
        followingBook = currentUser.getFollowingBook();



        recyclerView = findViewById(R.id.recyclerViewFollowRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list
        requestList = new ArrayList<>();
        adapter = new FollowRequestAdapter(requestList, new FollowingBook(), (pos,accepted) -> {
            String requestedUid = requestList.get(pos);
            requestList.remove(pos);
            adapter.notifyItemRemoved(pos);
            db.deleteFollowRequest(requestedUid,currentUser.getUid());
            if (accepted) {
                followingBook.acceptFollowRequest(requestedUid);
                db.addFollowRelationship(new FollowRelationship(currentUser.getUid(),requestedUid));
            } else {
                followingBook.declineFollowRequest(requestedUid);
            }
        });
        recyclerView.setAdapter(adapter);

        // Fetch and display pending follow requests
        currentUser.getPendingFollowRequests(userUid, requests -> {
            for (int i =0; i < requests.size(); i++) {
                db.getUser(requests.get(i).getRequesterUid(), profile -> {
                    requestList.add(profile.getUsername());
                    adapter.notifyItemInserted(requestList.size()-1);
                });
            }
        });
    }
}
