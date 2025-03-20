package com.example.tangclan;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a Mood
 * The Mood class is tied to a single emotional state, which is used to request system resources for color and emoticon
 * RELATED USER STORIES:
 *      US 01.02.01
 *      US 01.03.01
 */
public class Mood {
    private String emotion;

    /**
     * Constructor for the mood class
     * @param emotion
     *      the emotional state of the mood
     */
    public Mood(String emotion) {
        ArrayList<String> emotionsList = new ArrayList<>(List.of(
                "happy",
                "sad",
                "angry",
                "anxious",
                "ashamed",
                "calm",
                "confused",
                "disgusted",
                "no idea",
                "surprised",
                "terrified"));
        if (!(emotionsList.contains(emotion))) {
            Log.e("no", emotion);
            throw new IllegalArgumentException();
        }

        this.emotion = emotion;
    }

    /**
     * Sets the emotional state of the mood
     * @param emotionalState
     *      The emotional state of the mood
     */
    public void setEmotion(String emotionalState) {
        ArrayList<String> emotionsList = new ArrayList<>(List.of(
                "happy",
                "sad",
                "angry",
                "anxious",
                "ashamed",
                "calm",
                "confused",
                "disgusted",
                "no idea",
                "surprised",
                "terrified"));
        if (!(emotionsList.contains(emotion))) {
            throw new IllegalArgumentException();
        }

        this.emotion = emotionalState;
    }

    /**
     * Get the emotional state of the mood
     * @return
     *      the emotional state of the mood
     */
    public String getEmotion() {
        return emotion;
    }

    /**
     *  Gets the drawable corresponding to the emotional state
     * @return
     *      The corresponding drawable for the emotional state`
     */

    public Drawable getEmoticon (Context context) {

        Resources res = context.getResources();

        // initialize a map from each emotional state to the corresponding emoticon resource (drawable).
        // this should have a suitable handler if any of the getDrawable calls return null
        //TODO: implement a try/catch block to return a generic system icon
        Map<String, Drawable> mapEmotionEmoticon = Map.ofEntries(
                Map.entry("happy", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_happy, null))),
                Map.entry("sad", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_sadness, null))),
                Map.entry("angry", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_angy, null))),
                Map.entry("anxious", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_anxious, null))),
                Map.entry("ashamed", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_ashamed, null))),
                Map.entry("calm", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_calm, null))),
                Map.entry("confused", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_confuse, null))),
                Map.entry("disgusted", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_disgust, null))),
                Map.entry("surprised", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_surprised, null))),
                Map.entry("terrified", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_terrified, null))),
                Map.entry("no idea", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_no_idea, null)))
        );


        return mapEmotionEmoticon.get(this.emotion);
    }

    /**
     * gets an integer representation of the Mood Color given the emotional State
     * @return
     *      The integer (hexadecimal) representation of the emotional state's color
     */
    public Integer getColor (Context context) {
        Resources res = context.getResources();

        // initialize a map from each emotional state to the corresponding color resource (Color).
        // the values are integers in hexadecimal representing the color
        //TODO: implement a try/catch block that will return a generic color for a ResourceNotFound except
        Map<String, Integer> mapEmotionColor = Map.ofEntries(
                Map.entry("happy", ResourcesCompat.getColor(res, R.color.happy, null)),
                Map.entry("sad", ResourcesCompat.getColor(res, R.color.sad, null)),
                Map.entry("angry", ResourcesCompat.getColor(res, R.color.angry, null)),
                Map.entry("anxious", ResourcesCompat.getColor(res, R.color.anxious, null)),
                Map.entry("ashamed", ResourcesCompat.getColor(res, R.color.ashamed, null)),
                Map.entry("calm", ResourcesCompat.getColor(res, R.color.calm, null)),
                Map.entry("confused", ResourcesCompat.getColor(res, R.color.confused, null)),
                Map.entry("disgusted", ResourcesCompat.getColor(res, R.color.disgusted, null)),
                Map.entry("surprised", ResourcesCompat.getColor(res, R.color.surprised, null)),
                Map.entry("terrified", ResourcesCompat.getColor(res, R.color.terrified, null)),
                Map.entry("no idea", ResourcesCompat.getColor(res, R.color.no_idea, null)) // Corrected duplicate key
        );

        return mapEmotionColor.get(this.emotion);
    }
}
