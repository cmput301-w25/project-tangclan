package com.example.tangclan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Adapter specifically for displaying the current user's mood events on their profile
 */
public class MoodEventProfileAdapter extends ArrayAdapter<MoodEvent> {

    private Profile userProfile;

    /**
     * Constructor for the MoodEventProfileAdapter
     *
     * @param context The activity context
     * @param userProfile The profile of the current user
     */
    public MoodEventProfileAdapter(Context context, Profile userProfile) {
        super(context, 0, userProfile.getMoodEventBook().getMoodEventList());
        this.userProfile = userProfile;
    }

    /**
     * Updates the adapter with the latest mood events from the user's profile
     */
    public void refreshMoodEvents() {
        clear();
        addAll(userProfile.getMoodEventBook().getMoodEventList());
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Use the existing getView method from your MoodEventAdapter class
        View view = super.getView(position, convertView, parent);

        return view;
    }
}