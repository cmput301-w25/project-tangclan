package com.example.tangclan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
                        .replace(R.id.fragment_container, new AddEnvironmentFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                // Could handle final submission or more pages here
                break;
        }
    }

    // Optionally, a method to go back or to finish the wizard
    public void navigateBack() {
        onBackPressed();
    }
}
