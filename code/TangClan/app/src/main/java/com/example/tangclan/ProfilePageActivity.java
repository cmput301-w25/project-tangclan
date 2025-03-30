package com.example.tangclan;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.transition.Slide;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ProfilePageActivity extends AppCompatActivity implements EditFragment.FragmentListener {

    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private ListView profileArrayListView;
    private Profile userProfile;
    private Button editProfileBtn;
    private DatabaseBestie databaseBestie;
    private ArrayAdapter<MoodEvent> adapter;//
    private NetworkManager networkManager;

    private ListView listViewFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page_new);
        NavBarHelper.setupNavBar(this);

        networkManager = new NetworkManager(getApplicationContext());

        // Initialize views
        usernameTextView = findViewById(R.id.username);
        nameTextView = findViewById(R.id.nameDisplay);
        followersTextView = findViewById(R.id.follower_count);
        followingTextView = findViewById(R.id.following_count);
        profileArrayListView = findViewById(R.id.listview_profile_history);
        editProfileBtn = findViewById(R.id.button_edit_profile);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });

        // Initialize database helper
        databaseBestie = new DatabaseBestie();


        // Get current user profile
        getCurrentUserProfile();
        setupProfileListView();


        ImageView filterImageView = findViewById(R.id.filter);
        filterImageView.setOnClickListener(v -> showFilterPopup(v));

        // Set up the search bar
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


        // Process incoming mood event data if it exists
        processMoodEventData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list whenever the activity is resumed
        getCurrentUserProfile();
        setupProfileListView();
        networkManager.registerNetworkMonitor();
    }

    @Override
    protected void onPause() {
        networkManager.unregisterNetworkMonitor();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        networkManager.unregisterNetworkMonitor();
        super.onDestroy();

    }

    private void getCurrentUserProfile() {

        // Retrieve the current logged-in user profile using the Singleton instance
        userProfile = LoggedInUser.getInstance();

        // Initialize the mood event book if it doesn't exist
        if (userProfile.getMoodEventBook() == null) {
            userProfile.setMoodEventBook(new MoodEventBook());
        }

        // Fetch the user's past mood events from the database
        initializeMoodEventBookFromDatabase();
        userProfile.initializeFollowingBookFromDatabase(databaseBestie);

        // This method should retrieve the current user's profile
        // For now, we'll create a dummy profile for testing
        //userProfile = LoggedInUser.getInstance();

        // Initialize the mood event book if it doesn't exist

        // Set the user information in the UI
        String pfpStr = userProfile.getProfilePic();
        if (pfpStr != null) {
            ImageView pfp = findViewById(R.id.pfpView);
            byte[] decodedBytes = Base64.decode(pfpStr, Base64.DEFAULT);
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            pfp.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        }
        usernameTextView.setText(String.format("@%s", userProfile.getUsername()));
        nameTextView.setText(userProfile.getDisplayName());
        String followerCt = String.valueOf(userProfile.getFollowingBook().getFollowerCount());
        String followingCt = String.valueOf(userProfile.getFollowingBook().getFollowingCount());
        followersTextView.setText(followerCt);
        followingTextView.setText(followingCt);

        // Setup the ListView after profile is loaded
        setupProfileListView();
    }

    private void initializeMoodEventBookFromDatabase() {
        // Fetch the user's past mood events from the database
        if (userProfile != null) {
            userProfile.initializeMoodEventBookFromDatabase(databaseBestie);
        }
    }

    private void setupProfileListView() {
        if (userProfile != null && userProfile.getMoodEventBook() != null) {
            // Create a custom adapter using ProfileHistoryAdapter which has all the proper formatting
            //adapter = new ProfileHistoryAdapter(this, userProfile);

            // Set the adapter on the ListView
            //profileArrayListView.setAdapter(adapter);

            if (adapter == null) {
                adapter = new ProfileHistoryAdapter(this, userProfile);
                profileArrayListView.setAdapter(adapter);
            } else {
                // Otherwise, just update the existing adapter
                adapter.clear();
                adapter.addAll(userProfile.getMoodEventBook().getMoodEventList());
                adapter.notifyDataSetChanged();
            }


            // Adjust ListView height if needed
            //ViewGroup.LayoutParams params = profileArrayListView.getLayoutParams();

            //params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            //params.height = ViewGroup.LayoutParams.WRAP_CONTENT; // Let it expand as needed

            //profileArrayListView.setLayoutParams(params);

            // DELETE / EDIT / CANCEL operations on LongPress for Mood Events
            profileArrayListView.setOnItemLongClickListener((parent, view, position, id) -> {
                MoodEvent post = adapter.getItem(position); // Access from the data list
                String mid = post.getMid();

                String postDate = post.userFormattedDate();
                String month = postDate.substring(3);

                new AlertDialog.Builder(view.getContext())
                        .setMessage("Do you want to edit or delete this mood event?")
                        .setPositiveButton("Edit", (dialog, which) -> {
                            // Edit mood
                            Bundle moodDetails = getMoodEventBundle(post);
                            EditFragment form = EditFragment.newInstance(moodDetails);
                            form.setEnterTransition(new Slide(Gravity.BOTTOM));
                            form.setExitTransition(new Slide(Gravity.BOTTOM));
                            getSupportFragmentManager()
                                    .beginTransaction().add(R.id.edit_form_container, form).commit();

                        })
                        .setNegativeButton("Delete", (dialog, which) -> {
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle("Are you sure you want to delete this mood event?")
                                    .setMessage("This action cannot be undone.")
                                    .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                        // Remove item from the data list, NOT the ListView itself


                                        // delete from mood event book and database
                                        databaseBestie.getMoodEventByMid(post.getMid(), month, (event, emot) -> {
                                            userProfile.getMoodEventBook().deleteMoodEvent(event);
                                        });

                                        databaseBestie.deleteMoodEvent(post.getMid(), month);
                                        Toast.makeText(view.getContext(), "Mood Event Deleted", Toast.LENGTH_SHORT).show();


                                        adapter.notifyDataSetChanged(); // Notify adapter of changes

                                        adapter.remove(post);


                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
                return true; // Indicate that the long press event is consumed
            });

        }
    }

    private void processMoodEventData() {
        // Retrieve the Bundle data passed from ReviewDetailsActivity
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String selectedEmotion = bundle.getString("emotion");
            String selectedSetting = bundle.getString("setting");
            ArrayList<String> selectedSituation = bundle.getStringArrayList("collaborators");
            String reason = bundle.getString("reason");
            String image = bundle.getString("image");
            boolean privacy = bundle.getBoolean("privacy");


            // Create a new MoodEvent


            try {
                MoodEvent newMoodEvent;
                // Create the mood event based on available data
                if (selectedSituation != null && !selectedSituation.isEmpty()) {
                    newMoodEvent = new MoodEvent(selectedEmotion, selectedSituation);
                } else {
                    newMoodEvent = new MoodEvent(selectedEmotion);
                }


                // set setting
                if (selectedSetting != null) {
                    newMoodEvent.setSetting(selectedSetting);
                } else {
                    newMoodEvent.setSetting("");
                }

                // Set reason if available
                if (reason != null && !reason.isEmpty()) {
                    newMoodEvent.setReason(reason);
                } else {
                    newMoodEvent.setReason("");
                }

                // Set image if available
                if (image != null) {
                    byte[] imgBytes = Base64.decode(image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    newMoodEvent.setImage(bitmap);
                }

                // Set Privacy setting
                newMoodEvent.setPrivacyOn(privacy);

                // Add the mood event to the user's mood event book
                userProfile.post(newMoodEvent, databaseBestie);

                // Save the updated profile to the database
                //saveProfileToDatabase();

                // Force refresh the ListView by recreating the adapter
                //setupProfileListView();

                if (adapter != null) {
                    adapter.add(newMoodEvent);
                    adapter.notifyDataSetChanged();
                }

                // Show success message
                Toast.makeText(this, "Mood event added successfully!", Toast.LENGTH_SHORT).show();

                // Log the number of mood events for debugging
                int count = userProfile.getMoodEventBook().getMoodEventList().size();
                Toast.makeText(this, "Total mood events: " + count, Toast.LENGTH_SHORT).show();

            } catch (IllegalArgumentException e) {
                // Handle invalid input
                Toast.makeText(this, "Error creating mood event: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveProfileToDatabase() {


    }


    public void goToEditProfile() {
        // Handle edit profile button click
        Intent intent = new Intent(this, EditProfileActivity.class);
        /*
        Bundle profileDetails = new Bundle();
        profileDetails.putString("pfp",userProfile.getProfilePic());
        profileDetails.putString("displayName",userProfile.getDisplayName());
        profileDetails.putString("username",userProfile.getUsername().trim());
        profileDetails.putString("email",userProfile.getEmail().trim());
        profileDetails.putString("password",userProfile.getPassword().trim());
        intent.putExtras(profileDetails);
         */
        startActivity(intent);
        finish();
    }

    public Bundle getMoodEventBundle(MoodEvent post) {
        String mid = post.getMid();
        String month = post.userFormattedDate().substring(3);
        String emotion = post.getMoodEmotionalState();

        String collaborators = getStringOfCollaborators(post);
        String reason = post.getReason().orElse("");
        byte[] imgBytes = getImageBytes(post.getImage());
        boolean useLoc = false;  // TODO: implement location once MoodEvent has the field

        Bundle args = new Bundle();
        args.putString("mid", mid);
        args.putString("month", month);
        args.putString("emotion", emotion);

        args.putString("social situation", collaborators);
        args.putString("reason", reason);
        args.putByteArray("image", imgBytes);
        args.putBoolean("location permission", useLoc);

        return args;
    }

    public String getStringOfCollaborators(MoodEvent post) {
        StringBuilder collaboratorsStr = new StringBuilder();
        Optional<ArrayList<String>> collaborators = post.getCollaborators();
        collaborators.ifPresent(list -> {
            for (String item : list) {
                collaboratorsStr.append(item);
                collaboratorsStr.append(",");
            }
        });
        return collaboratorsStr.toString();
    }

    public byte[] getImageBytes(Bitmap img) {
        byte[] imageBytes;
        if (img != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            imageBytes = outputStream.toByteArray();
        } else {
            imageBytes = null;
        }
        return imageBytes;
    }

    @Override
    public void onFragmentFinished() {
        userProfile.getMoodEventBook().updateMoodEvents(); // update mood event book
        // update adapter
        String event_id, event_month;
        for (int i = 0; i < adapter.getCount(); i++) {
            int pos = i;
            MoodEvent event = adapter.getItem(i);
            event_id = event.getMid();
            event_month = event.userFormattedDate().substring(3);
            databaseBestie.getMoodEventByMid(event_id, event_month, (updatedEvent, emot) -> {
                adapter.remove(event);

                event.setSetting(updatedEvent.getSetting());
                event.setCollaborators(updatedEvent.getCollaborators().orElse(new ArrayList<>()));
                event.setReason(updatedEvent.getReason().orElse(""));
                event.setImage(updatedEvent.getImage());
                event.setMood(emot);
                event.setPrivacyOn(updatedEvent.isPrivacyOn());
                adapter.insert(event, pos);
            });
        }
        adapter.notifyDataSetChanged();
    }

    private void showFilterPopup(View view) {
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

        // Filter by emotional state (multiple selections)
        if (!selectedEmotionalStates.isEmpty()) {
            filteredEvents = filteredEvents.stream()
                    .filter(event -> selectedEmotionalStates.stream()
                            .anyMatch(state -> state.equalsIgnoreCase(event.getMoodEmotionalState())))
                    .collect(Collectors.toList());
        }

        // Filter by recent week
        if (filterByRecentWeek) {
            LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
            filteredEvents = filteredEvents.stream()
                    .filter(event -> event.getPostDate().isAfter(oneWeekAgo))
                    .collect(Collectors.toList());
        }

        // Update the adapter with the filtered events
        adapter.clear();
        adapter.addAll(filteredEvents);
        adapter.notifyDataSetChanged();
    }

    private void resetFilters() {
        adapter.notifyDataSetChanged();
        adapter.clear();
        adapter.addAll(userProfile.getMoodEventBook().getMoodEventList());
        userProfile.initializeMoodEventBookFromDatabase(databaseBestie);


        EditText searchEditText = findViewById(R.id.editText_search);
        searchEditText.setText("");

        // Notify the user that filters have been reset
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }

    private void filterByKeyword(String keyword) {
        List<MoodEvent> filteredEvents = new ArrayList<>(userProfile.getMoodEventBook().getMoodEventList());

        // Filter by keyword in the reason text
        if (!keyword.isEmpty()) {
            List<String> keywords = new ArrayList<>();
            keywords.add(keyword);
            filteredEvents = filteredEvents.stream()
                    .filter(event -> event.getReason().isPresent() &&
                            event.getReason().get().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Update the adapter with the filtered events
        adapter.clear();
        adapter.addAll(filteredEvents);
        adapter.notifyDataSetChanged();
    }
}