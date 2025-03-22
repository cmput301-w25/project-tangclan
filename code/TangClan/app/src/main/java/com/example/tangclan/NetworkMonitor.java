package com.example.tangclan;

import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * A network callback wrapper providing mechanisms to monitor the network
 * USER STORIES:
 *      US 06.01.01
 */
public class NetworkMonitor extends ConnectivityManager.NetworkCallback {
    interface databaseMethodsOnConnect {
        public void addOnConnect();
        public void deleteOnConnect();
        public void updateOnConnect();
    }
    public NetworkMonitor() {
        super();
    }

    @Override
    public void onLost(@NonNull Network network) {
        Log.d("Connectivity", "Lost connection");
    }

    public void onAvalable(@NonNull Network network) {
        Log.d("Connectivity", "Connected to a network");
    }
}
