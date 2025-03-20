package com.example.tangclan;

import static android.view.View.FIND_VIEWS_WITH_TEXT;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;

public class EditFragment extends Fragment {
    String mid, month, emotion, situation, reason;
    byte[] image;
    boolean locationPermission;
    private FragmentListener editFragmentListener;


    public interface FragmentListener {  // listens to when fragment finishes
        void onFragmentFinished();
    }

    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance(Bundle moodDetails) {
        EditFragment fragment = new EditFragment();
        fragment.setArguments(moodDetails);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            editFragmentListener = (FragmentListener) context; // Attach listener
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mid = getArguments().getString("mid");
            month = getArguments().getString("month");
            emotion = getArguments().getString("emotion");
            situation = getArguments().getString("social situation");
            reason = getArguments().getString("reason");
            image = getArguments().getByteArray("image");
            locationPermission = getArguments().getBoolean("location permission");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_details_fragment, container, false);

        // get radio buttons
        RadioButton[] radioButtons = {
                view.findViewById(R.id.radioHappy),
                view.findViewById(R.id.radioSad),
                view.findViewById(R.id.radioAngry),
                view.findViewById(R.id.radioAnxious),
                view.findViewById(R.id.radioAshamed),
                view.findViewById(R.id.radioCalm),
                view.findViewById(R.id.radioConfused),
                view.findViewById(R.id.radioDisgust),
                view.findViewById(R.id.radioNoIdea),
                view.findViewById(R.id.radioSurprised),
                view.findViewById(R.id.radioTerrified)
        };
        // set saved emotion
        setCheckedEmotion(view);
        getChangestoCheckedEmotion(view, radioButtons);  // listen for new selections

        // set saved social situation
        EditText editSocialSit = view.findViewById(R.id.edit_social_situation);
        editSocialSit.setText(situation);

        // set saved reason text
        EditText editReason = view.findViewById(R.id.edit_reasonwhy);
        editReason.setText(reason);

        // TODO: on text change listener, update character count

        if (image != null) {
            ImageButton postImage = view.findViewById(R.id.image_reasonwhy);
            // TODO: implement updating image
        }

        // set saved location permission
        SwitchCompat useLocation = view.findViewById(R.id.use_location_switch);
        useLocation.setChecked(locationPermission);


        Button submitButt = view.findViewById(R.id.submit_details);
        submitButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newReason = editReason.getText().toString();

                if (newReason.length() > 200) {
                    editReason.setError("Text is over 200 characters!");
                    return;
                }

                String newEmotion = getCheckedEmotionText(view, radioButtons);
                ArrayList<String> newCollaborators = getEditTextCollaborators(editSocialSit);

                saveEditsToDatabase(newEmotion, newReason, newCollaborators, "");

                finishFragment();
            }
        });

        // Implement Cancel Button
        ImageButton cancel_butt = view.findViewById(R.id.cancel_edit);
        cancel_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishFragment();
            }
        });

        return view;
    }

    public void setCheckedEmotion(View view) {
        RadioGroup emotionOptions = view.findViewById(R.id.emotion_selection);
        ArrayList<View> emotionalState = new ArrayList<>();  // list of length 1 that stores the view to look for
        emotionOptions.findViewsWithText(emotionalState,emotion, FIND_VIEWS_WITH_TEXT);
        RadioButton selectedEmotion = (RadioButton) emotionalState.get(0);
        emotionOptions.check(selectedEmotion.getId());  // set checked
    }

    public void getChangestoCheckedEmotion(View view, RadioButton[] buttons) {
        for (RadioButton button : buttons) {
            button.setOnClickListener(but -> {
                for (RadioButton btn : buttons) {
                    btn.setChecked(btn == button);
                }
            });
        }
    }

    public String getCheckedEmotionText(View view, RadioButton[] buttons) {
        String newEmotion = "";
        for (RadioButton button : buttons) {
            if (button.isChecked()) {
                newEmotion = button.getText().toString();  // get checked emotion string
            }
        }
        return newEmotion;
    }

    public ArrayList<String> getEditTextCollaborators(EditText inputLine) {
        String[] situationArray = inputLine.getText().toString().split(",");
        return new ArrayList<>(Arrays.asList(situationArray));
    }


    public void saveEditsToDatabase(String emotion, String reason, ArrayList<String> socialSit, String image) {
        DatabaseBestie db = new DatabaseBestie();
        db.updateMoodEventEmotionalState(mid, month, emotion.toLowerCase());
        db.updateMoodEventReason(mid,month, reason);
        db.updateMoodEventCollaborators(mid, month, socialSit);
    }

    private void finishFragment() {
        if (editFragmentListener != null) {
            editFragmentListener.onFragmentFinished(); // Notify Activity
        }
        getParentFragmentManager().beginTransaction().remove(this).commit(); // Remove Fragment
    }
}