package com.example.tangclan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import android.os.Bundle;

import android.graphics.drawable.Drawable;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
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

        ImageButton commentButton = view.findViewById(R.id.comment_button);
        commentButton.setOnClickListener(v -> {
            showCommentDialog(moodEvent);
        });

        DatabaseBestie db = new DatabaseBestie();
        String month = moodEvent.userFormattedDate().substring(3);
        db.getAuthorOfMoodEvent(moodEvent.getMid(), month, username -> {
            moodToUsernameMap.put(moodEvent, username);

            // call after grabbing username to change unknown to user
            this.notifyDataSetChanged();
        });


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

        ImageView emoticonView = view.findViewById(R.id.emoticon);

        try {
            Drawable emoticon = mood.getEmoticon(getContext());
            if (emoticon != null) {
                emoticonView.setImageDrawable(emoticon);
                emoticonView.setVisibility(View.VISIBLE);
            } else {
                emoticonView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            emoticonView.setVisibility(View.GONE);
            Log.e("MoodEventAdapter", "Error loading emoticon", e);
        }

        // Set username and emotion on the TextView
        TextView usernameEmotion = view.findViewById(R.id.username_emotional_state);

        String setting = moodEvent.getSetting();
        ArrayList<String> collaborators;

        if (moodEvent.getCollaborators().isPresent()) {
            collaborators = moodEvent.getCollaborators().get();

            // remove all empty tags, if any
            //collaborators.removeAll(Collections.singleton(""));

            int numCollaborators = collaborators.size();



            if (numCollaborators > 0) {
                spannableUsernameEmotion.append(" with ");
                SpannableString spannableSituation;

                if (numCollaborators == 1) {
                    spannableSituation = new SpannableString("one other person");
                } else if (numCollaborators <= 7) { // the condition numCollaborators >= 2 is also true in this block
                    spannableSituation = new SpannableString("two to several people");
                } else {
                    spannableSituation = new SpannableString("a crowd");
                }

                // underline to indicate clickable
                spannableSituation.setSpan(new UnderlineSpan(), 0, spannableSituation.length(), 0);
                spannableSituation.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        showCollaborators(moodEvent);
                    }
                }, 0, spannableSituation.length(), 0);
                // set the onClick/onTouch listener for the tags
                spannableUsernameEmotion.append(spannableSituation);
            } else {
                spannableUsernameEmotion.append(" alone");
            }
        } else {
            if (setting != null && !setting.isEmpty()) {
                spannableUsernameEmotion.append(" ").append(setting);
            }
        }

        usernameEmotion.setMovementMethod(LinkMovementMethod.getInstance());
        usernameEmotion.setText(spannableUsernameEmotion);

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

        DatabaseBestie bestie = new DatabaseBestie();

        // Associate mock usernames with each mood event for display purposes
        // In a real implementation, these would come from the database
        int userCounter = 1;
        String month;
        for (MoodEvent event : moodEvents) {
            Log.d("MOODEVENTADAPTER", "where am i pt 2");
            month = event.userFormattedDate().substring(3);
            Log.d("MOODEVENTADAPTER", "current mid is" + event.getMid() + "and month is"+month);
            bestie.getAuthorOfMoodEvent(event.getMid(), month, username -> {
                moodToUsernameMap.put(event, username);
            });
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

        DatabaseBestie db = new DatabaseBestie();
        ArrayList<Profile> users = new ArrayList<>();

        CollaboratorAdapter adapter = new CollaboratorAdapter(context, users);
        tags.setAdapter(adapter);

        for (String username : collaborators) {
            db.findProfileByUsername(username, profile -> {
                users.add(profile);
                adapter.notifyDataSetChanged();
            });
        }


        tags.setOnItemClickListener((a, view, pos, l) -> {
            Profile profile = adapter.getItem(pos);
            goToUserProfile(profile);
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow()
                .setBackgroundDrawable(ResourcesCompat
                        .getDrawable(context.getResources(), R.drawable.dialog_round, null));
        dialog.show();
    }

    public void goToUserProfile(Profile profile) {
        Intent intent = new Intent(getContext(), ViewOtherProfileActivity.class);
        Bundle profileDetails = new Bundle();


        profileDetails.putString("uid", profile.getUid());
        profileDetails.putString("username",profile.getUsername());
        profileDetails.putString("email",profile.getEmail());
        profileDetails.putString("displayName",profile.getDisplayName());
        String pfpStr = profile.getProfilePic();
        if (pfpStr != null) {
            byte[] decodedBytes = Base64.decode(pfpStr, Base64.DEFAULT);
            Bitmap toCompress = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            byte[] pfpBytes = ImageValidator.compressBitmapToSize(toCompress);
            if (pfpBytes != null) {
                profileDetails.putString("pfp", Base64.encodeToString(pfpBytes, Base64.DEFAULT));
            }
        } else {
            profileDetails.putString("pfp", null);
        }

        intent.putExtras(profileDetails);
        getContext().startActivity(intent);
    }
}