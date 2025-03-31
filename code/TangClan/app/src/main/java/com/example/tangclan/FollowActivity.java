package com.example.tangclan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends AppCompatActivity implements FollowRequestAdapter.handleFollowRequest {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FollowRequestAdapter adapter;
    private LoggedInUser currentUser;
    private FirebaseAuth auth;

    private DatabaseBestie db;
    private List<String> requestList;
    private List<String> followRequestUids;
    private FollowingBook followingBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_requests);
        NavBarHelper.setupNavBar(this);

        // Initialize lists
        requestList = new ArrayList<>();
        followRequestUids = new ArrayList<>();

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        // Initialize database
        db = new DatabaseBestie();

        if (firebaseUser == null) {
            Log.e("FollowActivity", "User not logged in!");
            finish(); // Close activity if no user is logged in
            return;
        }

        // Set up swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshFollowRequests);

        // Get the logged-in user's UID
        String userUid = firebaseUser.getUid();
        Log.d("FollowActivity", "Logged-in user UID: " + userUid);

        // Initialize current user and following book
        currentUser = LoggedInUser.getInstance();
        currentUser.initializeFollowingBookFromDatabase(db);
        followingBook = currentUser.getFollowingBook();

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFollowRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list
        adapter = new FollowRequestAdapter(requestList, followingBook, this);
        recyclerView.setAdapter(adapter);

        // Fetch and display pending follow requests
        loadFollowRequests(userUid);
    }

    private void loadFollowRequests(String userUid) {
        requestList.clear();
        followRequestUids.clear();
        adapter.notifyDataSetChanged();

        currentUser.getPendingFollowRequests(userUid, requests -> {
            for (FollowRequest request : requests) {
                followRequestUids.add(request.getRequesterUid());
                db.getUser(request.getRequesterUid(), profile -> {
                    requestList.add(profile.getUsername());
                    adapter.notifyItemInserted(requestList.size()-1);
                });
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void refreshFollowRequests() {
        if (auth.getCurrentUser() != null) {
            loadFollowRequests(auth.getCurrentUser().getUid());
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRequestHandled(int position, int mode) {
        String requesterUid = followRequestUids.get(position);
        requestList.remove(position);
        followRequestUids.remove(position);
        adapter.notifyItemRemoved(position);

        if (mode == 1) {
            FollowRelationship relationship = new FollowRelationship(currentUser.getUid(), requesterUid);
            db.addFollowRelationship(relationship);
            followingBook.acceptFollowRequest(requesterUid);
            Toast.makeText(this, "Follow request accepted", Toast.LENGTH_SHORT).show();
            db.deleteFollowRequest(requesterUid, currentUser.getUid());
        } else if (mode == 2) {
            followingBook.declineFollowRequest(requesterUid);
            Toast.makeText(this, "Follow request declined", Toast.LENGTH_SHORT).show();
            db.deleteFollowRequest(requesterUid, currentUser.getUid());
        } else if (mode == 3) {
            db.getUser(requesterUid, profile -> {
                Intent intent = new Intent(FollowActivity.this, ViewOtherProfileActivity.class);
                Bundle profileDetails = new Bundle();
                profileDetails.putString("uid", profile.getUid());
                profileDetails.putString("username",profile.getUsername());
                profileDetails.putString("email",profile.getEmail());
                profileDetails.putString("displayName",profile.getDisplayName());
                String pfpStr = profile.getProfilePic();
                if (pfpStr != null) {
                    byte[] decodedBytes = Base64.decode(pfpStr, Base64.DEFAULT);
                    Bitmap toCompress = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    byte[] pfpBytes = ImageValidator.compressBitmapToSize(toCompress);
                    if (pfpBytes != null) {
                        profileDetails.putString("pfp", Base64.encodeToString(pfpBytes, Base64.DEFAULT));
                    }
                } else {
                    profileDetails.putString("pfp", null);
                }
                intent.putExtras(profileDetails);
                startActivity(intent);
            });
        }
    }
}