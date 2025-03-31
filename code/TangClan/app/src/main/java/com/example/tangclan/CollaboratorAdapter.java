package com.example.tangclan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class CollaboratorAdapter extends ArrayAdapter<Profile> {
    private ArrayList<Profile> userList;
    public CollaboratorAdapter(Context context, ArrayList<Profile> userList) {
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

        Profile user = getItem(position);

        if (user == null) {
            usernameTextView.setText("Unknown");
            return view;
        }

        String username = user.getUsername();
        String pfp = user.getProfilePic();

        usernameTextView.setText(Objects.requireNonNullElse(username, "Unknown"));

        if (pfp != null && !pfp.isEmpty()) {
            byte[] pfpByteArray = Base64.decode(pfp, Base64.DEFAULT);
            Bitmap pfpBitmap = BitmapFactory.decodeByteArray(pfpByteArray, 0, pfpByteArray.length);
            pfpImageView.setImageBitmap(pfpBitmap);
        }

        return view;
    }
}
