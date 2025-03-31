package com.example.tangclan;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tangclan.FeedActivity;
import com.example.tangclan.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FeedActivityTest {

    @Rule
    public ActivityScenarioRule<FeedActivity> activityRule =
            new ActivityScenarioRule<>(FeedActivity.class);

    @Test
    public void testFeedRecyclerViewIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testRefreshButtonIsDisplayedAndClickable() {
        Espresso.onView(ViewMatchers.withId(R.id.refreshButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void testClickRefreshButton() {
        Espresso.onView(ViewMatchers.withId(R.id.refreshButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testScrollRecyclerView() {
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(ViewActions.swipeUp())
                .perform(ViewActions.swipeDown());
    }
}
