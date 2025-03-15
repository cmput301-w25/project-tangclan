package com.example.tangclan;

import static android.view.View.FIND_VIEWS_WITH_TEXT;

import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MID = "mid";
    private static final String ARG_MONTH = "month";
    private static final String ARG_FEELING = "feeling";
    private static final String ARG_SITUATION = "social situation";
    private static final String ARG_REASON = "reason";
    private static final String ARG_IMG = "image";
    private static final String ARG_LOC = "location permission";

    // TODO: Rename and change types of parameters
    public String mid;
    public String month;
    private String feeling;
    private String situation;
    private String reason;
    private byte[] image;
    private boolean location_permission;

    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param feeling Parameter 1.
     * @param socialSit Parameter 2.
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String mid, String month, String feeling, String socialSit, String reason, byte[] image, boolean useLoc) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MID, mid);
        args.putString(ARG_MONTH, month);
        args.putString(ARG_FEELING, feeling);
        args.putString(ARG_SITUATION, socialSit);
        args.putString(ARG_REASON, reason);
        args.putByteArray(ARG_IMG, image);
        args.putBoolean(ARG_LOC, useLoc);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mid = getArguments().getString(ARG_MID);
            month = getArguments().getString(ARG_MONTH);
            feeling = getArguments().getString(ARG_FEELING);
            situation = getArguments().getString(ARG_SITUATION);
            reason = getArguments().getString(ARG_REASON);
            image = getArguments().getByteArray(ARG_IMG);
            location_permission = getArguments().getBoolean(ARG_LOC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_details_fragment, container, false);
        RadioGroup emotionOptions = view.findViewById(R.id.emotion_selection);


        ArrayList<View> emotion = new ArrayList<>();
        emotionOptions.findViewsWithText(emotion, feeling, FIND_VIEWS_WITH_TEXT);
        RadioButton selectedEmotion = (RadioButton) emotion.get(0);
        emotionOptions.check(selectedEmotion.getId());  // set checked

        EditText editSocialSit = view.findViewById(R.id.edit_social_situation);
        editSocialSit.setText(situation);

        // implement a dropdown

        EditText editReason = view.findViewById(R.id.edit_reasonwhy);
        editReason.setText(reason);

        // on text change listener, update character count

        if (image != null) {
            ImageButton postImage = view.findViewById(R.id.image_reasonwhy);
            // add image
        }

        Switch useLocation = view.findViewById(R.id.use_location_switch);
        useLocation.setChecked(location_permission);

        Button submitButt = view.findViewById(R.id.submit_details);
        submitButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editSocialSit.getText().toString().length() > 200) {
                    return;
                }

                // update mood event in db
                DatabaseBestie db = new DatabaseBestie();
                db.updateMoodEvent(mid, new MoodEvent(), month);
            }
        });

        ImageButton cancel_butt = view.findViewById(R.id.cancel_edit);
        cancel_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(EditFragment.this).commit();
            }
        });

        return view;
    }
}