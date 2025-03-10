package com.example.tangclan;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class ProfileHistoryAdapter extends ArrayAdapter<MoodEvent> {

    // for security purposes we will only grab the username for the ArrayAdapter instead of the
    // whole profile.
    String username;

    /**
     * Constructor for the ProfileHistoryAdaptor object
     * @param context
     *      The context from 'this'
     * @param profile
     *      The profile of the current user to store their username
     */
    public ProfileHistoryAdapter(Context context, Profile profile) {
        super(context, 0, profile.moodEventBook.getMoodEventList());
        this.username = profile.getUsername();
    }

    /**
     *  Creates and recycles the view for the ListView item
     * @param position
     *      position of the item
     * @param convertView
     *      view to be used as a recycling/regenerating view
     * @param parent
     *      parent ViewGroup
     * @return
     */
    @Override
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_mood_event, parent,
                    false);
        } else {
            view = convertView;
        }
        MoodEvent moodEvent = getItem(position);

        // The string with username and emotionalstate has multiple styles;
        // SpannableStringBuilder allows us to recreate 'spans' as in html
        SpannableStringBuilder spannableUsernameEmotion = new SpannableStringBuilder("");

        SpannableString spannableUsername = new SpannableString(this.username);
        spannableUsername.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableUsername.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableEmotionalState = new SpannableString(moodEvent.getMood().getEmotion());
        spannableEmotionalState.setSpan(new ForegroundColorSpan(moodEvent.getMood().getColor()), 0,
                spannableEmotionalState.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableUsernameEmotion.append(spannableUsername + " is feeling " + spannableEmotionalState);

        TextView usernameEmotion = view.findViewById(R.id.username_emotional_state);
        TextView situation = view.findViewById(R.id.situation);
        TextView date = view.findViewById(R.id.date_text);
        TextView time = view.findViewById(R.id.time_text);


        // logic for dynamically populating the ChipGroup
        ChipGroup triggers = view.findViewById(R.id.trigger_tags);

        usernameEmotion.setText(spannableUsernameEmotion);

        // only populate the view if situation exists - otherwise, set invisible
        if (moodEvent.getSituation().isPresent()) {
            situation.setText(moodEvent.getSituation().get());
        } else {
            situation.setVisibility(View.INVISIBLE);
        }

        // only populate the view if situation exists - otherwise, set invisible
        if (moodEvent.getTriggers().isPresent()) {
            for (String trigger : moodEvent.getTriggers().get()) {
                Chip triggerChip = new Chip(getContext());
                ViewGroup.LayoutParams chipParams = new ViewGroup
                        .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                triggerChip.setLayoutParams(chipParams);
                triggerChip.setText(trigger);
                triggerChip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // use SP to set Text Size
                triggerChip.setTextColor(Color.BLACK);
                triggerChip.setChipBackgroundColorResource(
                        com.google.android.material.R.color.material_dynamic_neutral_variant70);

                Typeface chipFont = getContext().getResources().getFont(R.font.inter);
                triggerChip.setTypeface(chipFont);
                triggerChip.setChipStrokeWidth(0);

                triggers.addView(triggerChip);
            }
        } else {
            triggers.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
