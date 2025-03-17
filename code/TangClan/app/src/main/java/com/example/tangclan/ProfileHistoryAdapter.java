package com.example.tangclan;

import android.content.Context;
import android.graphics.Color;
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

/**
 * ArrayAdapter wrapper for the Profile History.
 * RELATED USER STORIES:
 *      US 01.04.01
 *
 */
public class ProfileHistoryAdapter extends ArrayAdapter<MoodEvent> {

    private String username;

    private Context context;
    private Map<MoodEvent, String> moodToUsernameMap;



    /**
     * Constructor for the ProfileHistoryAdapter
     * @param context
     *      The activity context
     * @param profile
     *      The current user's profile object
     */
    public ProfileHistoryAdapter(Context context, Profile profile) {
        super(context, 0, new ArrayList<MoodEvent>());

        moodToUsernameMap = new HashMap<>();
        List<MoodEvent> moodEvents = new ArrayList<>();

        // Get the username
        this.username = profile.getUsername();

        // Populate mood events and map usernames
        for (MoodEvent moodEvent : profile.getMoodEventBook().getMoodEventList()) {
            moodEvents.add(moodEvent);
            moodToUsernameMap.put(moodEvent, profile.getUsername());
        }

        // Add the mood events to the adapter's data source
        addAll(moodEvents);
    }



    /**
     * Creates and recycles the view for the ListView item
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

        // Find views by ID
        TextView emotionTextView = view.findViewById(R.id.username_emotional_state);
        TextView situationTextView = view.findViewById(R.id.situation);
        TextView reasonTextView = view.findViewById(R.id.reason);
        TextView dateTextView = view.findViewById(R.id.date_text);
        TextView timeTextView = view.findViewById(R.id.time_text);
        ImageView moodImageView = view.findViewById(R.id.mood_event_image);

        // Set the emotional state
        emotionTextView.setText(spannableUsernameEmotion); // username and emotional state

        // Set the social situation (if present, otherwise default message)
        situationTextView.setText(moodEvent.getSituation().isPresent() ? moodEvent.getSituation().get() : "No situation specified");

        // Set the reason (if present, otherwise default message)
        reasonTextView.setText(moodEvent.getReason() != null && !moodEvent.getReason().isEmpty() ? moodEvent.getReason() : "No reason specified");

        // Set the post date and time
        dateTextView.setText(moodEvent.getPostDate().toString());
        timeTextView.setText(moodEvent.getPostTime().toString());

        // Set the image (if available)
        if (moodEvent.getImage() != null) {
            moodImageView.setImageBitmap(moodEvent.getImage());
            moodImageView.setVisibility(View.VISIBLE); // Show image
        } else {
            moodImageView.setVisibility(View.GONE); // Hide image if not available
        }

        return view;
    }

}
