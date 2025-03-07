package com.example.tangclan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddTriggersFragment extends Fragment {

    private TextView triggersDisplay;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add_triggers, container, false);

        WizActivity activity = (WizActivity) requireActivity();
        triggersDisplay = view.findViewById(R.id.triggersDisplay);

        // Top bar close icon
        ImageView closeIcon = view.findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(v -> requireActivity().finish());

        EditText editTextTrigger = view.findViewById(R.id.editTextTrigger);
        Button btnAddTrigger = view.findViewById(R.id.btnAddTrigger);
        btnAddTrigger.setOnClickListener(v -> {
            String triggerText = editTextTrigger.getText().toString().trim();
            if (!TextUtils.isEmpty(triggerText)) {
                activity.getWizardViewModel().addTrigger(triggerText);
                editTextTrigger.setText("");
                updateTriggersDisplay();
            }
        });

        // Bottom row
        Button btnBack = view.findViewById(R.id.btnBackTrigger);
        btnBack.setOnClickListener(v -> activity.navigateBack());

        Button btnNext = view.findViewById(R.id.btnNextTrigger);
        btnNext.setOnClickListener(v -> {
            // Move to next fragment (Environment)
            activity.navigateToNextFragment(2);
        });

        // Update the display in case triggers already exist
        updateTriggersDisplay();

        return view;
    }

    private void updateTriggersDisplay() {
        WizActivity activity = (WizActivity) requireActivity();
        if (activity.getWizardViewModel().getTriggers().isEmpty()) {
            triggersDisplay.setText("No triggers added yet");
        } else {
            triggersDisplay.setText(activity.getWizardViewModel().getTriggers().toString());
        }
    }
}
