package com.example.tangclan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_mood_event_new, parent, false);
        } else {
            view = convertView;
        }

        MoodEvent moodEvent = getItem(position);
        if (moodEvent == null) return view;

        Button commentButton = view.findViewById(R.id.comment_button);
        commentButton.setOnClickListener(v -> {
            showCommentDialog(moodEvent);
        });



        // Retrieve username for this mood event
        String username = moodToUsernameMap.getOrDefault(moodEvent, "Unknown");

        // Format the username and mood emotion
        SpannableStringBuilder spannableUsernameEmotion = new SpannableStringBuilder();

        // Formatting username as bold
        SpannableString spannableUsername = new SpannableString(username);
        spannableUsername.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Formatting emotional state with color
        Mood mood = moodEvent.getMood();
        if (mood == null) {
            // Handle null Mood object
            Toast.makeText(getContext(), "Mood is null for event: " + moodEvent, Toast.LENGTH_SHORT).show();
            return view;
        }

        Integer color = mood.getColor(getContext().getApplicationContext());
        if (color == null) {
            // Handle null color
            Toast.makeText(getContext(), "Color is null for mood: " + mood, Toast.LENGTH_SHORT).show();
            return view;
        }


        SpannableString spannableEmotionalState = new SpannableString(mood.getEmotion());
        spannableEmotionalState.setSpan(new ForegroundColorSpan(color), 0, spannableEmotionalState.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableUsernameEmotion.append(spannableUsername).append(" is feeling ").append(spannableEmotionalState);

        // Set username and emotion on the TextView
        TextView usernameEmotion = view.findViewById(R.id.username_emotional_state);
        usernameEmotion.setText(spannableUsernameEmotion);

        // Set social situation (if present) or a default message
        TextView situation = view.findViewById(R.id.situation);

        // Set the reason (if available)
        TextView reason = view.findViewById(R.id.reason);

        // Handle Optional<String> for reason
        if (moodEvent.getReason().isPresent()) {
            reason.setText(moodEvent.getReason().get()); // Extract the value from Optional
        } else {
            reason.setText("No reason specified"); // Default message
        }

        // Set the post date and time
        TextView date = view.findViewById(R.id.date_text);
        TextView time = view.findViewById(R.id.time_text);
        date.setText(moodEvent.returnPostFormattedDate());
        time.setText(moodEvent.returnPostFormattedTime());

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

    private void showCommentDialog(MoodEvent moodEvent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_comments, null);
        builder.setView(dialogView);

        ListView commentsList = dialogView.findViewById(R.id.comments_list);
        EditText commentInput = dialogView.findViewById(R.id.comment_input);
        Button postButton = dialogView.findViewById(R.id.post_button);

        // Load existing comments
        DatabaseBestie db = DatabaseBestie.getInstance();
        db.getCommentsForMoodEvent(moodEvent.getMid(), comments -> {
            CommentAdapter adapter = new CommentAdapter(getContext(), comments);
            commentsList.setAdapter(adapter);
        });

        AlertDialog dialog = builder.create();

        postButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (!commentText.isEmpty()) {
                // Get current user
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    Comment comment = new Comment(moodEvent.getMid(), currentUser.getUid(), commentText);
                    db.addComment(comment, () -> {
                        // Refresh comments after posting
                        db.getCommentsForMoodEvent(moodEvent.getMid(), comments -> {
                            CommentAdapter adapter = new CommentAdapter(getContext(), comments);
                            commentsList.setAdapter(adapter);
                            commentInput.setText("");
                        });
                    });
                }
            }
        });

        dialog.show();
    }





}