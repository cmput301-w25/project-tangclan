package com.example.tangclan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * Fragment wrapper which allows the user to add an optional social situation to the Mood Event
 * being created.
 *
 * RELATED USER STORIES:
 *      US 01.01.01
 */
public class AddSocialSituation extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add_social_situation, container, false);

        WizActivity activity = (WizActivity) requireActivity();

        // Top bar close icon
        ImageView closeIcon = view.findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(v -> requireActivity().finish());

        EditText editTextSituation = view.findViewById(R.id.editTextSituation);

        // Bottom row
        Button btnBack = view.findViewById(R.id.btnBackEnvironment);
        btnBack.setOnClickListener(v -> activity.navigateBack());

        Button btnSave = view.findViewById(R.id.btnSaveEnvironment);
        btnSave.setOnClickListener(v -> {
            String situation = editTextSituation.getText().toString().trim();
            if (!TextUtils.isEmpty(situation)) {
                // You can do more robust checks here if you like
                // e.g., length <= 20, word count <= 3, etc.
                activity.getWizardViewModel().setSocialSituation(situation);
            }

            // Now create the MoodEvent
            createMoodEvent();
        });

        return view;
    }

    private void createMoodEvent() {
        WizActivity activity = (WizActivity) requireActivity();
        WizVIew vm = activity.getWizardViewModel();

        // You might have a MoodEventBook in your Activity or a global store
        MoodEventBook moodEventBook = new MoodEventBook();

        try {
            // Build the MoodEvent based on which fields are set
            if (!vm.getTriggers().isEmpty() && vm.getSocialSituation() != null) {
                MoodEvent moodEvent = new MoodEvent(vm.getEmotionalState(), vm.getTriggers(), vm.getSocialSituation());
                moodEventBook.addMoodEvent(moodEvent);
            } else if (!vm.getTriggers().isEmpty()) {
                MoodEvent moodEvent = new MoodEvent(vm.getEmotionalState(), vm.getTriggers());
                moodEventBook.addMoodEvent(moodEvent);
            } else if (vm.getSocialSituation() != null) {
                MoodEvent moodEvent = new MoodEvent(vm.getEmotionalState(), vm.getSocialSituation());
                moodEventBook.addMoodEvent(moodEvent);
            } else {
                MoodEvent moodEvent = new MoodEvent(vm.getEmotionalState());
                moodEventBook.addMoodEvent(moodEvent);
            }

            // Here, you might navigate to a confirmation screen or just finish()
            requireActivity().finish();

        } catch (IllegalArgumentException e) {
            // Show an error message if the mood event was invalid
            // (e.g., invalid emotional state or invalid social situation)
        }
    }
}
