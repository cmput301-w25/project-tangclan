package com.example.tangclan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AddLocationActivity extends AppCompatActivity {
    private MapView mapView;
    private AutoCompleteTextView searchBar;
    private Button searchButton;
    private ListView suggestionsList;
    private MyLocationNewOverlay locationOverlay;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> suggestions = new ArrayList<>();
    private ArrayList<GeoPoint> suggestionCoords = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private long lastTypedTime = 0;  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));
        setContentView(R.layout.activity_add_location);

        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(53.5232, -113.5267)); // Edmonton

        // Enable user location tracking
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Add compass
        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        suggestionsList = findViewById(R.id.suggestionsList);

        // Setup adapter for ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        suggestionsList.setAdapter(adapter);

        // Handle text changes for autocomplete
        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 2) { // Start searching after 3 characters
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTypedTime > 500) { // Debounce to avoid too many requests
                        lastTypedTime = currentTime;
                        getAutocompleteResults(charSequence.toString());
                    }
                } else {
                    suggestionsList.setVisibility(View.GONE); // Hide suggestions when no text
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        // Handle suggestion selection
        suggestionsList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            searchBar.setText(suggestions.get(position)); // Update search bar
            searchBar.setSelection(searchBar.getText().length()); // Move cursor to end
            suggestionsList.setVisibility(View.GONE); // Hide suggestions
        });

        // Handle search button click
        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            if (!query.isEmpty()) {
                suggestionsList.setVisibility(View.GONE); // Hide suggestions when search is clicked
                searchLocation(query);
            }
        });
    }

    private void enableUserLocation() {
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);
        mapView.invalidate();
    }

    private void getAutocompleteResults(String query) {
        new Thread(() -> {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + query + "&limit=5&addressdetails=1";
            Request request = new Request.Builder().url(url).build();

            try {
                Response response = client.newCall(request).execute();
                if (response.body() != null) {
                    String jsonData = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonData);

                    suggestions.clear();
                    suggestionCoords.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject location = jsonArray.getJSONObject(i);
                        String displayName = location.getString("display_name");
                        double lat = location.getDouble("lat");
                        double lon = location.getDouble("lon");

                        suggestions.add(displayName);
                        suggestionCoords.add(new GeoPoint(lat, lon));
                    }

                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        suggestionsList.setVisibility(suggestions.isEmpty() ? View.GONE : View.VISIBLE);
                    });
                }
            } catch (IOException | org.json.JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void searchLocation(String query) {
        int index = suggestions.indexOf(query); // Find index of selected location
        if (index != -1) {
            GeoPoint point = suggestionCoords.get(index);
            placeMarker(point.getLatitude(), point.getLongitude());
        } else {
            new Thread(() -> {
                String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + query + "&limit=1";
                Request request = new Request.Builder().url(url).build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.body() != null) {
                        String jsonData = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonData);

                        if (jsonArray.length() > 0) {
                            JSONObject location = jsonArray.getJSONObject(0);
                            double lat = location.getDouble("lat");
                            double lon = location.getDouble("lon");

                            runOnUiThread(() -> placeMarker(lat, lon));
                        } else {
                            Log.d("LocationSearch", "No results found for query: " + query);
                        }
                    }
                } catch (IOException | org.json.JSONException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void placeMarker(double lat, double lon) {
        GeoPoint point = new GeoPoint(lat, lon);
        mapView.getController().setCenter(point);
        mapView.getController().setZoom(15.0);

        // Remove old markers
        mapView.getOverlays().clear();
        if (locationOverlay != null) mapView.getOverlays().add(locationOverlay);

        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle("Selected Location");
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        }
    }
}
