package com.example.tangclan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.util.GeoPoint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 7;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private Button personalModeBtn, friendsModeBtn;
    private SeekBar distanceSeekBar;
    private TextView distanceLabel;
    private DatabaseBestie db;
    private LoggedInUser loggedInUser;
    private ImageView filterExt;

    private List<String> currentEmotionalStateFilters = new ArrayList<>();
    private boolean filterByRecentWeek = false;

    private SparseBooleanArray lastCheckedStates = new SparseBooleanArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_map_activity);

        // Initialize OSMDroid
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        filterExt = findViewById(R.id.filterExt);



        // Map View
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        filterExt.setOnClickListener(v -> showFilterPopup(v));

        // Set Initial Map Location (Edmonton)
        mapView.getController().setZoom(12.0);
        mapView.getController().setCenter(new GeoPoint(53.5461, -113.4938));

        // Request Permissions
        requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});

        // Initialize Overlays
        setupLocationOverlay();

        // Mode Selection Buttons
        personalModeBtn = findViewById(R.id.personalModeBtn);
        friendsModeBtn = findViewById(R.id.friendsModeBtn);
        distanceSeekBar = findViewById(R.id.distanceSeekBar);
        distanceLabel = findViewById(R.id.distanceLabel);




        db = new DatabaseBestie();

        loggedInUser = LoggedInUser.getInstance();

        // Initialize the mood event book if it doesn't exist
        if (loggedInUser.getMoodEventBook() == null) {
            loggedInUser.setMoodEventBook(new MoodEventBook());
        }

        // Fetch the user's following data from the database
        loggedInUser.initializeFollowingBookFromDatabase(db);
        FollowingBook followingBook = loggedInUser.getFollowingBook();

        personalModeBtn.setSelected(true);
        switchToPersonalMode();

        personalModeBtn.setOnClickListener(v -> {
            personalModeBtn.setSelected(true);
            friendsModeBtn.setSelected(false);
            switchToPersonalMode();
        });

        friendsModeBtn.setOnClickListener(v -> {
            if (followingBook.getFollowerCount() != 0) {
            personalModeBtn.setSelected(false);
            friendsModeBtn.setSelected(true);
            switchToFriendsMode();}
            else {
                Toast.makeText(this, "Follow your friends to see their moods!", Toast.LENGTH_LONG).show();
            }
        });

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceLabel.setText("Filter Distance: " + progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Rerun the currently selected mode
                if (personalModeBtn.isSelected()) {
                    switchToPersonalMode();
                } else {
                    switchToFriendsMode();
                }
            }
        });

        NavBarHelper.setupNavBar(this);

        loggedInUser = LoggedInUser.getInstance();

        // Initialize the mood event book if it doesn't exist
        if (loggedInUser.getMoodEventBook() == null) {
            loggedInUser.setMoodEventBook(new MoodEventBook());
        }

        // Fetch the user's following data from the database
        loggedInUser.initializeFollowingBookFromDatabase(db);
    }

    private void setupLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // add a compass overlay
        CompassOverlay compassOverlay = new CompassOverlay(this, mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
    }

    private void switchToPersonalMode() {
        mapView.getOverlays().clear();
        setupLocationOverlay(); // Re-add location overlay

        MoodEventBook moodEventBook = loggedInUser.getMoodEventBook();
        List<MoodEvent> filteredEvents = applyFiltersToEvents(moodEventBook.getAllMoodEvents());

        if (moodEventBook.getMoodEventCount() == 0) {
            Toast.makeText(this, "You haven't shared any moods yet. Come back here once you do to see them!", Toast.LENGTH_LONG).show();
        } else {

        for (MoodEvent event : filteredEvents) {
            if (event.hasGeolocation() && isWithinDistance(event.getLatitude(), event.getLongitude())) {
                addMoodMarker(mapView,
                        event.getLatitude(), event.getLongitude(),
                        event.getLocationName(),
                        "You were feeling " + event.getMood().getEmotion(),
                        event.getPostDate() + " " + event.getPostTime(),
                        getEmojiDrawableResId(event.getMood().getEmotion()),
                        loggedInUser.getUsername());
            }
        }}
        mapView.invalidate();
    }
    private void switchToFriendsMode() {
        mapView.getOverlays().clear();
        setupLocationOverlay(); // Re-add location overlay

        // Get filtered events for logged-in user
        MoodEventBook moodEventBook = loggedInUser.getMoodEventBook();
        List<MoodEvent> filteredUserEvents = applyFiltersToEvents(moodEventBook.getAllMoodEvents());
        MoodEvent validMoodEvent = getMostRecentMoodEventWithLocation((ArrayList<MoodEvent>) filteredUserEvents);

        if (validMoodEvent != null && isWithinDistance(validMoodEvent.getLatitude(), validMoodEvent.getLongitude())) {
            addMoodMarker(mapView, validMoodEvent.getLatitude(), validMoodEvent.getLongitude(),
                    validMoodEvent.getLocationName(), "You were feeling " + validMoodEvent.getMood().getEmotion(),
                    validMoodEvent.getPostDate() + " " + validMoodEvent.getPostTime(),
                    getEmojiDrawableResId(validMoodEvent.getMood().getEmotion()), loggedInUser.getUsername());

            mapView.getController().setCenter(new GeoPoint(validMoodEvent.getLatitude(), validMoodEvent.getLongitude()));
        }

        // Get friends' filtered events
        FollowingBook followingBook = loggedInUser.getFollowingBook();
        final AtomicInteger counter = new AtomicInteger(followingBook.getFollowing().size());
        final Map<String, MoodEvent> uidToMoodEvent = new HashMap<>();

        for (String followingUid : followingBook.getFollowing()) {
            db.getAllMoodEvents(followingUid, (moodEventBookFriend) -> {
                if (moodEventBookFriend != null) {
                    List<MoodEvent> filteredFriendEvents = applyFiltersToEvents(moodEventBookFriend);
                    MoodEvent mostRecentValidEvent = getMostRecentMoodEventWithLocation((ArrayList<MoodEvent>) filteredFriendEvents);
                    if (mostRecentValidEvent != null && isWithinDistance(mostRecentValidEvent.getLatitude(), mostRecentValidEvent.getLongitude())) {
                        uidToMoodEvent.put(followingUid, mostRecentValidEvent);
                    }
                }

                if (counter.decrementAndGet() == 0) {
                    onAllMoodEventsFetched(uidToMoodEvent);
                }
            });
        }
    }

    private List<MoodEvent> applyFiltersToEvents(List<MoodEvent> events) {
        List<MoodEvent> filteredEvents = new ArrayList<>(events);

        // Apply emotional state filters
        if (!currentEmotionalStateFilters.isEmpty()) {
            filteredEvents = Filter.filterByEmotionalState(filteredEvents, currentEmotionalStateFilters);
        }

        // Apply time filter
        if (filterByRecentWeek) {
            LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
            filteredEvents = Filter.filterByTimeRange(filteredEvents, oneWeekAgo, LocalDate.now());
        }

        return filteredEvents;
    }



    // This method will be called when all mood events have been fetched
    private void onAllMoodEventsFetched(Map<String, MoodEvent> uidToMoodEvent) {
        // Iterate through the map of followed users' most recent mood events
        for (Map.Entry<String, MoodEvent> entry : uidToMoodEvent.entrySet()) {
            String username = entry.getKey();
            MoodEvent event = entry.getValue();

            if (event != null) {
                addMoodMarker(mapView, event.getLatitude(), event.getLongitude(), event.getLocationName(),
                        "Feeling " + event.getMood().getEmotion(), event.getPostDate() + " " + event.getPostTime(),
                        getEmojiDrawableResId(event.getMood().getEmotion()), username);
            }
        }
    }


    private void requestPermissionsIfNecessary(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            setupLocationOverlay();
        }
    }

    public void addMoodMarker(MapView mapView, double latitude, double longitude, String locationName, String mood, String timestamp, int emojiDrawableResId, String username) {
        // Create a new marker for the map
        Marker marker = new Marker(mapView);
        GeoPoint point = new GeoPoint(latitude, longitude);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // Set the title and snippet (this will appear in the info window)
        marker.setTitle(mood); // Mood message (what the marker represents)
        marker.setSubDescription(locationName);
        marker.setSnippet(timestamp); // Timestamp for when the mood was recorded

        // Set a custom emoji as the marker icon
        Drawable markerIcon = ContextCompat.getDrawable(this, emojiDrawableResId);
        marker.setIcon(markerIcon);

        // Add the username on top of the marker in the info window
        marker.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubbl, mapView));
        marker.setOnMarkerClickListener((clickedMarker, mapView1) -> {
            if (username != null) {
                clickedMarker.setSubDescription(username + "\n" + clickedMarker.getSubDescription()); // Add username above the info window
            }
            clickedMarker.showInfoWindow();
            return true;
        });

        // Add the marker to the map
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private int getEmojiDrawableResId(String mood) {
        switch (mood) {
            case "happy":
                return R.drawable.ic_emotion_happy;
            case "sad":
                return R.drawable.ic_emotion_sad;
            case "angry":
                return R.drawable.ic_emotion_angry;
            case "anxious":
                return R.drawable.ic_emotion_anxious;
            case "ashamed":
                return R.drawable.ic_emotion_ashamed;
            case "confused":
                return R.drawable.ic_emotion_confused;
            case "calm":
                return R.drawable.ic_emotion_calm;
            case "disgusted":
                return R.drawable.ic_emotion_disgusted;
            case "surprised":
                return R.drawable.ic_emotion_surprised;
            case "terrified":
                return R.drawable.ic_emotion_terrified;
            case "noidea":
                return R.drawable.ic_emotion_no_idea;
        }

        return R.drawable.ic_emotion_no_idea;
    }

    private boolean isWithinDistance(double latitude, double longitude) {
        GeoPoint userLocation;

        if (myLocationOverlay.getMyLocation() != null) {
            userLocation = myLocationOverlay.getMyLocation(); // Device location
        } else {
            MoodEvent mostRecentEvent = loggedInUser.getMoodEventBook().getMostRecentMoodEvent();
            if (mostRecentEvent == null) return false;
            userLocation = new GeoPoint(mostRecentEvent.getLatitude(), mostRecentEvent.getLongitude());
        }

        GeoPoint eventLocation = new GeoPoint(latitude, longitude);
        double distance = userLocation.distanceToAsDouble(eventLocation) / 1000; // Convert to kilometers

        return distance <= distanceSeekBar.getProgress();
    }


    private MoodEvent getMostRecentMoodEventWithLocation(ArrayList<MoodEvent> moodEventBook) {
        if (moodEventBook == null) return null;

        ArrayList<MoodEvent> events = moodEventBook;
        if (events == null || events.isEmpty()) return null;

        for (int i = events.size() - 1; i >= 0; i--) {
            MoodEvent event = events.get(i);
            if (event.hasGeolocation()) {
                return event;
            }
        }
        return null;
    }

    private void showFilterPopup(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.filter_popup, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        CheckBox selectAllCheckbox = popupView.findViewById(R.id.select_all_checkbox);
        CheckBox filterRecentWeekCheckbox = popupView.findViewById(R.id.filter_recent_week);
        ListView emotionalStatesList = popupView.findViewById(R.id.emotional_states_list);
        Button applyFilterButton = popupView.findViewById(R.id.apply_filter_button);
        Button resetFiltersButton = popupView.findViewById(R.id.button_reset_filters);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                getResources().getStringArray(R.array.emotional_states));
        emotionalStatesList.setAdapter(adapter);


        filterRecentWeekCheckbox.setChecked(filterByRecentWeek);


        String[] emotionalStates = getResources().getStringArray(R.array.emotional_states);
        for (int i = 0; i < emotionalStates.length; i++) {
            emotionalStatesList.setItemChecked(i, currentEmotionalStateFilters.contains(emotionalStates[i]));
        }


        selectAllCheckbox.setChecked(currentEmotionalStateFilters.size() == emotionalStates.length);

        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < emotionalStatesList.getCount(); i++) {
                emotionalStatesList.setItemChecked(i, isChecked);
            }
        });

        applyFilterButton.setOnClickListener(v -> {
            currentEmotionalStateFilters.clear();
            SparseBooleanArray checkedItems = emotionalStatesList.getCheckedItemPositions();
            for (int i = 0; i < emotionalStatesList.getCount(); i++) {
                if (emotionalStatesList.isItemChecked(i)) {
                    currentEmotionalStateFilters.add(emotionalStatesList.getItemAtPosition(i).toString());
                }
            }

            filterByRecentWeek = filterRecentWeekCheckbox.isChecked();

            if (personalModeBtn.isSelected()) {
                switchToPersonalMode();
            } else {
                switchToFriendsMode();
            }

            popupWindow.dismiss();
        });

        resetFiltersButton.setOnClickListener(v -> {
            currentEmotionalStateFilters.clear();
            filterByRecentWeek = false;

            for (int i = 0; i < emotionalStatesList.getCount(); i++) {
                emotionalStatesList.setItemChecked(i, false);
            }
            selectAllCheckbox.setChecked(false);
            filterRecentWeekCheckbox.setChecked(false);

            if (personalModeBtn.isSelected()) {
                switchToPersonalMode();
            } else {
                switchToFriendsMode();
            }

            Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
        });
    }
}
