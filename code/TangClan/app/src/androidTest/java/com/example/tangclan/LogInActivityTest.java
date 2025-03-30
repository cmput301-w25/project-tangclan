package com.example.tangclan;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.tangclan.ui.login.LogInActivity;
import com.example.tangclan.ui.login.SignUpActivity;
import com.example.tangclan.ui.login.VerifyEmailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LogInActivityTest {

    @Rule
    public ActivityScenarioRule<LogInActivity> scenario = new
            ActivityScenarioRule<LogInActivity>(LogInActivity.class);

    @Test
    public void loggingInWithNoAccountShouldNotWork() {
        // check that form doesn't allow empty email
        onView(withId(R.id.username)).perform(ViewActions.typeText("sabTHEGOAT"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("Password123*"));
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.username)).check(matches(hasErrorText("Cannot find an account with that username")));
    }

    @Test
    public void loggingInWithWrongPasswordShouldNotWork() {
        // check that form doesn't allow empty email
        onView(withId(R.id.username)).perform(ViewActions.typeText("sabslabsyou"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("LETMEINPLEASE!1"));
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.password)).check(matches(hasErrorText("Wrong Password")));
    }

    @Test
    public void loggingInShouldNotShowErrors() {
        // check that form doesn't allow empty email
        onView(withId(R.id.username)).perform(ViewActions.typeText("sabslabsyou"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("Password1!"));
        onView(withId(R.id.login)).perform(click());
    }

    @Before
    public void seedDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        Profile profile = new Profile("Sabrina", "sabslabsyou", "Password1!", "scarpenter@gmail.com");
        profile.setUid("abc");

        FirebaseAuth.getInstance().createUserWithEmailAndPassword("scarpenter@gmail.com", "Password1!");
        usersRef.document("abc").set(profile);

    }

    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
        FirebaseAuth.getInstance().useEmulator(androidLocalhost, portNumber);
    }


    @After
    public void tearDown() {
        String projectId = "moodly-7003b";
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
