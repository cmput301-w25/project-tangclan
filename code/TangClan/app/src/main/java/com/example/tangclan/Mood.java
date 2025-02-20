package com.example.tangclan;

import java.util.Map;

/**
 * Represents a Mood
 */
public class Mood {
    final private Map<String, String> emotionColorsMap = Map.of(
            //TODO: replace colors with hex
            "happy", "yellow",
            "calm", "blue-light",
            "surprised,", "tan",
            "disgusted", "green-dark",
            "terrified", "magenta",
            "confused", "yellow-light",
            "ashamed", "red-light",
            "angry", "red"
    );

    private String emotion;

    public Mood(String emotion) {
        if (!(emotionColorsMap.containsKey(emotion))) {
            throw new IllegalArgumentException();
        }

        this.emotion = emotion;
    }

    public String getEmotion() {
        return this.emotion;
    }

    public void setEmotion(String emotion) {
        if (!(emotionColorsMap.containsKey(emotion))) {
            throw new IllegalArgumentException();
        }

        this.emotion = emotion;
    }

    public String getColor() {
        return emotionColorsMap.get(this.emotion);
    }

    public String getEmoticon() {
        //TODO: implement logic to calculate the emoticon from the color
        return "Placeholder emoticon";
    }
}
