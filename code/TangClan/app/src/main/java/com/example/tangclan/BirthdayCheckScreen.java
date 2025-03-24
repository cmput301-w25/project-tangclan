package com.example.tangclan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
//

/**
 * BirthdayCheckScreen is an activity screen that displays the user's birthday setup page.
 * It is part of the profile setup process for the application.
 */

//TODO implement functionality of this screen in this class
public class BirthdayCheckScreen extends AppCompatActivity {

    /**
     * Called when the activity is first created. Initializes the layout and sets up the
     * birthday profile setup screen.
     *
     * @param savedInstanceState .
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup_birthday);

    }
}
