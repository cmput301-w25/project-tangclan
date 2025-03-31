package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ViewOtherProfileActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private Button followBtn;
    private ListView moodEventsListView;
    private EditText searchEditText;
    private ImageView filterImageView;

    private String otherUsersID;
    private LoggedInUser loggedInUser;
    private DatabaseBestie db;
    private ArrayList<MoodEvent> userMoodEvents = new ArrayList<>();
    private ProfileHistoryAdapter adapter;

    // Filtering variables
    private List<String> selectedEmotionalStates = new ArrayList<>();
    private boolean filterByRecentWeek = false;

    @Override
    public void onStart() {
        super.onStart();
        Bundle profileDetails = getIntent().getExtras();
        if (profileDetails != null) {
            setUpProfileDetails(profileDetails);
            otherUsersID = profileDetails.getString("uid");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list whenever the activity is resumed
        Bundle profileDetails = getIntent().getExtras();
        if (profileDetails != null) {
            setUpProfileDetails(profileDetails);
            otherUsersID = profileDetails.getString("uid");
        }

        loggedInUser = LoggedInUser.getInstance();
        loggedInUser.initializeFollowingBookFromDatabase(db);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page_new);
        NavBarHelper.setupNavBar(this);

        db = new DatabaseBestie();

        usernameTextView = findViewById(R.id.username);
        nameTextView = findViewById(R.id.nameDisplay);
        followersTextView = findViewById(R.id.follower_count);
        followingTextView = findViewById(R.id.following_count);
        filterImageView = findViewById(R.id.filter);
        moodEventsListView = findViewById(R.id.listview_profile_history);


        Bundle profileDetails = getIntent().getExtras();
        if (profileDetails != null) {
            setUpProfileDetails(profileDetails);
            otherUsersID = profileDetails.getString("uid");
        }


        // Get current users following book and initialize it
        loggedInUser = LoggedInUser.getInstance();
        loggedInUser.initializeFollowingBookFromDatabase(db);


        // Set button text depending on current relationship
        followBtn = findViewById(R.id.button_edit_profile);
        setFollowButtonText();

        // get current user's id if not yet
        if (loggedInUser.getUid()== null) {
            loggedInUser.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.checkExistingRequest(loggedInUser.getUid(), otherUsersID, reqExists -> {
                    if (!reqExists) {
                        Log.d("VIEWINGPROFILEACTIVITY","request sent");
                        Toast.makeText(getApplicationContext(), "Follow request sent to "+ usernameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                        db.sendFollowRequest(loggedInUser.getUid(), otherUsersID, requestProcessed -> {
                            loggedInUser.getFollowingBook().addMyRequest(otherUsersID);
                            setFollowButtonText();
                        });
                        setFollowButtonText();
                    }
                });
            }
        });

        searchEditText = findViewById(R.id.editText_search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByKeyword(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        filterImageView.setOnClickListener(v -> showFilterPopup(v));

        moodEventsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position < adapter.getCount()) {
                MoodEvent moodEvent = adapter.getItem(position);
                showMoodEventDetails(moodEvent);
                return true;
            }
            return false;
        });
    }

    public void setUpListView(String username, String uid) {
        moodEventsListView = findViewById(R.id.listview_profile_history);
        db = DatabaseBestie.getInstance();

        db.getAllMoodEvents(uid, events -> {
            Collections.reverse(events);
            userMoodEvents = new ArrayList<>(events); // Store the original events
            adapter = new ProfileHistoryAdapter(this, events, username); // Initialize the class field
            moodEventsListView.setAdapter(adapter);
        });
    }

    public void setUpProfileDetails(Bundle bundle) {
        String otherUsersID = bundle.getString("uid");
        String username = bundle.getString("username");
        String displayName = bundle.getString("displayName");

        usernameTextView.setText(username);
        nameTextView.setText(displayName);

        // use db to get the follower and following count of a user
        db.getFollowers(otherUsersID, fllwers -> {
            followersTextView.setText(String.valueOf(fllwers.size()));
        });
        db.getFollowing(otherUsersID, fllwing -> {
            followingTextView.setText(String.valueOf(fllwing.size()));
        });

        // set up profile picture
        String pfpStr = bundle.getString("pfp");
        if (pfpStr != null) {
            Log.d("pfpstr", pfpStr);
            ImageView pfp = findViewById(R.id.pfpView);
            byte[] decodedBytes = Base64.decode(pfpStr, Base64.DEFAULT);
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            pfp.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        }

        setUpListView(username, otherUsersID);
    }

    public void setFollowButtonText() {
        // Set button text depending on current relationship
        Button followBtn = findViewById(R.id.button_edit_profile);
        if (loggedInUser.getFollowingBook().getFollowing().contains(otherUsersID)) {
            followBtn.setText("  Following ");
            followBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            followBtn.setTextColor(Color.parseColor("#ffffff"));
        } else if (loggedInUser.getFollowingBook().getMyFollowRequests().contains(otherUsersID)) {
            followBtn.setText("   Pending   ");
            followBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            followBtn.setTextColor(Color.parseColor("#ffffff"));
        }
        else { followBtn.setText("   Follow   "); }
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
        Button resetFiltersButton = popupView.findViewById(R.id.button_reset_filters);

        // Set up the emotional states list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                getResources().getStringArray(R.array.emotional_states));
        emotionalStatesList.setAdapter(adapter);
        emotionalStatesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Restore previous selections
        filterRecentWeekCheckbox.setChecked(filterByRecentWeek);

        // Restore emotional state selections
        String[] emotionalStates = getResources().getStringArray(R.array.emotional_states);
        for (int i = 0; i < emotionalStates.length; i++) {
            if (selectedEmotionalStates.contains(emotionalStates[i])) {
                emotionalStatesList.setItemChecked(i, true);
            }
        }

        // Update "Select All" checkbox based on current selections
        selectAllCheckbox.setChecked(selectedEmotionalStates.size() == emotionalStates.length);

        // Set up the "Select All" checkbox
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < emotionalStatesList.getCount(); i++) {
                emotionalStatesList.setItemChecked(i, isChecked);
            }
        });

        // Set up the apply filter button
        applyFilterButton.setOnClickListener(v -> {
            // Get the selected emotional states
            selectedEmotionalStates.clear();
            SparseBooleanArray checkedItems = emotionalStatesList.getCheckedItemPositions();
            for (int i = 0; i < checkedItems.size(); i++) {
                if (checkedItems.valueAt(i)) {
                    selectedEmotionalStates.add(emotionalStatesList.getItemAtPosition(checkedItems.keyAt(i)).toString());
                }
            }

            // Get the "In the last week" filter value
            filterByRecentWeek = filterRecentWeekCheckbox.isChecked();

            // Apply the filters
            applyFilters(selectedEmotionalStates, filterByRecentWeek);

            // Dismiss the popup
            popupWindow.dismiss();
        });

        // Set up the reset filters button
        resetFiltersButton.setOnClickListener(v -> {
            // Reset the filters
            resetFilters();

            // Dismiss the popup
            popupWindow.dismiss();
        });
    }

    /**
     * Applies selected filters to the mood events list
     */
    private void applyFilters(List<String> selectedEmotionalStates, boolean filterByRecentWeek) {
        List<MoodEvent> filteredEvents = new ArrayList<>(userMoodEvents);

        // Filter by emotional state (multiple selections)
        if (!selectedEmotionalStates.isEmpty()) {
            filteredEvents = filteredEvents.stream()
                    .filter(event -> selectedEmotionalStates.stream()
                            .anyMatch(state -> state.equalsIgnoreCase(event.getMoodEmotionalState())))
                    .collect(Collectors.toList());
        }

        // Filter by recent week if selected
        if (filterByRecentWeek) {
            LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
            filteredEvents = Filter.filterByTimeRange(filteredEvents, oneWeekAgo, LocalDate.now());
        }

        // Also apply any existing keyword filter
        if (searchEditText != null && !searchEditText.getText().toString().isEmpty()) {
            String keyword = searchEditText.getText().toString().trim();
            List<String> keywords = new ArrayList<>();
            keywords.add(keyword);
            filteredEvents = Filter.filterByKeywords(filteredEvents, keywords);
        }

        // Update the adapter with filtered events
        updateAdapterWithFilteredEvents(filteredEvents);
    }

    /**
     * Filters mood events by keyword
     */
    private void filterByKeyword(String keyword) {
        List<MoodEvent> filteredEvents = new ArrayList<>(userMoodEvents);

        if (!keyword.isEmpty()) {
            List<String> keywords = new ArrayList<>();
            keywords.add(keyword);
            filteredEvents = Filter.filterByKeywords(filteredEvents, keywords);
        }

        // Apply any existing emotional state and time filters
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

        // Update the adapter with filtered events
        updateAdapterWithFilteredEvents(filteredEvents);
    }

    /**
     * Updates the ListView adapter with filtered events
     */
    private void updateAdapterWithFilteredEvents(List<MoodEvent> filteredEvents) {
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(filteredEvents);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Resets all filters and shows all mood events
     */
    private void resetFilters() {
        // Clear all filters
        selectedEmotionalStates.clear();
        filterByRecentWeek = false;

        // Clear search text
        if (searchEditText != null) {
            searchEditText.setText("");
        }

        // Reset to original events list
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(userMoodEvents);
            adapter.notifyDataSetChanged();
        }

        // Notify user
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays the details of a selected mood event in an alert dialog
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
}