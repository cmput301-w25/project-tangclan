package com.example.tangclan;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FeedFilterTest {

    @Test
    public void testFilterPopupElements() {
        ActivityScenario.launch(FeedActivity.class);

        // Open filter popup
        Espresso.onView(ViewMatchers.withId(R.id.filter))
                .perform(ViewActions.click());

        // Check all filter elements are present
        Espresso.onView(ViewMatchers.withId(R.id.select_all_checkbox))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.filter_recent_week))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.apply_filter_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testApplyEmptyFilter() {
        ActivityScenario.launch(FeedActivity.class);

        // Open filter popup
        Espresso.onView(ViewMatchers.withId(R.id.filter))
                .perform(ViewActions.click());

        // Click apply button without selecting anything
        Espresso.onView(ViewMatchers.withId(R.id.apply_filter_button))
                .perform(ViewActions.click());

        // Verify feed is still visible
        Espresso.onView(ViewMatchers.withId(R.id.feed_container))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testResetFilters() {
        ActivityScenario.launch(FeedActivity.class);

        // Open filter popup
        Espresso.onView(ViewMatchers.withId(R.id.filter))
                .perform(ViewActions.click());

        // Click reset button
        Espresso.onView(ViewMatchers.withId(R.id.button_reset_filters))
                .perform(ViewActions.click());

        // Verify feed is still visible
        Espresso.onView(ViewMatchers.withId(R.id.feed_container))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}