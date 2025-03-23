package com.example.tangclan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * A Network Callback wrapper that provides additional functionality for handling network events
 * Designed specifically for use in Moodly
 *
 * RELATED USER STORIES
 *      US 07.01.01
 */
public class NetworkMonitor extends ConnectivityManager.NetworkCallback {
    private final Context appContext;
    /**
     * Constructor for the NetworkMonitor class
     */
    public NetworkMonitor(Context context) {
        super();

        appContext = context.getApplicationContext();
    }

    /**
     * Callback when connection is lost
     * @param network
     *      The network that no longer satisfies the request
     */
    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);

        Toast.makeText(appContext, "Connection lost", Toast.LENGTH_SHORT).show();
        Log.d("appConnect", "lost");
    }

    /**
     * Callback when connection is available/gained
     * @param network
     *      The network that satisfies this request
     */
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);

        Toast.makeText(appContext, "Connected to a network", Toast.LENGTH_SHORT).show();
        Log.d("appConnect", "Connected");
    }

}
