package com.example.tangclan;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class CustomInfoWindow extends InfoWindow {
    private String mood;
    private String timestamp;
    private String user;

    public CustomInfoWindow(MapView mapView, String mood, String timestamp, String username) {
        super(R.layout.custom_info_window, mapView);
        this.mood = mood;
        this.timestamp = timestamp;
        this.user = username;
    }

    @Override
    public void onOpen(Object item) {
        View view = getView();
        TextView username = view.findViewById(R.id.username);
        TextView title = view.findViewById(R.id.title);
        TextView timestampy = view.findViewById(R.id.timestamp);

        String message = "Feeling " + mood;

        username.setText(user);
        title.setText(message);
        timestampy.setText(timestamp);

    }

    @Override
    public void onClose() {}
}
