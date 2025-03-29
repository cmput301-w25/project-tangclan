package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


//part of US 01.01.01, US 01.04.01, US 01.05.01 and US 01.06.01

/**
 * The class is responsible for displaying the mood event feed to the user.
 * It allows users to view the most recent mood events from participants they follow,
 * add a new mood event, and view detailed information about any mood event in the feed.

 */

//TODO make sure this screen is updated after the addition of a mood event from the add emotion fragments

//TODO fix the bug for loadfeed because of the List<MoodEvent> to following book, cause runtime error


/**
 * Represents the activity feed, with all MoodEvents of users that the session user follows
 * USER STORIES:
 *      US 01.04.01
 */


public class FeedActivity extends AppCompatActivity {
    private ListView listViewFeed;
    private Feed feed;
    private MoodEventAdapter adapter;
    private TextView followingTab;
    private TextView forYouTab;
    private View rectangleFollowing;
    private View rectangleForYou;



    // creating variables for UI components
    private RecyclerView ProfileRV;
    private Toolbar toolbar;

    // variable for our adapter class and array list
    private SearchOtherProfileAdapter adapter1;
    private ArrayList<Profile> ProfileArrayList;




    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        Log.d("Tag123:",currentUser.getUid());
        if(currentUser == null) {

            startActivity(new Intent(FeedActivity.this, LoginOrSignupActivity.class));
            finish();
        }

        DatabaseBestie db = new DatabaseBestie();

        LoggedInUser loggedInUser = LoggedInUser.getInstance();

