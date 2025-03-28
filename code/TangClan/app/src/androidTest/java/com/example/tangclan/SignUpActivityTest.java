package com.example.tangclan;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import android.util.Log;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.tangclan.ui.login.SignUpActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
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
public class SignUpActivityTest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> scenario = new
            ActivityScenarioRule<SignUpActivity>(SignUpActivity.class);

    @Test
    public void emptyFieldsShouldShowErrorWhenSigningUp() {
        // check that form doesn't allow empty email
        onView(withId(R.id.full_name)).perform(ViewActions.typeText("John Doe"));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.email_addre)).check(matches(hasErrorText("Enter email")));

        // check that form doesn't allow empty username
        onView(withId(R.id.email_addre)).perform(ViewActions.typeText("JohnDoe@example.com"));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.username)).check(matches(hasErrorText("Enter username")));

        // check that form doesn't allow empty password
        onView(withId(R.id.username)).perform(ViewActions.typeText("John123"));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.password)).check(matches(hasErrorText("Enter password")));

        onView(withId(R.id.password)).perform(ViewActions.typeText("Password123#"));
        onView(withId(R.id.confirm_pas)).perform(ViewActions.typeText("Password123#"));
        onView(withId(R.id.button_signup)).perform(click());

        onView(withId(R.id.button_signup)).perform(click()); // submit valid data
    }

    @Test
    public void invalidFieldsShouldShowErrorWhenSigningUp() {
        // add to database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document().set(new Profile("JohnBow", "John1", "password1#", "John1@gmail.com", "20"));

        // enter new user data
        onView(withId(R.id.full_name)).perform(ViewActions.typeText("JohnDoe"));
        onView(withId(R.id.email_addre)).perform(ViewActions.typeText("John1"));
        onView(withId(R.id.username)).perform(ViewActions.typeText("John1"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("pass"));
        onView(withId(R.id.confirm_pas)).perform(ViewActions.typeText("pass"));

        onView(withId(R.id.button_signup)).perform(click());

        // Check email format is enforced
        onView(withId(R.id.email_addre)).check(matches(hasErrorText("Wrong format")));
        onView(withId(R.id.email_addre)).perform(ViewActions.clearText());
        onView(withId(R.id.email_addre)).perform(ViewActions.typeText("JohnDoe@gmail.com")); // add domain

        // Check password format is enforced
        String errText = "Password must be at least 8 characters long and must contain one of each:\n" +
                " - Capital letter\n" +
                " - Lowercase letter\n" +
                " - One of the special characters: !,#,$,%,^,&,*,|\n";
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.password)).check(matches(hasErrorText(errText))); // password = "pass"

        // add uppercase and check password
        onView(withId(R.id.password)).perform(ViewActions.clearText());
        onView(withId(R.id.password)).perform(ViewActions.typeText("passW"));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.password)).check(matches(hasErrorText(errText)));

        // add number and check password
        onView(withId(R.id.password)).perform(ViewActions.clearText());
        onView(withId(R.id.password)).perform(ViewActions.typeText("passW1"));  // add number
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.password)).check(matches(hasErrorText(errText)));

        // add special character and check password
        onView(withId(R.id.password)).perform(ViewActions.clearText());
        onView(withId(R.id.password)).perform(ViewActions.typeText("passW1#"));  // add special character
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.password)).check(matches(hasErrorText(errText)));

        // add last character to meet min length and check password
        onView(withId(R.id.password)).perform(ViewActions.clearText());
        onView(withId(R.id.password)).perform(ViewActions.typeText("passW1##"));  // add another character to meet min length
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.confirm_pas)).check(matches(hasErrorText("Entered password does not match")));

        // update passwords to match
        onView(withId(R.id.confirm_pas)).perform(ViewActions.clearText());
        onView(withId(R.id.confirm_pas)).perform(ViewActions.typeText("passW1##"));
        onView(withId(R.id.button_signup)).perform(click());

        // check unique username is enforced
        onView(withId(R.id.username)).check(matches(hasErrorText("Username already exists")));

        // check unique email is enforced
        onView(withId(R.id.username)).perform(ViewActions.clearText());
        onView(withId(R.id.username)).perform(ViewActions.typeText("John2"));

        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.email_addre)).check(matches(hasErrorText("An account with this email already exists")));

        // enter email that is not taken
        onView(withId(R.id.email_addre)).perform(ViewActions.clearText());
        onView(withId(R.id.email_addre)).perform(ViewActions.typeText("JohnDoe@gmail.com"));
        onView(withId(R.id.button_signup)).perform(click());
    }

    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
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

