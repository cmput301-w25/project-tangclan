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

    private static final int TAB_HOME = 0;
    private static final int TAB_PROFILE = 1;
    private static final int TAB_ADD = 2;

    private static int currentActiveTab = -1;

    private static final int TAB_FOLLOW=3;
    public static void setupNavBar(Activity activity) {
        // Find all navbar items
        ImageView homeIcon = activity.findViewById(R.id.imgHome);
        ImageView profileIcon = activity.findViewById(R.id.imgProfile);
        ImageView addIcon = activity.findViewById(R.id.fabAdd);
        ImageView notifIcon = activity.findViewById(R.id.imgNotification);

        Class<?> activityClass = activity.getClass();
        if (activityClass.equals(FeedActivity.class)) {
            currentActiveTab = TAB_HOME;
        } else if (activityClass.equals(ProfilePageActivity.class)) {
            currentActiveTab = TAB_PROFILE;
        } else if (activityClass.equals(AddEmotionActivity.class)) {
            currentActiveTab = TAB_ADD;
        } else if (activityClass.equals(FollowActivity.class)) {
            currentActiveTab= TAB_FOLLOW;
        }

        updateIcons(homeIcon, profileIcon, addIcon);


        if (homeIcon != null) {
            homeIcon.setOnClickListener(v -> {
                navigateToActivity(activity, FeedActivity.class);
            });
        }

        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                navigateToActivity(activity, ProfilePageActivity.class);
            });
        }

        if (addIcon != null) {
            addIcon.setOnClickListener(v -> {
                navigateToActivity(activity, AddEmotionActivity.class);
            });
        }
        if (notifIcon != null) {
            notifIcon.setOnClickListener(v -> {
                navigateToActivity(activity, FollowActivity.class);
            });
        }

    }

    private static void updateIcons(ImageView homeIcon, ImageView profileIcon, ImageView addIcon) {
        if (homeIcon != null) {
            homeIcon.setImageResource(
                    currentActiveTab == TAB_HOME ?
                            R.drawable.outline_home_24 :
                            R.drawable.baseline_home_24
            );
        }

        if (profileIcon != null) {
            profileIcon.setImageResource(
                    currentActiveTab == TAB_PROFILE ?
                            R.drawable.outline_person_24 :
                            R.drawable.baseline_person_24
            );
        }

        if (addIcon != null) {
            addIcon.setImageResource(
                    currentActiveTab == TAB_ADD ?
                            R.drawable.baseline_add_box_24 :
                            R.drawable.outline_add_box_24
            );
        }

        if (addIcon != null) {
            addIcon.setImageResource(
                    R.drawable.baseline_notifications_24
            );
        }
    }

    private static void navigateToActivity(Activity currentActivity, Class<?> destinationClass) {
        if (currentActivity.getClass().equals(destinationClass)) {
            return;
        }

        Intent intent = new Intent(currentActivity, destinationClass);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
}