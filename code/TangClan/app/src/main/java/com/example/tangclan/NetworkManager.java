package com.example.tangclan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

/**
 * A manager that facilitates all network operations
 *
 * USER STORIES:
 *      US 07.01.01
 */
public class NetworkManager {
    /**
     * Classes that implement this interface must define a mechanism for indicating to the user that
     * database-dependent lists cannot be shown because the app is offline
     */
    public interface NetworkUIOperations {
        public void offlineListViewHandler();
    }

    /**
     * Classes that implement this interface must define a mechanism for posting, updating, and
     * removing MoodEvents from the database once the app comes online
     */
    public interface NetworkMoodEventOperations {
        public void post(MoodEvent moodEvent, DatabaseBestie db);
        public void update(MoodEvent moodEvent, DatabaseBestie db);
        public void remove(MoodEvent moodEvent, DatabaseBestie db);
    }
    private final ConnectivityManager connectivityManager;
    private NetworkMonitor monitor;

    /**
     * Constructor for the NetworkManager class
     * @param context
     *      The context passed in order to access system services
     */
    public NetworkManager(Context context) {
        // grab the application context
        Context appContext = context.getApplicationContext();

        // create an instance of the ConnectivityManager
        this.connectivityManager = (ConnectivityManager) appContext
                .getSystemService(ConnectivityManager.class);

        // create an instance of the NetworkMonitor class
        this.monitor = new NetworkMonitor(appContext);
    }

    /**
     * Handles registration of the NetworkMonitor class
     */
    public void registerNetworkMonitor() {
        unregisterNetworkMonitor();
        NetworkRequest request = buildNetworkRequest();

        connectivityManager.registerNetworkCallback(request, monitor);
    }

    /**
     * Unregistering mechanism for the NetworkMonitor class
     */
    public void unregisterNetworkMonitor() {
        try {
            connectivityManager.unregisterNetworkCallback(monitor);
        } catch (IllegalArgumentException e) {
            Log.d("appConnect", "Callback already unregistered");
        }
    }

    /**
     * Builds a network request that requests networks that can connect to internet and have no
     * data usage limits
     * @return
     *      The network request returned by the builder
     */
    public NetworkRequest buildNetworkRequest() {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        // specify the types of networks that the NetworkRequest should return
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // network reaches internet
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED); // unmetered network/no data usage limits

        return builder.build();
    }
}
