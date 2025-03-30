package com.example.tangclan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adapter for displaying mood events in a ListView.
 * This class retrieves mood events and associates them with usernames.
 * It ensures that mood events are displayed with formatted text and optional images.
 */
public class MoodEventAdapter extends ArrayAdapter<MoodEvent> {
    private Map<String, String> uidToUsernameMap; // Maps UIDs to usernames
    private DatabaseBestie db;
    private AtomicInteger pendingUsernameFetches;

    public MoodEventAdapter(Context context, List<MoodEvent> moodEvents) {
        super(context, 0, moodEvents);
        this.uidToUsernameMap = new HashMap<>();
        this.db = DatabaseBestie.getInstance();
        this.pendingUsernameFetches = new AtomicInteger(0);
        fetchAllUsernames(moodEvents);
    }

    public MoodEventAdapter(Context context, FollowingBook followingBook) {
        super(context, 0, new ArrayList<>());
        this.uidToUsernameMap = new HashMap<>();
        this.db = DatabaseBestie.getInstance();
        this.pendingUsernameFetches = new AtomicInteger(0);

        followingBook.getRecentMoodEvents(db, new DatabaseBestie.MoodEventsCallback() {
            @Override
            public void onMoodEventsRetrieved(ArrayList<MoodEvent> events) {
                clear();
                addAll(events);
                fetchAllUsernames(events);
            }
        });
    }

    private void fetchAllUsernames(List<MoodEvent> moodEvents) {
        if (moodEvents == null || moodEvents.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        // Clear existing mappings
        uidToUsernameMap.clear();
        pendingUsernameFetches.set(0);

        // Count unique UIDs that need to be fetched
        Set<String> uniqueUids = new HashSet<>();
        for (MoodEvent event : moodEvents) {
            String uid = event.getPostedBy();
            if (uid != null && !uid.isEmpty()) {
                uniqueUids.add(uid);
            }
        }

        if (uniqueUids.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        pendingUsernameFetches.set(uniqueUids.size());

        // Fetch all usernames
        for (String uid : uniqueUids) {
            db.getUser(uid, new DatabaseBestie.UserCallback() {
                @Override
                public void onUserRetrieved(Profile user) {
                    synchronized (uidToUsernameMap) {
                        if (user != null && user.getUsername() != null) {
                            uidToUsernameMap.put(uid, user.getUsername());
                        } else {
                            uidToUsernameMap.put(uid, "Anonymous");
                        }
                    }

                    if (pendingUsernameFetches.decrementAndGet() == 0) {
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView != null ? convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.content_mood_event_new, parent, false);

        MoodEvent moodEvent = getItem(position);
        if (moodEvent == null) return view;

        String uid = moodEvent.getPostedBy();
        String username = uid != null ? uidToUsernameMap.getOrDefault(uid, "Anonymous") : "Anonymous";

        // Setup views
        TextView usernameEmotion = view.findViewById(R.id.username_emotional_state);
        ImageView emoticonView = view.findViewById(R.id.emoticon);
        ImageButton commentButton = view.findViewById(R.id.comment_button);
        TextView reasonView = view.findViewById(R.id.reason);
        TextView dateView = view.findViewById(R.id.date_text);
        TextView timeView = view.findViewById(R.id.time_text);
        ImageView imageView = view.findViewById(R.id.mood_event_image);

        // Set comment button listener
        commentButton.setOnClickListener(v -> showCommentDialog(moodEvent));

        // Create formatted text
        SpannableStringBuilder spannableText = new SpannableStringBuilder();

        // Format username as bold
        SpannableString usernameSpan = new SpannableString(username);
        usernameSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, usernameSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableText.append(usernameSpan);

        // Get mood and emotion safely
        Mood mood = moodEvent.getMood();
        if (mood != null) {
            // Set the emoticon image
            Drawable emoticon = mood.getEmoticon(getContext());
            if (emoticon != null) {
                emoticonView.setImageDrawable(emoticon);
                emoticonView.setVisibility(View.VISIBLE);
            } else {
                emoticonView.setVisibility(View.GONE);
            }

            String emotion = mood.getEmotion();
            if (emotion != null && !emotion.isEmpty()) {
                spannableText.append(" is feeling ");

                // Add emotion with color
                SpannableString emotionSpan = new SpannableString(emotion);
                Integer color = mood.getColor(getContext().getApplicationContext());
                if (color != null) {
                    emotionSpan.setSpan(
                            new ForegroundColorSpan(color),
                            0,
                            emotionSpan.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
                spannableText.append(emotionSpan);
            }
        } else {
            emoticonView.setVisibility(View.GONE);
        }

        // Add social situation if available
        Optional<ArrayList<String>> collaboratorsOpt = moodEvent.getCollaborators();
        if (collaboratorsOpt.isPresent()) {
            ArrayList<String> collaborators = collaboratorsOpt.get();
            int count = collaborators.size();

            if (count > 0) {
                String situationText = count == 1 ? " with one other person" :
                        count <= 7 ? " with two to several people" :
                                " with a crowd";

                SpannableString situationSpan = new SpannableString(situationText);
                situationSpan.setSpan(new UnderlineSpan(), 0, situationSpan.length(), 0);
                situationSpan.setSpan(new ClickableSpan() {
                    @Override public void onClick(@NonNull View v) {
                        showCollaborators(moodEvent);
                    }
                }, 0, situationSpan.length(), 0);

                spannableText.append(situationSpan);
            } else {
                spannableText.append(" alone");
            }
        } else if (moodEvent.getSetting() != null && !moodEvent.getSetting().isEmpty()) {
            spannableText.append(" ").append(moodEvent.getSetting());
        }

        // Set all view values
        usernameEmotion.setMovementMethod(LinkMovementMethod.getInstance());
        usernameEmotion.setText(spannableText);
        reasonView.setText(moodEvent.getReason().orElse("No reason specified"));
        dateView.setText(moodEvent.returnPostFormattedDate());
        timeView.setText(moodEvent.returnPostFormattedTime());

        if (moodEvent.getImage() != null) {
            imageView.setImageBitmap(moodEvent.getImage());
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
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
        fetchAllUsernames(moodEvents);
    }
    private void showCommentDialog(MoodEvent moodEvent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_comments, null);
        builder.setView(dialogView);

        ListView commentsList = dialogView.findViewById(R.id.comments_list);
        EditText commentInput = dialogView.findViewById(R.id.comment_input);
        ImageButton postButton = dialogView.findViewById(R.id.post_button);

        DatabaseBestie db = DatabaseBestie.getInstance();
        db.getCommentsForMoodEvent(moodEvent.getMid(), comments -> {
            CommentAdapter adapter = new CommentAdapter(getContext(), comments);
            commentsList.setAdapter(adapter);
        });

        AlertDialog dialog = builder.create();

        postButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (!commentText.isEmpty()) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    Comment comment = new Comment(moodEvent.getMid(), currentUser.getUid(), commentText);
                    db.addComment(comment, () -> {
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



    private void showCollaborators(MoodEvent moodEvent) {
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_tagged,null);
        builder.setView(dialogView);

        ListView tags = dialogView.findViewById(R.id.listview_tagged);

        ArrayList<String> collaborators = moodEvent.getCollaborators().get(); // non-null handled
        CollaboratorAdapter adapter = new CollaboratorAdapter(context, collaborators);
        tags.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow()
                .setBackgroundDrawable(ResourcesCompat
                        .getDrawable(context.getResources(), R.drawable.dialog_round, null));
        dialog.show();
    }
}