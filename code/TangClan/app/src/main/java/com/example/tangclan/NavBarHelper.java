package com.example.tangclan;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

/**
 * Helper class to manage navigation bar functionality across the app.
 * This class provides methods to set up and handle click events for the navbar items.
 */
public class NavBarHelper {

    /**
     * Sets up the navigation bar for the given activity.
     * This method finds all navbar items and sets their click listeners.
     *
     * @param activity The activity containing the navbar
     */
    public static void setupNavBar(Activity activity) {
        // Find all navbar items
        ImageView homeIcon = activity.findViewById(R.id.imgHome);
        ImageView profileIcon = activity.findViewById(R.id.imgProfile);
        ImageView addIcon = activity.findViewById(R.id.fabAdd);

        // Set click listeners for each navbar item
        if (homeIcon != null) {
            homeIcon.setOnClickListener(v -> navigateToActivity(activity, FeedActivity.class));
        }

        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> navigateToActivity(activity, ProfilePageActivity.class));
        }

        if (addIcon != null) {
            addIcon.setOnClickListener(v -> navigateToActivity(activity, AddEmotionActivity.class));
        }
    }

    /**
     * Navigates to the specified activity.
     * This method prevents navigating to the same activity.
     *
     * @param currentActivity The current activity
     * @param destinationClass The class of the destination activity
     */
    private static void navigateToActivity(Activity currentActivity, Class<?> destinationClass) {
        if (currentActivity.getClass().equals(destinationClass)) {
            return;
        }

        Intent intent = new Intent(currentActivity, destinationClass);
        currentActivity.startActivity(intent);
        if (!currentActivity.isTaskRoot()) {
            currentActivity.finish(); // Finish current activity to prevent stacking
        }
    }

}