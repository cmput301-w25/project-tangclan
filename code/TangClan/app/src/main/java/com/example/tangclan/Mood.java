package com.example.tangclan;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a Mood
 * The Mood class is tied to a single emotional state, which is used to request system resources for color and emoticon
 */
public class Mood {
    private final String emotion;

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
                "neutral",
                "surprised",
                "terrified"));
        if (!(emotionsList.contains(emotion))) {
            throw new IllegalArgumentException();
        }

        this.emotion = emotion;
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
     * 
     * @param context
     *      The current activity context used to grab system resources
     * @return
     *     The corresponding drawable for the emotional state`
     */
    public Drawable getEmoticon (Context context) {
        Resources res = context.getResources();

        // initialize a map from each emotional state to the corresponding emoticon resource (drawable). 
        // this should have a suitable handler if any of the getDrawable calls return null
        //TODO: implement a try/catch block to return a generic system icon
        Map<String, Drawable> mapEmotionEmoticon = Map.of(
                "happy", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_happy, null)),
                "sad", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_sadness, null)),
                "angry", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_angy, null)),
                "anxious", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_anxious, null)),
                "ashamed", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_ashamed, null)),
                "calm", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_calm, null)),
                "confused", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_confuse, null)),
                "disgusted", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_disgust, null)),
                "surprised", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_surprised, null)),
                "terrified", Objects.requireNonNull(ResourcesCompat.getDrawable(res, R.drawable.le_terrified, null))
        );

        return mapEmotionEmoticon.get(this.emotion);
    }

    /**
     *
     * @param context
     *      The context passed in order to access resources
     * @return
     *      The integer (hexadecimal) representation of the emotional state's color
     */
    public Integer getColor (Context context) {
        Resources res = context.getResources();

        // initialize a map from each emotional state to the corresponding color resource (Color).
        // the values are integers in hexadecimal representing the color
        //TODO: implement a try/catch block that will return a generic color for a ResourceNotFound except
        Map<String, Integer> mapEmotionColor = Map.of(
                "happy", (ResourcesCompat.getColor(res, R.color.happy, null)),
                "sad", (ResourcesCompat.getColor(res, R.color.sad, null)),
                "angry", (ResourcesCompat.getColor(res, R.color.angry, null)),
                "anxious", (ResourcesCompat.getColor(res, R.color.anxious, null)),
                "ashamed", (ResourcesCompat.getColor(res, R.color.ashamed, null)),
                "calm", (ResourcesCompat.getColor(res, R.color.calm, null)),
                "confused", (ResourcesCompat.getColor(res, R.color.confused, null)),
                "disgusted", (ResourcesCompat.getColor(res, R.color.disgusted, null)),
                "surprised", (ResourcesCompat.getColor(res, R.color.surprised, null)),
                "terrified", (ResourcesCompat.getColor(res, R.color.terrified, null))
        );

        return mapEmotionColor.get(this.emotion);
    }
}
