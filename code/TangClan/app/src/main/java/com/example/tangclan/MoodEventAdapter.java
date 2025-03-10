package com.example.tangclan;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoodEventAdapter extends ArrayAdapter<MoodEvent> {
    //moodeventarray adapterrr

    private Map<MoodEvent, String> moodToUsernameMap; // Maps MoodEvent to corresponding username

    /**
     * Constructor for the MoodEventAdapter
     * @param context The activity context
     * @param followingBook The FollowingBook containing users and their mood events
     */
    public MoodEventAdapter(Context context, FollowingBook followingBook) {
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

        addAll(moodEvents);
    }

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
        if (moodEvent == null) return view;

        // Retrieve username for this mood event
        String username = moodToUsernameMap.getOrDefault(moodEvent, "Unknown");

        // Format the username and mood emotionnn
        SpannableStringBuilder spannableUsernameEmotion = new SpannableStringBuilder();

        SpannableString spannableUsername = new SpannableString(username);
        spannableUsername.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableEmotionalState = new SpannableString(moodEvent.getMood().getEmotion());
        spannableEmotionalState.setSpan(new ForegroundColorSpan(moodEvent.getMood().getColor(getContext().getApplicationContext())), 0, spannableEmotionalState.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableUsernameEmotion.append(spannableUsername).append(" is feeling ").append(spannableEmotionalState);

        TextView usernameEmotion = view.findViewById(R.id.username_emotional_state);
        TextView situation = view.findViewById(R.id.situation);
        TextView date = view.findViewById(R.id.date_text);
        TextView time = view.findViewById(R.id.time_text);
        ImageView imageView = view.findViewById(R.id.mood_event_image);  // Get the ImageView

        usernameEmotion.setText(spannableUsernameEmotion);
        situation.setText(moodEvent.getSituation() != null ? moodEvent.getSituation() : "No situation");
        date.setText(moodEvent.getPostDate().toString());
        time.setText(moodEvent.getPostTime().toString());


        if (moodEvent.getImage() != null) {
            imageView.setImageBitmap(moodEvent.getImage());  // Set the image
            imageView.setVisibility(View.VISIBLE);  // Ensure the ImageView is visible
        } else {
            imageView.setVisibility(View.GONE);  // Hide the ImageView if no image
        }

        return view;
    }
}
