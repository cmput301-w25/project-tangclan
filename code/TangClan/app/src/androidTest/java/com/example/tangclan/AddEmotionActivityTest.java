package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddEmotionActivityTest {

    @Test
    public void testAllEmotionButtonsDisplayed() {
        ActivityScenario.launch(AddEmotionActivity.class);

        // Verify all emotion buttons are displayed
        Espresso.onView(ViewMatchers.withId(R.id.emotionHappy))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionCalm))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionSurprised))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionDisgusted))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionAngry))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionConfused))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionTerrfied))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionNoIdea))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionAshamed))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionSad))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionAnxious))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testEmotionSelection() {
        ActivityScenario.launch(AddEmotionActivity.class);

        // Select happy emotion
        Espresso.onView(ViewMatchers.withId(R.id.emotionHappy))
                .perform(ViewActions.click());

        // Verify the button is selected (you might need a custom matcher to check the background)
        Espresso.onView(ViewMatchers.withId(R.id.emotionHappy))
                .check(ViewAssertions.matches(CustomMatchers.withSelectedBackground()));

        // Select another emotion (sad) and verify happy is no longer selected
        Espresso.onView(ViewMatchers.withId(R.id.emotionSad))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.emotionHappy))
                .check(ViewAssertions.matches(CustomMatchers.withUnselectedBackground()));
        Espresso.onView(ViewMatchers.withId(R.id.emotionSad))
                .check(ViewAssertions.matches(CustomMatchers.withSelectedBackground()));
    }

    @Test
    public void testNextButtonWithoutSelectionShowsToast() {
        ActivityScenario.launch(AddEmotionActivity.class);

        // Click next without selecting an emotion
        Espresso.onView(ViewMatchers.withId(R.id.btnNextEmotion))
                .perform(ViewActions.click());

        // Verify toast message is shown
        Espresso.onView(ViewMatchers.withText("Please select an emotion"))
                .inRoot(new ToastMatcher())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNextButtonWithSelectionNavigatesToNextActivity() {
        ActivityScenario.launch(AddEmotionActivity.class);

        // Select an emotion
        Espresso.onView(ViewMatchers.withId(R.id.emotionHappy))
                .perform(ViewActions.click());

        // Click next
        Espresso.onView(ViewMatchers.withId(R.id.btnNextEmotion))
                .perform(ViewActions.click());

        // Verify the next activity is launched
        // You might need to add an id from AddSocialSituationActivity to verify
        Espresso.onView(ViewMatchers.withId(R.id.SocialSituation))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testCancelButtonNavigatesToFeedActivity() {
        ActivityScenario.launch(AddEmotionActivity.class);

        // Click cancel
        Espresso.onView(ViewMatchers.withId(R.id.btnCancelEmotion))
                .perform(ViewActions.click());

        // Verify FeedActivity is launched
        // You might need to add an id from FeedActivity to verify
        Espresso.onView(ViewMatchers.withId(R.id.feed))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testCloseIconNavigatesToFeedActivity() {
        ActivityScenario.launch(AddEmotionActivity.class);

        // Click close icon
        Espresso.onView(ViewMatchers.withId(R.id.closeIcon))
                .perform(ViewActions.click());

        // Verify FeedActivity is launched
        Espresso.onView(ViewMatchers.withId(R.id.feed))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSavedEmotionIsPreselected() {
        // Create intent with saved emotion
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddEmotionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("emotion", "happy");
        intent.putExtras(bundle);

        ActivityScenario.launch(intent);

        // Verify happy emotion is preselected
        Espresso.onView(ViewMatchers.withId(R.id.emotionHappy))
                .check(ViewAssertions.matches(CustomMatchers.withSelectedBackground()));
    }
}