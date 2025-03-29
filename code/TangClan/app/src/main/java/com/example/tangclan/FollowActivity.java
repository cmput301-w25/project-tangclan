package com.example.tangclan;

import android.os.Bundle;
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

    private List<String> requestList;

    private List<String> followRequestUids;
    private FollowingBook followingBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_requests);
        NavBarHelper.setupNavBar(this);

        requestList = new ArrayList<>();
        followRequestUids = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        db = new DatabaseBestie();

        if (firebaseUser == null) {
            Log.e("FollowActivity", "User not logged in!");
            finish();
            return;
        }
        
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshFollowRequests);




        String userUid = firebaseUser.getUid();
        Log.d("FollowActivity", "Logged-in user UID: " + userUid);

        currentUser = LoggedInUser.getInstance();
        currentUser.initializeFollowingBookFromDatabase(db);
        followingBook = currentUser.getFollowingBook();

        recyclerView = findViewById(R.id.recyclerViewFollowRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FollowRequestAdapter(requestList, followingBook, this);
        recyclerView.setAdapter(adapter);



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
    public void onRequestHandled(int position, boolean accepted) {
        String requesterUid = followRequestUids.get(position);
        requestList.remove(position);
        followRequestUids.remove(position);
        adapter.notifyItemRemoved(position);

        if (accepted) {
            FollowRelationship relationship = new FollowRelationship(currentUser.getUid(), requesterUid);
            db.addFollowRelationship(relationship);
            followingBook.acceptFollowRequest(requesterUid);

            Toast.makeText(this, "Follow request accepted", Toast.LENGTH_SHORT).show();
        } else {
            followingBook.declineFollowRequest(requesterUid);
            Toast.makeText(this, "Follow request declined", Toast.LENGTH_SHORT).show();
        }
        db.deleteFollowRequest(requesterUid, currentUser.getUid());
    }
}