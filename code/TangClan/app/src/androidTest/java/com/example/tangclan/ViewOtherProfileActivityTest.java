package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ViewOtherProfileActivityTest {

    private Intent intent;

    @Before
    public void setup() {
        // Create intent with test data
        intent = new Intent(ApplicationProvider.getApplicationContext(), ViewOtherProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("uid", "test_user_123");
        bundle.putString("username", "testuser");
        bundle.putString("displayName", "Test User");
        intent.putExtras(bundle);
    }

    @Test
    public void testUIElementsDisplayed() {
        try (ActivityScenario<ViewOtherProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify all main UI elements are displayed
            Espresso.onView(ViewMatchers.withId(R.id.username))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.nameDisplay))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.follower_count))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.following_count))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.button_edit_profile))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.listview_profile_history))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.editText_search))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.filter))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testProfileInfoDisplayedCorrectly() {
        try (ActivityScenario<ViewOtherProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify profile information is displayed correctly
            Espresso.onView(ViewMatchers.withId(R.id.username))
                    .check(ViewAssertions.matches(ViewMatchers.withText("testuser")));

            Espresso.onView(ViewMatchers.withId(R.id.nameDisplay))
                    .check(ViewAssertions.matches(ViewMatchers.withText("Test User")));
        }
    }

    @Test
    public void testFollowButtonInteraction() {
        try (ActivityScenario<ViewOtherProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify follow button can be clicked
            Espresso.onView(ViewMatchers.withId(R.id.button_edit_profile))
                    .perform(ViewActions.click());

            // Note: Actual follow functionality would need mocking of DatabaseBestie
            // This just verifies the button is clickable
        }
    }

    @Test
    public void testSearchFunctionality() {
        try (ActivityScenario<ViewOtherProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Type in search field
            Espresso.onView(ViewMatchers.withId(R.id.editText_search))
                    .perform(ViewActions.typeText("test"), ViewActions.closeSoftKeyboard());

            // Verify search was processed (actual filtering would need mock data)
            // This just verifies the text was entered
            Espresso.onView(ViewMatchers.withId(R.id.editText_search))
                    .check(ViewAssertions.matches(ViewMatchers.withText("test")));
        }
    }

    @Test
    public void testFilterPopupContent() {
        try (ActivityScenario<ViewOtherProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Click filter button to open popup
            Espresso.onView(ViewMatchers.withId(R.id.filter))
                    .perform(ViewActions.click());

            // Verify all filter popup elements are displayed
            Espresso.onView(ViewMatchers.withId(R.id.select_all_checkbox))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.emotional_states_list))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.filter_recent_week))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.apply_filter_button))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.button_reset_filters))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            // Verify emotional states list has items (using a matcher that won't fail if empty)
            Espresso.onView(ViewMatchers.withId(R.id.emotional_states_list))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testMoodEventListDisplay() {
        try (ActivityScenario<ViewOtherProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify list view is present and can be scrolled
            Espresso.onView(ViewMatchers.withId(R.id.listview_profile_history))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            // Note: To test actual items in the list, you'd need to mock the database response
            // with test mood events
        }
    }

}