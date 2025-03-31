package com.example.tangclan;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_mood_event_new, parent, false);
        } else {
            view = convertView;
        }

        MoodEvent moodEvent = getItem(position);
        if (moodEvent == null) return view;


        // Format the username and mood emotion
        SpannableStringBuilder spannableUsernameEmotion = new SpannableStringBuilder();

        ImageButton commentButton = view.findViewById(R.id.comment_button);
        commentButton.setOnClickListener(v -> showCommentDialog(moodEvent));


        if (username == null) {
            username = LoggedInUser.getInstance().getUsername();
        }
        //  Log.d("DEBUG PROFILEHISTORYADAPTER", username);
        SpannableString spannableUsername = new SpannableString(username);
        spannableUsername.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int moodColor = moodEvent.getMood().getColor(getContext());
        int transparentMoodColor = Color.argb(178, Color.red(moodColor), Color.green(moodColor), Color.blue(moodColor)); // 178 = 70% opacity

        SpannableString spannableEmotionalState = new SpannableString(moodEvent.getMood().getEmotion());
        spannableEmotionalState.setSpan(new ForegroundColorSpan(moodColor), 0, spannableEmotionalState.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableUsernameEmotion.append(spannableUsername).append(" is feeling ").append(spannableEmotionalState);

        Optional<ArrayList<String>> collaborators = moodEvent.getCollaborators();
        String setting = moodEvent.getSetting();

        if (collaborators.isPresent() && !collaborators.get().isEmpty() && !collaborators.get().get(0).isEmpty()) {
            int numCollaborators =  collaborators.get().size();

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
                        Log.d("test1", "here");
                        showCollaborators(moodEvent);
                    }
                }, 0, spannableSituation.length(), 0);
                // set the onClick/onTouch listener for the tags
                spannableUsernameEmotion.append(spannableSituation);
            } else {
                spannableUsernameEmotion.append("alone");
            }
        } else {
            if (setting != null && !setting.isEmpty()) {
                spannableUsernameEmotion.append(" ").append(setting);
            }
        }


        // Find views by ID
        TextView emotionTextView = view.findViewById(R.id.username_emotional_state);
        //TextView situationTextView = view.findViewById(R.id.situation);
        TextView reasonTextView = view.findViewById(R.id.reason);
        TextView dateTextView = view.findViewById(R.id.date_text);
        TextView timeTextView = view.findViewById(R.id.time_text);
        ImageView moodImageView = view.findViewById(R.id.mood_event_image);
        ImageView moodIcon = view.findViewById(R.id.emoticon); // Emoticon ImageView

        // Set the emoticon for the mood
        Drawable emoticonDrawable = moodEvent.getMood().getEmoticon(getContext());
        if (emoticonDrawable != null) {
            moodIcon.setImageDrawable(emoticonDrawable);  // Use setImageDrawable to display the icon
        }

        // Set the emotional state
        emotionTextView.setMovementMethod(LinkMovementMethod.getInstance());
        emotionTextView.setText(spannableUsernameEmotion);//


        // Set the reason (in a mini box)
        Optional<String> reason = moodEvent.getReason();
        if (reason.isPresent() && !reason.get().isEmpty()) {
            reasonTextView.setText(reason.get());
        } else {
            reasonTextView.setText("");
        }
        // Set the post date and time
        dateTextView.setText(moodEvent.returnPostFormattedDate());
        timeTextView.setText(moodEvent.returnPostFormattedTime());

        // Set the image (if available)
        if (moodEvent.getImage() != null) {
            moodImageView.setImageBitmap(moodEvent.getImage());
            moodImageView.setVisibility(View.VISIBLE); // Show image
        } else {
            moodImageView.setVisibility(View.GONE); // Hide image if not available
        }



        return view;
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

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow()
                .setBackgroundDrawable(ResourcesCompat
                        .getDrawable(context.getResources(), R.drawable.dialog_round, null));
        dialog.show();
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

        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow()
                .setBackgroundDrawable(ResourcesCompat
                        .getDrawable(getContext().getResources(), R.drawable.dialog_round, null));
        dialog.show();
    }



}