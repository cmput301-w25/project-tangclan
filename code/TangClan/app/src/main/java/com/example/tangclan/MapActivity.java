package com.example.tangclan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_map_activity);

        // Initialize OSMDroid
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));

        // Map View
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

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
        NavBarHelper.setupNavBar(this);

        db = new DatabaseBestie();
        personalModeBtn.setSelected(true);
        switchToPersonalMode();

        personalModeBtn.setOnClickListener(v -> {
            personalModeBtn.setSelected(true);
            friendsModeBtn.setSelected(false);
            switchToPersonalMode();
        });

        friendsModeBtn.setOnClickListener(v -> {
            personalModeBtn.setSelected(false);
            friendsModeBtn.setSelected(true);
            switchToFriendsMode();
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

        loggedInUser = LoggedInUser.getInstance();

        // Initialize the mood event book if it doesn't exist
        if (loggedInUser.getMoodEventBook() == null) {
            loggedInUser.setMoodEventBook(new MoodEventBook());
        }

        // Fetch the user's following data from the database
        loggedInUser.initializeFollowingBookFromDatabase(db);
        FollowingBook userFollowingBook = loggedInUser.getFollowingBook();
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
        Toast.makeText(this, "Switched to Personal Mode", Toast.LENGTH_SHORT).show();
        mapView.getOverlays().clear();

        MoodEventBook moodEventBook = loggedInUser.getMoodEventBook();
        for (MoodEvent event : moodEventBook.getAllMoodEvents()) {
            if (event.hasGeolocation() && isWithinDistance(event.getLatitude(), event.getLongitude())) { // Filter by distance
                addMoodMarker(mapView,
                        event.getLatitude(), event.getLongitude(),
                        event.getLocationName(),
                        "You were feeling " + event.getMood().getEmotion(),
                        event.getPostDate() + " " + event.getPostTime(),
                        getEmojiDrawableResId(event.getMood().getEmotion()),
                        loggedInUser.getUsername());
            }
        }
        mapView.invalidate(); // Refresh the map
    }

    private void switchToFriendsMode() {
        Toast.makeText(this, "Switched to Friends Mode", Toast.LENGTH_SHORT).show();
        mapView.getOverlays().clear();

        // Get the most recent mood event with a location for the logged-in user
        MoodEventBook moodEventBook = loggedInUser.getMoodEventBook();
        MoodEvent validMoodEvent = getMostRecentMoodEventWithLocation(moodEventBook.getAllMoodEvents());

        if (validMoodEvent != null) {
            addMoodMarker(mapView, validMoodEvent.getLatitude(), validMoodEvent.getLongitude(),
                    validMoodEvent.getLocationName(), "You were feeling " + validMoodEvent.getMood().getEmotion(),
                    validMoodEvent.getPostDate() + " " + validMoodEvent.getPostTime(),
                    getEmojiDrawableResId(validMoodEvent.getMood().getEmotion()), loggedInUser.getUsername());

            mapView.getController().setCenter(new GeoPoint(validMoodEvent.getLatitude(), validMoodEvent.getLongitude()));
        }

        // Get friends' most recent mood events with location
        FollowingBook followingBook = loggedInUser.getFollowingBook();
        final AtomicInteger counter = new AtomicInteger(followingBook.getFollowing().size());
        final Map<String, MoodEvent> uidToMoodEvent = new HashMap<>();

        for (String followingUid : followingBook.getFollowing()) {
            db.getAllMoodEvents(followingUid, (moodEventBookFriend) -> {
                if (moodEventBookFriend != null) {
                    MoodEvent mostRecentValidEvent = getMostRecentMoodEventWithLocation(moodEventBookFriend);
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

        for (int i = events.size() - 1; i >= 0; i--) { // Start from most recent
            MoodEvent event = events.get(i);
            if (event.hasGeolocation()) {
                return event;
            }
        }
        return null; // No valid event found
    }
}
