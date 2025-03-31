package com.example.tangclan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
//import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProfilePageActivityTest {

    private Profile testProfile;

    @Before
    public void setUp() {
        // Reset the singleton instance before each test
        LoggedInUser.resetInstance();

        // Create a proper Profile instance
        Profile realProfile = new Profile();
        realProfile.setUsername("testUser");
        realProfile.setEmail("test@example.com");
        realProfile.setDisplayName("Test User");
        realProfile.setPassword("ValidPass123!"); // Adjust based on your password rules
        realProfile.setProfilePic(bitmapToBase64(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)));

        // Initialize FollowingBook with test data
        FollowingBook followingBook = new FollowingBook();

        // Add followers using the proper addFollower method
        followingBook.addFollower("follower1");
        followingBook.addFollower("follower2");

        // Add following using the proper addFollowing method
        followingBook.addFollowing("following1");
        followingBook.addFollowing("following2");
        followingBook.addFollowing("following3");

        // Set up username mappings using the proper methods
        followingBook.addEntryToFollowerMap("follower1", "userAlice");
        followingBook.addEntryToFollowerMap("follower2", "userBob");

        followingBook.addEntryToFollowingMap("following1", "userCharlie");
        followingBook.addEntryToFollowingMap("following2", "userDana");
        followingBook.addEntryToFollowingMap("following3", "userEve");

        // Add some follow requests
        followingBook.addRequestingFollower("pendingFollower1");

        // Add some outgoing follow requests
        followingBook.addMyRequest("requestedUser1");

        realProfile.setFollowingBook(followingBook);

        // Initialize MoodEventBook with test events
        MoodEventBook moodEventBook = new MoodEventBook();
        moodEventBook.addMoodEvent(new MoodEvent("Happy"));
        moodEventBook.addMoodEvent(new MoodEvent("Sad"));
        realProfile.setMoodEventBook(moodEventBook);

        // Set this as the logged in user
        LoggedInUser.getInstance().setProfileData(realProfile);

    }

    @Test
    public void testProfileInfoDisplay() {
        // Launch the activity
        ActivityScenario.launch(ProfilePageActivity.class);

        // Verify profile information is displayed correctly
        onView(withId(R.id.username))
                .check(matches(withText("@testUser")));
        onView(withId(R.id.nameDisplay))
                .check(matches(withText("Test User")));
        onView(withId(R.id.pfpView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEditProfileButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfilePageActivity.class);
        ActivityScenario<ProfilePageActivity> scenario = ActivityScenario.launch(intent);

        // Click the edit profile button
        onView(withId(R.id.button_edit_profile)).perform(click());

        // Verify we're taken to the edit profile activity
        // (This would ideally be verified with an intent mock)
        scenario.onActivity(activity -> {
            assert(activity.isFinishing()); // Current activity should finish
        });
    }

    @Test
    public void testMoodEventListDisplay() {
        ActivityScenario.launch(ProfilePageActivity.class);

        // Verify mood events are displayed
        onView(withId(R.id.listview_profile_history))
                .check(matches(isDisplayed()));

        onData(anything())
                .inAdapterView(withId(R.id.listview_profile_history))
                .atPosition(0)
                .check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.listview_profile_history))
                .atPosition(1)
                .check(matches(isDisplayed()));
    }

    @Test
    public void testMoodEventLongClickOptions() {
        ActivityScenario.launch(ProfilePageActivity.class);

        // Long click on first mood event
        onData(anything())
                .inAdapterView(withId(R.id.listview_profile_history))
                .atPosition(0)
                .perform(longClick());

        // Verify dialog appears with options
        onView(withText("Do you want to edit or delete this mood event?"))
                .check(matches(isDisplayed()));
        onView(withText("Edit")).check(matches(isDisplayed()));
        onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withText("Cancel")).check(matches(isDisplayed()));
    }

    @Test
    public void testFilterFunctionality() {
        // Set up test data with different emotional states
        testProfile.getMoodEventBook().getMoodEventList().clear();
        testProfile.getMoodEventBook().addMoodEvent(new MoodEvent("Happy"));
        testProfile.getMoodEventBook().addMoodEvent(new MoodEvent("Sad"));
        testProfile.getMoodEventBook().addMoodEvent(new MoodEvent("Angry"));

        ActivityScenario.launch(ProfilePageActivity.class);

        // Wait for data to load
        waitFor(1000);

        // Open filter popup
        onView(withId(R.id.filter)).perform(click());

        // Wait for popup to appear
        waitFor(500);

        // Verify popup is displayed by checking for the emotional states list
        onView(withId(R.id.emotional_states_list))
                .check(matches(isDisplayed()));

        // Select "Happy" emotional state
        onData(anything())
                .inAdapterView(withId(R.id.emotional_states_list))
                .atPosition(0) // Assuming "Happy" is first in the list
                .perform(click());

        // Apply the filter
        onView(withId(R.id.apply_filter_button)).perform(click());

        // Wait for filter to apply
        waitFor(500);

        // Verify only "Happy" events are shown
        onView(withId(R.id.listview_profile_history))
                .check((view, noViewFoundException) -> {
                    ListView listView = (ListView) view;
                    int count = listView.getAdapter().getCount();
                    assertTrue("Filter didn't work correctly", count > 0 && count < 3);

                    // Verify all shown events are "Happy"
                    for (int i = 0; i < count; i++) {
                        MoodEvent event = (MoodEvent) listView.getAdapter().getItem(i);
                        assertEquals("Happy", event.getMoodEmotionalState());
                    }
                });
    }

    private void waitFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSearchFunctionality() {
        // Create test events with different reasons
        MoodEvent event1 = new MoodEvent("Happy");
        event1.setReason("Feeling great today");

        MoodEvent event2 = new MoodEvent("Sad");
        event2.setReason("Not feeling well");

        MoodEvent event3 = new MoodEvent("Happy"); // Same emotion as event1 but different reason
        event3.setReason("Unique search text"); // This is what we'll search for

        // Add events to profile
        testProfile.getMoodEventBook().getMoodEventList().clear(); // Clear existing events
        testProfile.getMoodEventBook().addMoodEvent(event1);
        testProfile.getMoodEventBook().addMoodEvent(event2);
        testProfile.getMoodEventBook().addMoodEvent(event3);

        // Launch activity
        ActivityScenario.launch(ProfilePageActivity.class);

        // Wait for data to load
        try {
            Thread.sleep(1000); // Brief delay for list to populate
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify initial count (all 3 events)
        onView(withId(R.id.listview_profile_history))
                .check((view, noViewFoundException) -> {
                    ListView listView = (ListView) view;
                    assertTrue("Initial events not loaded", listView.getAdapter().getCount() == 3);
                });

        // Search for unique reason text
        onView(withId(R.id.editText_search))
                .perform(typeText("Unique search text"));

        // Wait for search to complete
        try {
            Thread.sleep(500); // Brief delay for search to execute
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify only the matching event is shown
        onView(withId(R.id.listview_profile_history))
                .check((view, noViewFoundException) -> {
                    ListView listView = (ListView) view;
                    assertTrue("Search didn't filter correctly",
                            listView.getAdapter().getCount() == 1);

                    // Additional verification that the correct item remains
                    MoodEvent event = (MoodEvent) listView.getAdapter().getItem(0);
                    assertTrue("Wrong event filtered",
                            event.getReason().orElse("").contains("Unique search text"));
                });
    }

    @Test
    public void testFollowersFollowingClick() {
        // Set up test following data
        FollowingBook followingBook = new FollowingBook();
        followingBook.addFollower("follower1");
        followingBook.addFollowing("following1");
        testProfile.setFollowingBook(followingBook);

        ActivityScenario.launch(ProfilePageActivity.class);

        // Click followers count
        onView(withId(R.id.follower_count)).perform(click());
        onView(withText("Followers")).check(matches(isDisplayed()));
        onView(withText("follower1")).check(matches(isDisplayed()));

        // Go back
        Espresso.pressBack();

        // Click following count
        onView(withId(R.id.following_count)).perform(click());
        onView(withText("Following")).check(matches(isDisplayed()));
        onView(withText("following1")).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavBarFunctionality() {
        ActivityScenario.launch(ProfilePageActivity.class);


        onView(withId(R.id.imgHome)).perform(click());
        // Verify home screen opens

        onView(withId(R.id.fabAdd)).perform(click());
        // Verify "add post" action

        onView(withId(R.id.imgNotification)).perform(click());
        // Verify notifications screen

        onView(withId(R.id.imgProfile)).perform(click());
        // Verify profile screen (may reload current activity)
    }

    @Test
    public void testNavBarIconsVisible() {
        ActivityScenario.launch(ProfilePageActivity.class);

        // Verify all nav icons are displayed
        onView(withId(R.id.imgMap))
                .check(matches(isDisplayed()));
        onView(withId(R.id.imgHome))
                .check(matches(isDisplayed()));
        onView(withId(R.id.fabAdd))
                .check(matches(isDisplayed()));
        onView(withId(R.id.imgNotification))
                .check(matches(isDisplayed()));
        onView(withId(R.id.imgProfile))
                .check(matches(isDisplayed()));
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}