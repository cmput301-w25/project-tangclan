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

/**
 * Adapter for displaying mood events in a ListView.
 * This class retrieves mood events and associates them with usernames.
 * It ensures that mood events are displayed with formatted text and optional images.
 */
public class MoodEventAdapter extends ArrayAdapter<MoodEvent> {

    private Map<MoodEvent, String> moodToUsernameMap; // Maps MoodEvent to corresponding username

    /**
     * Constructor for the MoodEventAdapter with a pre-populated list of mood events.
     *
     * @param context The activity context
     * @param moodEvents List of mood events to display
     */
    public MoodEventAdapter(Context context, List<MoodEvent> moodEvents) {
        super(context, 0, moodEvents);
        this.moodToUsernameMap = new HashMap<>();

        // Associate mock usernames with each mood event for display purposes
        // In a real implementation, these would come from the database
        int userCounter = 1;
        for (MoodEvent event : moodEvents) {
            moodToUsernameMap.put(event, "User" + userCounter++);
        }
    }

    /**
     * Constructor for the MoodEventAdapter that takes a FollowingBook.
     *
     * @param context The activity context
     * @param followingBook The FollowingBook containing users and their mood events
     */
    public MoodEventAdapter(Context context, FollowingBook followingBook) {
        super(context, 0, new ArrayList<>());
        this.moodToUsernameMap = new HashMap<>();

        DatabaseBestie bestie = new DatabaseBestie();

        // Placeholder implementation - in real app would get actual data from database
        // Add dummy data for testing
        Map<String, MoodEvent> events = followingBook.getRecentMoodEvents(bestie);
        for (Map.Entry<String, MoodEvent> entry : events.entrySet()) {
            add(entry.getValue());
            moodToUsernameMap.put(entry.getValue(), "User_" + entry.getKey());
        }
    }

    /**
     * View recycler for the ListView
     * @param position
     *      position of the item
     * @param convertView
     *      view to be recycled
     * @param parent
     *      parent view
     * @return
     *      a single recycled view to be added to the ListView
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
        if (moodEvent == null) return view;

        // Retrieve username for this mood event
        String username = moodToUsernameMap.getOrDefault(moodEvent, "Unknown");

        // Format the username and mood emotion
        SpannableStringBuilder spannableUsernameEmotion = new SpannableStringBuilder();

        // Formatting username as bold
        SpannableString spannableUsername = new SpannableString(username);
        spannableUsername.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Formatting emotional state with color
        SpannableString spannableEmotionalState = new SpannableString(moodEvent.getMood().getEmotion());
        spannableEmotionalState.setSpan(new ForegroundColorSpan(moodEvent.getMood().getColor(getContext().getApplicationContext())), 0, spannableEmotionalState.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableUsernameEmotion.append(spannableUsername).append(" is feeling ").append(spannableEmotionalState);

        // Set username and emotion on the TextView
        TextView usernameEmotion = view.findViewById(R.id.username_emotional_state);
        usernameEmotion.setText(spannableUsernameEmotion);

        // Set social situation (if present) or a default message
        TextView situation = view.findViewById(R.id.situation);
        situation.setText(moodEvent.getSituation().isPresent() ? moodEvent.getSituation().get() : "No situation");

        // Set the reason (if available)
        TextView reason = view.findViewById(R.id.reason);
        reason.setText(moodEvent.getReason() != null ? moodEvent.getReason() : "No reason specified");

        // Set the post date and time
        TextView date = view.findViewById(R.id.date_text);
        TextView time = view.findViewById(R.id.time_text);
        date.setText(moodEvent.getPostDate().toString());
        time.setText(moodEvent.getPostTime().toString());

        // Set the optional photo (if available)
        ImageView imageView = view.findViewById(R.id.mood_event_image);
        if (moodEvent.getImage() != null) {
            imageView.setImageBitmap(moodEvent.getImage());
            imageView.setVisibility(View.VISIBLE);  // Make sure the ImageView is visible
        } else {
            imageView.setVisibility(View.GONE);  // Hide the ImageView if no image is available
        }

        return view;
    }

    /**
     * Updates the adapter with a new list of mood events
     *
     * @param moodEvents New list of mood events to display
     */
    public void updateMoodEvents(List<MoodEvent> moodEvents) {
        clear();
        addAll(moodEvents);

        // Reset username mapping
        moodToUsernameMap.clear();
        int userCounter = 1;
        for (MoodEvent event : moodEvents) {
            moodToUsernameMap.put(event, "User" + userCounter++);
        }

        notifyDataSetChanged();
    }
}