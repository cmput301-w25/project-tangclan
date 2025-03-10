package com.example.tangclan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MoodEventAdapter extends ArrayAdapter<MoodEvent> {
    private Context context;
    private List<MoodEvent> moodEvents;

    public MoodEventAdapter(Context context, List<MoodEvent> moodEvents) {
        super(context, 0, moodEvents);
        this.context = context;
        this.moodEvents = moodEvents;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MoodEvent moodEvent = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_mood_event, parent, false);
        }


        TextView usernameTextView = convertView.findViewById(R.id.username_emotional_state);
        TextView moodTextView = convertView.findViewById(R.id.situation);
        TextView dateTextView = convertView.findViewById(R.id.date_text);
        TextView timeTextView = convertView.findViewById(R.id.time_text);


        usernameTextView.setText(moodEvent.getMoodEmotionalState());
        moodTextView.setText(moodEvent.getSituation());


        String date = moodEvent.getPostDate().toString();
        String time = moodEvent.getPostTime().toString();
        dateTextView.setText(date);
        timeTextView.setText(time);

        return convertView;
    }
}
