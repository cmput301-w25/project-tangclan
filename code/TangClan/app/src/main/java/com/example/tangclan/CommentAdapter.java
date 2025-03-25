package com.example.tangclan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {
    public CommentAdapter(Context context, List<Comment> comments) {
        super(context, 0, comments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Comment comment = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
        }

        TextView username = convertView.findViewById(R.id.comment_username);
        TextView text = convertView.findViewById(R.id.comment_text);
        TextView timestamp = convertView.findViewById(R.id.comment_timestamp);

        // TODO: Fetch username from UID
        username.setText("User " + comment.getUid().substring(0, 4));
        text.setText(comment.getText());
        timestamp.setText(comment.getPostDate() + " " + comment.getPostTime());

        return convertView;
    }
}