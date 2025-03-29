package com.example.tangclan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CollaboratorAdapter extends ArrayAdapter<String> {
    private ArrayList<String> userList;
    public CollaboratorAdapter(Context context, ArrayList<String> userList) {
        super(context, 0, userList);
        this.userList = userList;
    }

    @Override
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_tagged, parent, false);
        } else {
            view = convertView;
        }

        TextView usernameTextView = view.findViewById(R.id.tag_username);
        ImageView pfpImageView = view.findViewById(R.id.tag_pfp);

        String username = getItem(position);

        usernameTextView.setText(username);

        return view;
    }
}
