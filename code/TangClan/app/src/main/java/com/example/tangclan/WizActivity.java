package com.example.tangclan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WizActivity extends AppCompatActivity {

    private WizVIew wizardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        // Obtain the shared ViewModel
        wizardViewModel = new ViewModelProvider(this).get(WizVIew.class);

        if (savedInstanceState == null) {
            // Load the first fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddEmotionFragment())
                    .commit();
        }
    }

    public WizVIew getWizardViewModel() {
        return wizardViewModel;
    }

    // Helper method to navigate between fragments
    public void navigateToNextFragment(int fragmentId) {
        switch (fragmentId) {
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddTriggersFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddSocialSituation())
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                // Could handle final submission or more pages here
                break;
        }
    }
    // Inside WizActivity

    private void finishWizard() {
        // Assuming the WizVIew ViewModel contains the data for the MoodEvent
        WizVIew vm = getWizardViewModel();

        // Create a MoodEvent from the ViewModel data
        MoodEvent moodEvent = new MoodEvent(vm.getEmotionalState(), vm.getTriggers(), vm.getSocialSituation());

        // Optionally, save it to a database or a local cache

        // Pass the MoodEvent to ProfileActivity
        Intent intent = new Intent(WizActivity.this, profileActivity.class);

        // Pass the MoodEvent as Serializable or Parcelable
        intent.putExtra("moodEvent", moodEvent);

        // Start ProfileActivity
        startActivity(intent);

        // Optionally finish this activity
        finish();
    }



    // write to database/local cache

    // Optionally, a method to go back or to finish the wizard
    public void navigateBack() {
        onBackPressed();
    }


}