        db.getUser(currentUser.getUid(), user -> {
           loggedInUser.setEmail(user.getEmail());
           loggedInUser.setUsername(user.getUsername());
           loggedInUser.setPassword(user.getPassword());
           loggedInUser.setDisplayName(user.getDisplayName());
           loggedInUser.setAge(user.getAge());
           loggedInUser.setUid(currentUser.getUid());
           loggedInUser.initializeMoodEventBookFromDatabase(db);

           Log.d("FINALDEBUG", String.valueOf(loggedInUser.getMoodEventBook().getMoodEventCount()));
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed);
        NavBarHelper.setupNavBar(this);

        //Searching for users
        // initializing variables
        ProfileRV = findViewById(R.id.ProfileRecyclerViewForYou);
        toolbar = findViewById(R.id.toolbar);




        // setting the toolbar as the ActionBar
        setSupportActionBar(toolbar);

        // calling method to build recycler view
        buildRecyclerView();



        // Logout button
        Button logout = findViewById(R.id.logout_butt);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                // Clear the LoggedInUser instance on logout
                LoggedInUser loggedInUser = LoggedInUser.getInstance();
                LoggedInUser.resetInstance();

                // Redirect to the login/signup activity
                Intent intent = new Intent(FeedActivity.this, LoginOrSignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize the ListView
        listViewFeed = findViewById(R.id.listViewFeed);

        // Initialize tab views for feed switching
        followingTab = findViewById(R.id.following);
        forYouTab = findViewById(R.id.for_you);
        rectangleFollowing = findViewById(R.id.rectangle_1);
        rectangleForYou = findViewById(R.id.rectangle_2);

        // Retrieve the LoggedInUser instance
        LoggedInUser loggedInUser = LoggedInUser.getInstance();

        // Ensure the LoggedInUser instance is properly populated
        if (loggedInUser.getUsername() == null) {
            // If the LoggedInUser instance is not populated, redirect to login
            startActivity(new Intent(FeedActivity.this, LoginOrSignupActivity.class));
            finish();
            return;
        }

        // Get the user's FollowingBook and MoodEventBook from the LoggedInUser instance
        FollowingBook followingBook = loggedInUser.getFollowingBook();
        MoodEventBook moodEventBook = loggedInUser.getMoodEventBook();

        // Initialize the Feed with the user's FollowingBook and MoodEventBook
        feed = new Feed(followingBook, moodEventBook);

        // Set up tab click listeners
        setupTabListeners();

        // Load the following feed by default (3 most recent events)
        loadFollowingFeed();

        // Add Emotion button
        ImageButton addEmotionButton = findViewById(R.id.fabAdd);
        addEmotionButton.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, AddEmotionActivity.class);
            startActivity(intent);
        });

        // Long click listener for mood event details
        listViewFeed.setOnItemLongClickListener((parent, view, position, id) -> {
            MoodEvent moodEvent = feed.getFeedEvents().get(position);
            showMoodEventDetails(moodEvent);
            return true;
        });

        // NAVBAR
        ImageView pinIcon = findViewById(R.id.imgMap);
        ImageView homeIcon = findViewById(R.id.imgHome); // do nothing but change color to white
        ImageView searchIcon = findViewById(R.id.imgSearch);
        ImageView profileIcon = findViewById(R.id.imgProfile);
        //Button testButton=findViewById(R.id.test_button);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedActivity.this, ProfilePageActivity.class));
                finish();
            }
        });



    }

    /**
     * Sets up tab listeners for switching between Following and For You feeds
     */
    private void setupTabListeners() {
        rectangleFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFollowingFeed();
                // Update UI to show Following tab is active
                rectangleFollowing.setBackgroundResource(R.drawable.rectangle_1); // Active style
                rectangleForYou.setBackgroundResource(R.drawable.rectangle_2); // Inactive style
                listViewFeed.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.INVISIBLE);
                ProfileRV.setVisibility(View.INVISIBLE);

            }
        });

        rectangleForYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFullFeed();
                // Update UI to show For You tab is active
                rectangleFollowing.setBackgroundResource(R.drawable.rectangle_2); // Inactive style
                rectangleForYou.setBackgroundResource(R.drawable.rectangle_1); // Active style
                listViewFeed.setVisibility(View.INVISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                ProfileRV.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Loads only the 3 most recent mood events from followed users
     */
    private void loadFollowingFeed() {
        // Load just the 3 most recent events from followed users
        feed.loadRecentFollowingFeed();

        if (feed.getFeedEvents().isEmpty()) {
            Toast.makeText(this, "No recent mood events from people you follow", Toast.LENGTH_SHORT).show();
        }

        adapter = new MoodEventAdapter(this, feed.getFeedEvents());
        listViewFeed.setAdapter(adapter);
    }

    /**
     * Loads the full mood event feed
     */
    private void loadFullFeed() {
        feed.loadFeed();
        adapter = new MoodEventAdapter(this, feed.getFeedEvents());
        listViewFeed.setAdapter(adapter);
    }

    /**
     * Displays the details of a selected mood event in an alert dialog.
     *
     * @param moodEvent The mood event whose details are to be displayed.
     */
    private void showMoodEventDetails(MoodEvent moodEvent) {
        StringBuilder details = new StringBuilder();
        details.append("Emotional State: ").append(moodEvent.getMoodEmotionalState()).append("\n");
        details.append("Mood Color: ").append(moodEvent.getMood().getColor(getBaseContext()).toString()).append("\n");
        details.append("Emoticon: ").append(moodEvent.getMoodEmotionalState()).append("emote\n");

        // Use getCollaborators() instead of getSituation()
        if (moodEvent.getCollaborators().isPresent()) {
            details.append("Situation: ").append(moodEvent.getCollaborators().get()).append("\n");
        } else {
            details.append("Situation: N/A\n");
        }

        if (moodEvent.getReason().isPresent()) {
            details.append("Reason: ").append(moodEvent.getReason().get()).append("\n");
        } else {
            details.append("Reason: N/A\n");
        }

        if (moodEvent.hasGeolocation()) {
            details.append("Location: Lat: ").append(moodEvent.getLatitude()).append(", Lon: ").append(moodEvent.getLongitude()).append("\n");
        } else {
            details.append("Location: N/A\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Mood Event Details")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the feed when coming back to this activity
        loadFollowingFeed();
    }



    // calling onCreateOptionsMenu to inflate our menu file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the MenuInflater
        MenuInflater inflater = getMenuInflater();

        // inflate the menu
        inflater.inflate(R.menu.search_menu,menu);

        // get the search menu item
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // get the SearchView from the menu item
        SearchView searchView = (SearchView) searchItem.getActionView();

        // set the on query text listener for the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // call a method to filter your RecyclerView
                filter(newText);
                return false;
            }
        });
        return true;
    }

    // method to filter data based on query
    private void filter(String text) {
        // creating a new array list to filter data
        ArrayList<Profile> filteredlist = new ArrayList<>();

        // running a for loop to compare elements
        for (Profile item : ProfileArrayList) {
            // checking if the entered string matches any item of our recycler view
            if (item.getUsername().toLowerCase().contains(text.toLowerCase())) {
                // adding matched item to the filtered list
                filteredlist.add(item);
            }
        }

        if (filteredlist.isEmpty()) {
            // displaying a toast message if no data found
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // passing the filtered list to the adapter class
            adapter1.filterList(filteredlist);
        }
    }

    // method to build RecyclerView
    private void buildRecyclerView() {
        // creating a new array list

        //Get the array list from the database here!
        ProfileArrayList = new ArrayList<>();

        // adding data to the array list from the collection from the database
        //Profile profileTest=databaseBestie.getUser("yMlofXqAySgYvEFaOyocWp2yNbh1",);


        //TODO: HOOK UP TO DATABASE, AND GET PROFILES FROM USERS COLLECTIONS AND ADD TO PROFILE ARRAYLIST
        ProfileArrayList.add(new Profile("Shaiansss","shaian","Gg123?111","shaian@ualberta.ca","20",null));
        ProfileArrayList.add(new Profile("Shaiansss","shaian123","Gg123?111","shaian@ualberta.ca","20",null));
        ProfileArrayList.add(new Profile("Shaiansss","ggshaian","Gg123?111","shaian@ualberta.ca","20",null));
        ProfileArrayList.add(new Profile("Shaiansss","BBshaian","Gg123?111","shaian@ualberta.ca","20",null));





        // initializing the adapter class
        adapter1 = new SearchOtherProfileAdapter(ProfileArrayList, FeedActivity.this);

        // adding layout manager to the RecyclerView
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ProfileRV.setHasFixedSize(true);

        // setting layout manager to the RecyclerView
        ProfileRV.setLayoutManager(manager);

        // setting adapter to the RecyclerView
        ProfileRV.setAdapter(adapter1);
    }








}