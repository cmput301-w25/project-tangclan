package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


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
    //feed activitysssnn
    private ListView listViewFeed;
    private Feed feed;
    private MoodEventAdapter adapter;

    /**
     * Initializes the activity, sets up the user interface, loads the mood event feed,
     * and configures event listeners for adding and viewing mood events.
     *
     * @param savedInstanceState The saved instance state from a previous session, if any.
     */

    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
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

            Log.d("FINALDEBUG", user.getUsername());
            Log.d("FINALDEBUG", String.valueOf(loggedInUser.getMoodEventBook().getMoodEventCount()));
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_new);

        // Initialize the ListView
        listViewFeed = findViewById(R.id.listview_feed); // Initialize listViewFeed here

        // Initialize the adapter and set it to the ListView
        adapter = new MoodEventAdapter(this, new ArrayList<>());
        listViewFeed.setAdapter(adapter); // Now listViewFeed is properly initialized

        // Set up the filter ImageView
        ImageView filterImageView = findViewById(R.id.filter);
        filterImageView.setOnClickListener(v -> showFilterPopup(v));

        // Initialize the feed
        FollowingBook followingBook = new FollowingBook();
        MoodEventBook moodEventBook = new MoodEventBook();
        feed = new Feed(followingBook, moodEventBook);

        // Load the feed
        loadFeed();

        // Set up the "Add Emotion" button
        ImageButton addEmotionButton = findViewById(R.id.fabAdd);
        addEmotionButton.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, AddEmotionActivity.class);
            startActivity(intent);
        });


        // Set up long-click listener for mood event details
        listViewFeed.setOnItemLongClickListener((parent, view, position, id) -> {
            MoodEvent moodEvent = feed.getFeedEvents().get(position);
            showMoodEventDetails(moodEvent);
            return true;
        });
        EditText searchEditText = findViewById(R.id.editText_search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the mood events based on the keyword
                String keyword = s.toString().trim();
                filterByKeyword(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        // NAVBAR
        NavBarHelper.setupNavBar(this);
    }

    /**
     * Loads the mood event feed and updates the list adapter.
     *
     */
    private void loadFeed() {
        feed.loadFeed();
        FollowingBook followingBook = feed.getFollowingBook();  // Assuming you have this getter in Feed class.

        adapter = new MoodEventAdapter(this, followingBook);  // Pass followingBook instead of feedEvents
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

        if (moodEvent.getReason().isPresent()) {
            details.append("Situation: ").append(moodEvent.getReason().get()).append("\n");
        } else {
            details.append("Situation: N/A\n");
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

    public void showFilterPopup(View view) {
        // Inflate the popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.filter_popup, null);

        // Create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup dismiss it
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // Get references to the UI elements
        CheckBox selectAllCheckbox = popupView.findViewById(R.id.select_all_checkbox);
        CheckBox filterRecentWeekCheckbox = popupView.findViewById(R.id.filter_recent_week);
        ListView emotionalStatesList = popupView.findViewById(R.id.emotional_states_list);
        Button applyFilterButton = popupView.findViewById(R.id.apply_filter_button);
        Button resetFiltersButton = popupView.findViewById(R.id.button_reset_filters); // Reset Filters button

        // Set up the emotional states list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, getResources().getStringArray(R.array.emotional_states));
        emotionalStatesList.setAdapter(adapter);

        // Set up the "Select All" checkbox
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < emotionalStatesList.getCount(); i++) {
                emotionalStatesList.setItemChecked(i, isChecked);
            }
        });

        // Set up the apply filter button
        applyFilterButton.setOnClickListener(v -> {
            // Get the selected emotional states
            SparseBooleanArray checkedItems = emotionalStatesList.getCheckedItemPositions();
            List<String> selectedEmotionalStates = new ArrayList<>();
            for (int i = 0; i < checkedItems.size(); i++) {
                if (checkedItems.valueAt(i)) {
                    selectedEmotionalStates.add(emotionalStatesList.getItemAtPosition(checkedItems.keyAt(i)).toString());
                }
            }

            // Get the "In the last week" filter value
            boolean filterByRecentWeek = filterRecentWeekCheckbox.isChecked();

            // Apply the filters
            applyFilters(selectedEmotionalStates, filterByRecentWeek);

            // Dismiss the popup
            popupWindow.dismiss();
        });

        // Set up the reset filters button
        resetFiltersButton.setOnClickListener(v -> {
            // Reset the feed to its original state
            resetFilters();

            // Dismiss the popup
            popupWindow.dismiss();
        });
    }

    private void applyFilters(List<String> selectedEmotionalStates, boolean filterByRecentWeek) {
        List<MoodEvent> filteredEvents = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            filteredEvents.add(adapter.getItem(i));
        }

        for (MoodEvent event : filteredEvents) {
            Log.d("FeedActivity", "Event: " + event.getMoodEmotionalState() + ", Date: " + event.getPostDate());
        }

        // Filter by emotional state (multiple selections)
        if (!selectedEmotionalStates.isEmpty()) {
            filteredEvents = filteredEvents.stream()
                    .filter(event -> selectedEmotionalStates.stream()
                            .anyMatch(state -> state.equalsIgnoreCase(event.getMoodEmotionalState())))
                    .collect(Collectors.toList());
        }

        if (filterByRecentWeek) {
            LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
            filteredEvents = Filter.filterByTimeRange(filteredEvents, oneWeekAgo, LocalDate.now());
        }

        for (MoodEvent event : filteredEvents) {
            Log.d("FeedActivity", "Event: " + event.getMoodEmotionalState() + ", Date: " + event.getPostDate());
        }

        // Update the adapter with the filtered events
        adapter.updateMoodEvents(filteredEvents);
    }
    private void filterByKeyword(String keyword) {
        List<MoodEvent> filteredEvents = new ArrayList<>(feed.getFeedEvents());


        if (!keyword.isEmpty()) {
            List<String> keywords = new ArrayList<>();
            keywords.add(keyword);
            filteredEvents = Filter.filterByKeywords(filteredEvents, keywords);
        }


        adapter.updateMoodEvents(filteredEvents);
    }

    private void resetFilters() {
        // Reload the feed without applying any filters
        loadFeed();

        // Clear the search EditText
        EditText searchEditText = findViewById(R.id.editText_search);
        searchEditText.setText("");

        // Notify the user that filters have been reset
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }
    //need to account for multiple moods being selected



}
