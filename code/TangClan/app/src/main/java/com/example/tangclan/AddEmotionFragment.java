package com.example.tangclan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment wrapper which allows for adding an emotional state to the Mood Event being created
 * RELATED USER STORIES:
 *      US 01.01.01
 */
public class AddEmotionFragment extends Fragment {

    private String selectedEmotion = null;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add_emotion, container, false);

        // Top bar close icon
        ImageView closeIcon = view.findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(v -> requireActivity().finish());

        // Emoticon buttons
        ImageButton btnHappy = view.findViewById(R.id.emotionHappy);
        ImageButton btnCalm = view.findViewById(R.id.emotionCalm);
        ImageButton btnSurprised = view.findViewById(R.id.emotionSurprised);
        ImageButton btnDisgusted = view.findViewById(R.id.emotionDisgusted);
        ImageButton btnAngry = view.findViewById(R.id.emotionAngry);
        ImageButton btnConfused = view.findViewById(R.id.emotionConfused);
        TextView emotText = view.findViewById(R.id.emotionText);

        emotText.setVisibility(View.GONE);

        // Click listeners to select an emotion
        btnHappy.setOnClickListener(v ->  {
            selectedEmotion = "happy";
            emotText.setText("You are feeling happy!");
            emotText.setVisibility(View.VISIBLE);
        });
        btnCalm.setOnClickListener(v -> {
            selectedEmotion = "calm";
            emotText.setText("You are feeling calm!");
            emotText.setVisibility(View.VISIBLE);
        });
        btnSurprised.setOnClickListener(v -> {
            selectedEmotion = "surprised";
            emotText.setText("You are feeling surprised?!");
            emotText.setVisibility(View.VISIBLE);
        });
        btnDisgusted.setOnClickListener(v -> {
            selectedEmotion = "disgusted";
            emotText.setText("You are feeling disgusted.");
            emotText.setVisibility(View.VISIBLE);
        });
        btnAngry.setOnClickListener(v -> {
            selectedEmotion = "angry";
            emotText.setText("You are feeling angry!");
            emotText.setVisibility(View.VISIBLE);
        });
        btnConfused.setOnClickListener(v -> {
            selectedEmotion = "calm";
            emotText.setText("You are feeling... confused?");
            emotText.setVisibility(View.VISIBLE);
        });

        // Bottom buttons
        Button btnCancel = view.findViewById(R.id.btnCancelEmotion);
        btnCancel.setOnClickListener(v -> requireActivity().finish());

        Button btnNext = view.findViewById(R.id.btnNextEmotion);
        btnNext.setOnClickListener(v -> {
            if (selectedEmotion == null) {
                Toast.makeText(getContext(), "Please select an emotion", Toast.LENGTH_SHORT).show();
                return;
            }
            // Save selected emotion in ViewModel
            WizActivity activity = (WizActivity) requireActivity();
            emotText.setText("You are feeling " + selectedEmotion + "!");
            emotText.setVisibility(View.VISIBLE);
            activity.getWizardViewModel().setEmotionalState(selectedEmotion);

            // Move to triggers page
            activity.navigateToNextFragment(1);
        });

        return view;
    }
}
