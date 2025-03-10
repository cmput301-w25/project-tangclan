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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ArrayAdapter wrapper for the Profile History.
 * RELATED USER STORIES:
 *      US 01.04.01
 *              1.04.01a Create a mechanism for showing the given mood-event on the user profile
 *              upon creation of the mood event
 */
public class ProfileHistoryAdapter extends ArrayAdapter<MoodEvent> {

    // for security purposes we will only grab the username for the ArrayAdapter instead of the
    // whole profile.
    String username;

    /**
     * Constructor for the ProfileHistoryAdapter
     * @param context
     *      The activity context
     * @param profile
     *      The current user's profile object
     */


    public ProfileHistoryAdapter(Context context, FollowingBook followingBook) {
        super(context, 0, new ArrayList<>());

        moodToUsernameMap = new HashMap<>();
        List<MoodEvent> moodEvents = new ArrayList<>();
        DatabaseBestie bestie = new DatabaseBestie();

        // Populate mood events and map usernames
        for (String uid: followingBook.getFollowing()) {
            bestie.getUser(uid, profile -> {
                for (MoodEvent moodEvent : profile.getMoodEventBook().getMoodEventList()) {
                    moodEvents.add(moodEvent);
                    moodToUsernameMap.put(moodEvent, profile.getUsername());
                }
            });
        }

    
        // Populate mood events and map usernames




    /**
     *  Creates and recycles the view for the ListView item
     * @param position
     *      position of the item
     * @param convertView
     *      view to be used as a recycling/regenerating view
     * @param parent
     *      parent ViewGroup
     * @return
     *      A ListView item view
     */
    @Override
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_mood_event, parent, false);
        } else {
            view = convertView;
        }
        MoodEvent moodEvent = getItem(position);

        // Format the username and mood emotion
        SpannableStringBuilder spannableUsernameEmotion = new SpannableStringBuilder();

        SpannableString spannableUsername = new SpannableString(username);
        spannableUsername.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableEmotionalState = new SpannableString(moodEvent.getMood().getEmotion());

        spannableEmotionalState.setSpan(new ForegroundColorSpan(moodEvent.getMood().getColor(getContext())), 0, spannableEmotionalState.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableUsernameEmotion.append(spannableUsername).append(" is feeling ").append(spannableEmotionalState);

        TextView usernameEmotion = view.findViewById(R.id.username_emotional_state);
        TextView situation = view.findViewById(R.id.situation);
        TextView date = view.findViewById(R.id.date_text);
        TextView time = view.findViewById(R.id.time_text);


        // logic for dynamically populating the ChipGroup
        ChipGroup triggers = view.findViewById(R.id.trigger_tags);

        usernameEmotion.setText(spannableUsernameEmotion);

        situation.setText(moodEvent.getSituation().isPresent() ? moodEvent.getSituation().get() : "No situation");
        date.setText(moodEvent.getPostDate().toString());
        time.setText(moodEvent.getPostTime().toString());


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
