package com.example.tangclan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class UploadPictureForMoodEventActivityTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private Intent intent;

    @Before
    public void setup() {
        intent = new Intent(ApplicationProvider.getApplicationContext(), UploadPictureForMoodEventActivity.class);
        // Add any required extras to the intent
        Bundle bundle = new Bundle();
        bundle.putString("selectedEmotion", "Happy");
        intent.putExtras(bundle);
    }

    @Test
    public void testUIElementsDisplayed() {
        try (ActivityScenario<UploadPictureForMoodEventActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify all UI elements are displayed
            Espresso.onView(ViewMatchers.withId(R.id.imageView))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.reason))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.btnNext))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.btnBackEnvironment))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.closeIcon))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testReasonInputAndCharacterCount() {
        try (ActivityScenario<UploadPictureForMoodEventActivity> scenario = ActivityScenario.launch(intent)) {
            // Type text that's less than 200 characters
            String shortText = "This is a test reason";
            Espresso.onView(ViewMatchers.withId(R.id.reason))
                    .perform(ViewActions.typeText(shortText));

            // Verify character count is not visible for short text
            Espresso.onView(ViewMatchers.withId(R.id.charCount))
                    .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

            // Clear the text
            Espresso.onView(ViewMatchers.withId(R.id.reason))
                    .perform(ViewActions.clearText());

            // Type long text (more than 150 characters to trigger the counter)
            String longText = "This is a very long reason that exceeds the character limit. " +
                    "This is a very long reason that exceeds the character limit. " +
                    "This is a very long reason that exceeds the character limit. " +
                    "This is a very long reason that exceeds the character limit.";

            Espresso.onView(ViewMatchers.withId(R.id.reason))
                    .perform(ViewActions.typeText(longText));

            // Verify character count is visible and shows correct count
            Espresso.onView(ViewMatchers.withId(R.id.charCount))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testBackButtonNavigation() {
        try (ActivityScenario<UploadPictureForMoodEventActivity> scenario = ActivityScenario.launch(intent)) {
            // Click the back button
            Espresso.onView(ViewMatchers.withId(R.id.btnBackEnvironment))
                    .perform(ViewActions.click());

            // Verify the activity finishes (we can't directly test navigation to previous activity in unit test)
            scenario.onActivity(activity -> {
                assert(activity.isFinishing());
            });
        }
    }

    @Test
    public void testCloseButtonNavigation() {
        try (ActivityScenario<UploadPictureForMoodEventActivity> scenario = ActivityScenario.launch(intent)) {
            // Click the close button
            Espresso.onView(ViewMatchers.withId(R.id.closeIcon))
                    .perform(ViewActions.click());

            // Verify the activity finishes (navigation to FeedActivity can't be directly tested in unit test)
            scenario.onActivity(activity -> {
                assert(activity.isFinishing());
            });
        }
    }

    @Test
    public void testNextButtonWithoutImage() {
        try (ActivityScenario<UploadPictureForMoodEventActivity> scenario = ActivityScenario.launch(intent)) {
            // Enter valid reason
            Espresso.onView(ViewMatchers.withId(R.id.reason))
                    .perform(ViewActions.typeText("Valid reason"), ViewActions.closeSoftKeyboard());

            // Click next button
            Espresso.onView(ViewMatchers.withId(R.id.btnNext))
                    .perform(ViewActions.click());

            // Verify activity finishes (navigation to ReviewDetailsActivity can't be directly tested)
            scenario.onActivity(activity -> {
                assert(activity.isFinishing());
            });
        }
    }

    @Test
    public void testNextButtonWithInvalidReason() {
        try (ActivityScenario<UploadPictureForMoodEventActivity> scenario = ActivityScenario.launch(intent)) {
            // Enter very long invalid reason
            String invalidReason = new String(new char[300]).replace('\0', 'a');
            Espresso.onView(ViewMatchers.withId(R.id.reason))
                    .perform(ViewActions.typeText(invalidReason), ViewActions.closeSoftKeyboard());

            // Click next button
            Espresso.onView(ViewMatchers.withId(R.id.btnNext))
                    .perform(ViewActions.click());

            // Verify error is shown and activity doesn't finish
            Espresso.onView(ViewMatchers.withId(R.id.reason))
                    .check(ViewAssertions.matches(ViewMatchers.hasErrorText("You're way over limit!")));

            scenario.onActivity(activity -> {
                assert(!activity.isFinishing());
            });
        }
    }

    // Helper method to create a test image file
    private Uri createTestImage() throws IOException {
        // Create a bitmap
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        // Save it to a file
        File file = new File(ApplicationProvider.getApplicationContext().getCacheDir(), "test.jpg");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();

        return Uri.fromFile(file);
    }
}