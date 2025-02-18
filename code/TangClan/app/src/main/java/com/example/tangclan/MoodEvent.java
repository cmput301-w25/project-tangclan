package com.example.tangclan;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Mood Event
 */
public class MoodEvent {
    private int mid;
    private final LocalTime postTime;
    private final LocalDate postDate;
    private ArrayList<String> triggers;
    private Mood mood;
    private String situation;


    /**
     * default constructor for MoodEvent
     * @param emotionalState
     *      emotional state used for the Mood constructor
     */
    public MoodEvent(String emotionalState) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);
    }

    /**
     * constructor for MoodEvent with trigger
     * @param emotionalState
     *      emotional state used for the Mood constructor
     * @param trigger
     *      optional list of strings representing the triggers for the MoodEvent
     */
    public MoodEvent(String emotionalState, ArrayList<String> trigger) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);
        this.triggers = trigger;
    }

    /**
     * constructor for MoodEvent with social situation
     * @param emotionalState
     *      emotional state used for the Mood constructor
     * @param situation
     *      optional string representing a social situation (20 char or 3 word max)
     */
    public MoodEvent(String emotionalState, String situation) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);

        // convert into stream and count the number of spaces
        int spaceCount = (int) situation.chars().filter(ch -> ch == ' ').count();

        // raise an exception if the social situation exceeds length or word limit
        if ((situation.length() > 20) || (spaceCount > 2)) {
            throw new IllegalArgumentException();
        }

        this.situation = situation;
    }

    /**
     * constructor for MoodEvent with trigger and situation
     * @param emotionalState
     *      emotional state used for the Mood constructor
     * @param trigger
     *      list of strings representing the triggers for the MoodEvent
     * @param situation
     *      optional string representing a social situation (20 char or 3 word max)
     */
    public MoodEvent(String emotionalState, ArrayList<String> trigger, String situation) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);
        this.triggers = trigger;

        // convert into stream and count the number of spaces
        int spaceCount = (int) situation.chars().filter(ch -> ch == ' ').count();

        // raise an exception if the social situation exceeds length or word limit
        if ((situation.length() > 20) || (spaceCount > 2)) {
            throw new IllegalArgumentException();
        }

        this.situation = situation;
    }

    // getters, setters
    public LocalDate getPostDate() {
        return this.postDate;
    }

    public LocalTime getPostTime() {
        return this.postTime;
    }

    public Mood getMood() {
        return this.mood;
    }

    public String getMoodEmotionalState() {
        return this.mood.getEmotion();
    }

    public String getMoodColor() {
        return this.mood.getColor();
    }

    public String getMoodEmoticon() {
        return this.mood.getEmoticon();
    }


    public ArrayList<String> getTriggers() {
        return this.triggers;
    }

    public String getSituation() {
        return this.situation;
    }

    public void setMood(String emotionalState) {
        this.mood.setEmotion(emotionalState);
    }

    public void setTriggers(ArrayList<String> triggers) {
        this.triggers = triggers;
    }

    public void setSituation(String situation) {
        // convert into stream and count the number of spaces
        int spaceCount = (int) situation.chars().filter(ch -> ch == ' ').count();

        // raise an exception if the social situation exceeds length or word limit
        if ((situation.length() > 20) || (spaceCount > 2)) {
            throw new IllegalArgumentException();
        }

        this.situation = situation;
    }


    // helpers
}
