package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.util.Hex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class AddSocialSituationActivity extends AppCompatActivity {

    String[] settings = {"", "alone", "with one other person", "with two to several people", "with a crowd"};
    //private WizVIew wizVIew;  // ViewModel instance
    private String socialSetting;
    private ArrayList<String> collaborators;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_social_situation);

        // get the session user (for followingbook)
        //TODO: make sure that followingbook is up to date for everything
        LoggedInUser user = LoggedInUser.getInstance();
        ArrayList<String> followerBook = user.getFollowingBook().getFollowers();

        // Initialize ViewModel
        //
        // wizVIew = new ViewModelProvider(this).get(WizVIew.class);

        // Initialize UI elements
        AutoCompleteTextView editTextSituation = findViewById(R.id.editTextSituation);
        AutoCompleteTextView autoCompleteSituation = findViewById(R.id.editTextSetting);
        TextView taggedSoFar = findViewById(R.id.taggedSoFar);
        ImageView closeIcon = findViewById(R.id.closeIcon);
        Button btnBack = findViewById(R.id.btnBackEnvironment);
        Button btnSave = findViewById(R.id.btnSaveEnvironment);

        View.OnClickListener onTaggedSoFarClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCollaborators(collaborators);
            }
        };

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
                taggedSoFar.setText(String.format(Locale.CANADA, "%d tagged", collaborators.size()));
                taggedSoFar.setClickable(true);

                taggedSoFar.setTextColor(Color.parseColor("#366184"));
                taggedSoFar.setPaintFlags(8);


                taggedSoFar.setOnClickListener(onTaggedSoFarClicked);

            } else {
                taggedSoFar.setText("No one tagged");
                taggedSoFar.setClickable(false);
            }
        }

        // setup the autocomplete for tags, based on followingBook
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, followerBook);
        editTextSituation.setAdapter(adapter);

        // on username clicked mechanism to tag
        editTextSituation.setOnItemClickListener((adapterView, view, pos, l) -> {
            String collaborator = adapter.getItem(pos);

            // create an empty array list if null
            if (collaborators == null) {
                collaborators = new ArrayList<>();
            }

            // all names in collaborators must be unique
            if (collaborators.contains(collaborator)) {
                Toast.makeText(getBaseContext(),  collaborator + " is already tagged!", Toast.LENGTH_SHORT).show();
            } else {
                editTextSituation.setText("");
                collaborators.add(collaborator);
                taggedSoFar.setText(String.format(Locale.CANADA, "%d tagged", collaborators.size()));
                taggedSoFar.setClickable(true);
                taggedSoFar.setTextColor(Color.parseColor("#366184"));
                taggedSoFar.setPaintFlags(8);
                taggedSoFar.setOnClickListener(onTaggedSoFarClicked);
                Toast.makeText(getBaseContext(), collaborator + " successfully added to tag list!", Toast.LENGTH_SHORT).show();
            }
        });

        // Create Dropdown
        String[] selectedSetting = new String[1];
        selectedSetting[0] = socialSetting;
        ArrayAdapter<String> sitOptions = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, settings);
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
            savedDetails.putStringArrayList("collaborators", collaborators);
            Intent intent = new Intent(AddSocialSituationActivity.this, AddEmotionActivity.class);
            intent.putExtras(savedDetails);
            startActivity(intent);
            finish();
        });

        // Save and navigate forward
        btnSave.setOnClickListener(v -> {
            // save social setting and collaborators to the bundle
            savedDetails.putString("setting", selectedSetting[0]);  // not null since first fragment handles this
            savedDetails.putStringArrayList("collaborators", collaborators);

            // Save the social situation in the ViewModel
            // wizVIew.setSocialSituation(collaborators);

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

    public void showCollaborators(ArrayList<String> collaborators) {
        Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_tagged,null);
        builder.setView(dialogView);

        ListView tags = dialogView.findViewById(R.id.listview_tagged);

        CollaboratorAdapter adapter = new CollaboratorAdapter(context, collaborators);
        tags.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow()
                .setBackgroundDrawable(ResourcesCompat
                        .getDrawable(context.getResources(), R.drawable.dialog_round, null));
        dialog.show();
    }
}
