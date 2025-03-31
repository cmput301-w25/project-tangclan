package com.example.tangclan;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
//import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class AddSocialSituationActivityTest {

    private Bundle testBundle;

    @Before
    public void setUp() {
        // Create test bundle with required data
        testBundle = new Bundle();
        testBundle.putString("emotion", "happy");
        testBundle.putString("setting", null);
        testBundle.putStringArrayList("collaborators", null);

        // Mock LoggedInUser
        LoggedInUser user = LoggedInUser.getInstance();
        user.getFollowingBook().setFollowers(
                new ArrayList<>(Arrays.asList("user1", "user2", "user3")));
    }

    @Test
    public void testAllUiElementsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddSocialSituationActivity.class);
        intent.putExtras(testBundle);

        ActivityScenario.launch(intent);

        onView(withId(R.id.editTextSetting)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextSituation)).check(matches(isDisplayed()));
        onView(withId(R.id.taggedSoFar)).check(matches(isDisplayed()));
        onView(withId(R.id.closeIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.btnBackEnvironment)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSaveEnvironment)).check(matches(isDisplayed()));
    }

    @Test
    public void testTagCollaboratorFunctionality() {
        testBundle.putStringArrayList("collaborators", null);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddSocialSituationActivity.class);
        intent.putExtras(testBundle);

        ActivityScenario.launch(intent);
        onView(withId(R.id.editTextSituation))
                .check(matches(isDisplayed()));

        onView(withId(R.id.taggedSoFar))
                .check(matches(withText("No one tagged")));

        onView(withId(R.id.editTextSituation))
                .perform(typeText("user1"), closeSoftKeyboard());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("user1"))
                .inRoot(isPlatformPopup())
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.taggedSoFar))
                .check(matches(withText("1 tagged")));
    }

    @Test
    public void testDuplicateCollaborator() {
        // Pre-populate with one collaborator
        testBundle.putStringArrayList("collaborators",
                new ArrayList<>(Arrays.asList("user1")));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddSocialSituationActivity.class);
        intent.putExtras(testBundle);

        ActivityScenario.launch(intent);

        // Try to add the same collaborator again
        onView(withId(R.id.editTextSituation))
                .perform(typeText("user1"), closeSoftKeyboard());

        onView(withText("user1")).perform(click());

        // Verify count remains 1
        onView(withId(R.id.taggedSoFar))
                .check(matches(withText("1 tagged")));
    }

    @Test
    public void testSocialSettingSelection() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddSocialSituationActivity.class);
        intent.putExtras(testBundle);

        ActivityScenario.launch(intent);

        // Open dropdown
        onView(withId(R.id.editTextSetting)).perform(click());

        // Wait for dropdown to appear
        try {
            Thread.sleep(500); // Small delay for dropdown to render
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Select an option from dropdown
        onView(withText("alone"))
                .inRoot(isPlatformPopup()) // This is crucial for dropdown items
                .perform(click());

        // Verify selection by checking hint (since AutoCompleteTextView uses hint)
        onView(withId(R.id.editTextSetting))
                .check(matches(withHint(containsString("alone"))));
    }

    @Test
    public void testBackButtonNavigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddSocialSituationActivity.class);
        intent.putExtras(testBundle);

        ActivityScenario<AddSocialSituationActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.btnBackEnvironment)).perform(click());

        // Verify activity finishes
        scenario.onActivity(activity -> {
            assert(activity.isFinishing());
        });
    }

    @Test
    public void testSaveButtonNavigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddSocialSituationActivity.class);
        intent.putExtras(testBundle);

        ActivityScenario<AddSocialSituationActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.btnSaveEnvironment)).perform(click());

        // Verify activity finishes
        scenario.onActivity(activity -> {
            assert(activity.isFinishing());
        });
    }

    @Test
    public void testCloseIconNavigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddSocialSituationActivity.class);
        intent.putExtras(testBundle);

        ActivityScenario<AddSocialSituationActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.closeIcon)).perform(click());

        // Verify activity finishes
        scenario.onActivity(activity -> {
            assert(activity.isFinishing());
        });
    }
}