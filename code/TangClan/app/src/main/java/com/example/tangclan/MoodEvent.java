package com.example.tangclan;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents a Mood Event
 */
public class MoodEvent {
    private int mid;
    private final LocalTime postTime;
    private final LocalDate postDate;
    private ArrayList<String> triggers = null;
    private Mood mood;
    private String situation = null;
    private Bitmap image;
    private Double latitude = null;
    private Double longitude = null;



    /**
     * default constructor for MoodEvent
     * @param emotionalState
     *      emotional state used for the Mood constructor
     */
    public MoodEvent(String emotionalState) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);

        // create an instance of the LocationManager
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
        int spaceCount = (int) situation.chars().filter(ch -> ch == ' ').count(); //cast to int because .count returns long

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

    /**
     * Getter for the Mood Event ID
     * @return
     *  the ID of the Mood Event in the database
     */
    public int getMid() {
        return this.mid;
    }

    /**
     * Getter for the post date
     * @return
     *      The date the MoodEvent was posted
     */
    public LocalDate getPostDate() {
        return this.postDate;
    }

    /**
     * Getter for the post time
     * @return
     *      the time the MoodEvent was posted
     */
    public LocalTime getPostTime() {
        return this.postTime;
    }

    /**
     * Getter for the mood object of the Mood Event
     * @return
     *      the MoodEvent's associated Mood object
     */
    public Mood getMood() {
        return this.mood;
    }

    /**
     * Getter for the emotional state of the mood
     * @return
     *      The Mood Event's associated emotional state, in the Mood object
     */
    public String getMoodEmotionalState() {
        return this.mood.getEmotion();
    }

    /**
     * Getter for the optional triggers of the mood
     * @return
     *      an Optional object possibly containing a list of triggers.
     */
    public Optional<ArrayList<String>> getTriggers() {
        return Optional.ofNullable(this.triggers);
    }

    /**
     * Getter for the optional situation of the mood
     * @return
     *      an Optional object possibly containing a situation
     */
    public Optional<String> getSituation() {
        return Optional.ofNullable(this.situation);
    }

    /**
     * Getter for the image in bitmap form of the MoodEvent
     * @return
     */
    public Bitmap getImage() {
        return this.image;
    }

    /**
     * Setter for the MoodEvent ID of the MoodEvent
     * @param id
     *      the id of the Mood Event to be set to
     */
    public void setMid(int id) {
        this.mid = id;
    }

    /**
     * Setter for the Mood object for the MoodEvent
     * @param emotionalState
     *      the emotionalState that distinguishes the Mood
     */
    public void setMood(String emotionalState) {
        this.mood.setEmotion(emotionalState);
    }

    /**
     * Setter for the Mood event triggers
     * @param triggers
     *      The list of triggers for the triggers attribute to be set to
     */
    public void setTriggers(ArrayList<String> triggers) {
        this.triggers = triggers;
    }

    /**
     * Setter for the MoodEvent situation
     * @param situation
     *      The String situation for the situation attribute to be set to
     */
    public void setSituation(String situation) {
        // convert into stream and count the number of spaces
        int spaceCount = (int) situation.chars().filter(ch -> ch == ' ').count();

        // raise an exception if the social situation exceeds length or word limit
        if ((situation.length() > 20) || (spaceCount > 2)) {
            throw new IllegalArgumentException();
        }

        this.situation = situation;
    }

    /**
     * Setter for the image attribute
     * @param image
     *      an image in bitmap form
     */
    public void setImage(Bitmap image) {
        this.image = image;
    }

    /**
     * Checks if the MoodEvent has a geolocation
     * @return
     *      A boolean value indicating whether the MoodEvent has a geolocation
     */
    public boolean hasGeolocation() {
        return (this.latitude != null && this.longitude != null);
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }





    // helpers

    /**
     * Creates a string-formatted date of the form "{DAY-OF-WEEK}, {MONTH} {DATE}, {YEAR}"
     * @return
     *      the string date of the MoodEvent date
     */
    public String returnFormattedDate() {
        // format the
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM, d, uuuu");

        return this.postDate.format(formatter);
    }

    /**
     * Creates a string-formatted time of the form "{HOUR}:{MINUTES}{DAY-SEGMENT}"
     * @return
     *      the string time of the MoodEventTime e.g. '10:30PM'
     */
    public String returnFormattedTime() {
        // format the time with an hour (no leading 0), minutes (leading 0) and the time segment (AM PM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        return this.postTime.format(formatter);
    }
}
