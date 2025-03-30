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
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.util.GeoPoint;

public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private Button personalModeBtn, friendsModeBtn;
    private SeekBar distanceSeekBar;
    private TextView distanceLabel;

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

        personalModeBtn.setOnClickListener(v -> switchToPersonalMode());
        friendsModeBtn.setOnClickListener(v -> switchToFriendsMode());
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceLabel.setText("Filter Distance: " + progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                filterMoodEventsByDistance(seekBar.getProgress());
            }
        });
    }

    private void setupLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Optional: Add a compass overlay
        CompassOverlay compassOverlay = new CompassOverlay(this, mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
    }

    private void switchToPersonalMode() {
        Toast.makeText(this, "Switched to Personal Mode", Toast.LENGTH_SHORT).show();
        // TODO: Load personal mood events
    }

    private void switchToFriendsMode() {
        Toast.makeText(this, "Switched to Friends Mode", Toast.LENGTH_SHORT).show();
        // TODO: Load friends' mood events
    }

    private void filterMoodEventsByDistance(int distanceKm) {
        Toast.makeText(this, "Filtering events within " + distanceKm + " km", Toast.LENGTH_SHORT).show();
        // TODO: Apply distance filter to displayed markers
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

    public void addMoodMarker(MapView mapView, double latitude, double longitude, String mood, String timestamp, int emojiDrawableResId) {
        // Create a new marker for the map
        Marker marker = new Marker(mapView);
        GeoPoint point = new GeoPoint(latitude, longitude);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // Set the title and snippet (this will appear in the info window)
        marker.setTitle(mood); // Mood message (what the marker represents)
        marker.setSnippet(timestamp); // Timestamp for when the mood was recorded

        // Set a custom emoji as the marker icon
        Drawable markerIcon = ContextCompat.getDrawable(this, emojiDrawableResId);
        marker.setIcon(markerIcon);

        // Show the default info window when the marker is clicked
        marker.setOnMarkerClickListener((clickedMarker, mapView1) -> {
            clickedMarker.showInfoWindow(); // This will show the default popup
            return true;
        });

        // Add the marker to the map
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

}
