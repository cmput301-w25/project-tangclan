package com.example.tangclan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;

public class AddSocialSituationActivity extends AppCompatActivity {

    String[] settings = {"", "alone", "with one other person", "with two to several people", "with a crowd"};
    private WizVIew wizVIew;  // ViewModel instance
    private String socialSetting;
    private ArrayList<String> collaborators;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_social_situation);

        // Initialize ViewModel
        wizVIew = new ViewModelProvider(this).get(WizVIew.class);

        // Initialize UI elements
        EditText editTextSituation = findViewById(R.id.editTextSituation);
        AutoCompleteTextView autoCompleteSituation = findViewById(R.id.editTextSetting);
        ImageView closeIcon = findViewById(R.id.closeIcon);
        Button btnBack = findViewById(R.id.btnBackEnvironment);
        Button btnSave = findViewById(R.id.btnSaveEnvironment);

        // Retrieve the selected emotion from the previous activity using the Bundle
        Bundle savedDetails = getIntent().getExtras();
        if (savedDetails != null) {
            socialSetting = savedDetails.getString("setting");
            collaborators = savedDetails.getStringArrayList("collaborators");
            // show saved input
            if (socialSetting != null) {
                autoCompleteSituation.setText(socialSetting);
                autoCompleteSituation.setHint(socialSetting);
            }

            if (collaborators != null) {
                StringBuilder s = new StringBuilder();
                if (!collaborators.get(0).isEmpty()) {
                    for (String collaborator : collaborators) {
                        s.append(collaborator.trim()).append(", ");
                    }
                }
                editTextSituation.setText(s);
            }
        }

        // Create Dropdown
        String[] selectedSetting = new String[1];
        selectedSetting[0] = socialSetting;
        ArrayAdapter<String> sitOptions = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, settings);
        autoCompleteSituation.setAdapter(sitOptions);
        autoCompleteSituation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSetting[0] = i == 0 ? null : adapterView.getItemAtPosition(i).toString();
                autoCompleteSituation.setHint(adapterView.getItemAtPosition(i).toString());
            }
        });

        // Close the activity when clicking the close icon
        closeIcon.setOnClickListener(v -> {
            startActivity(new Intent(AddSocialSituationActivity.this, FeedActivity.class));
            finish();
        });

        // Navigate back TODO: pass back selectedEmotion
        btnBack.setOnClickListener(v -> {
            savedDetails.putString("setting", selectedSetting[0]);  // not null since first fragment handles this
            savedDetails.putStringArrayList("collaborators", listCollaborators(editTextSituation));
            Intent intent = new Intent(AddSocialSituationActivity.this, AddEmotionActivity.class);
            intent.putExtras(savedDetails);
            startActivity(intent);
            finish();
        });

        // Save and navigate forward
        btnSave.setOnClickListener(v -> {
            // save social setting and collaborators to the bundle
            savedDetails.putString("setting", selectedSetting[0]);  // not null since first fragment handles this
            savedDetails.putStringArrayList("collaborators", listCollaborators(editTextSituation));

            // Save the social situation in the ViewModel
            wizVIew.setSocialSituation(listCollaborators(editTextSituation));

            // Create an intent to start the next activity
            Intent intent = new Intent(AddSocialSituationActivity.this, UploadPictureForMoodEventActivity.class);

            // Attach the bundle to the intent
            intent.putExtras(savedDetails);
            // Start the activity
            startActivity(intent);  // Start UploadPictureForMoodEventActivity
            finish();  // Close current activity
        });
    }

    public ArrayList<String> listCollaborators(EditText editTextSituation) {
        String[] socialSituationList = editTextSituation.getText().toString().trim().split(",");

        return new ArrayList<>(Arrays.asList(socialSituationList));

    }
}
