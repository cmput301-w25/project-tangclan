package com.example.tangclan;

import android.graphics.Bitmap;

import android.util.Base64;




import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.Map;
import java.util.Optional;



/**
 * Represents a Mood Event
 * RELATED USER STORIES:
 *      US 01.01.01
 *      1.01.01a Create a mood event class
 * TODO: Method to get the current location
 */
public class MoodEvent implements Serializable {
    private int mid;
    private LocalTime postTime;
    private LocalDate postDate;
    private Mood mood;
    private String reason = null;
    private ArrayList<String> situation = null;
    private Bitmap image = null;
    private Double latitude = null;
    private Double longitude = null;

    /**
     * Default constructor (required for Firestore)
     */
    public MoodEvent() {
        this.mid = -1; // Placeholder, should be set later
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();
    }

    /**
     * default constructor for MoodEvent
     *
     * @param emotionalState emotional state used for the Mood constructor
     */
    public MoodEvent(String emotionalState) {
        this.mid = mid;
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();
        this.mood = new Mood(emotionalState);

    }

    /**
     * constructor for MoodEvent with collaborators
     *
     * @param emotionalState emotional state used for the Mood constructor
     * @param collaborators  optional list of strings representing the collaborators for the MoodEvent
     */
    public MoodEvent(String emotionalState, ArrayList<String> collaborators) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);
        this.situation = collaborators;
    }

    /**
     * constructor for MoodEvent with social situation
     *
     * @param emotionalState emotional state used for the Mood constructor
     * @param reason      optional string representing a social situation (200 char max)
     */
    public MoodEvent(String emotionalState, String reason) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);

        // raise an exception if the reason exceeds length limit
        if (reason.length() > 200) {
            throw new IllegalArgumentException();
        }

        this.reason = reason;
    }

    /**
     * constructor for MoodEvent with collaborators and reason
     *
     * @param emotionalState emotional state used for the Mood constructor
     * @param collaborators       list of strings representing the collaborators for the MoodEvent
     * @param reason      optional string representing reason (200 char max)
     */
    public MoodEvent(String emotionalState, ArrayList<String> collaborators, String reason) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);
        this.situation = collaborators;

        // convert into stream and count the number of spaces
        int spaceCount = (int) reason.chars().filter(ch -> ch == ' ').count();

        // raise an exception if the reason exceeds length limit
        if (reason.length() > 200) {
            throw new IllegalArgumentException();
        }

        this.reason = reason;
    }

    // getters, setters

    /**
     * Getter for the Mood Event ID
     *
     * @return the ID of the Mood Event in the database
     */
    public int getMid() {
        return this.mid;
    }

    /**
     * Getter for the post date
     *
     * @return The date the MoodEvent was posted
     */
    public LocalDate getPostDate() {
        return this.postDate;
    }

    /**
     * Getter for the post time
     *
     * @return the time the MoodEvent was posted
     */
    public LocalTime getPostTime() {
        return this.postTime;
    }

    /**
     * Getter for the mood object of the Mood Event
     *
     * @return the MoodEvent's associated Mood object
     */
    public Mood getMood() {
        return this.mood;
    }

    /**
     * Getter for the emotional state of the mood
     *
     * @return The Mood Event's associated emotional state, in the Mood object
     */
    public String getMoodEmotionalState() {
        return this.mood.getEmotion();
    }


    /**
     * Getter for the list of collaborators of the Mood Event
     * @return
     *      An optional object containing either null, or a list of collaborator usernames
     */
    public Optional<ArrayList<String>> getCollaborators() {
        return Optional.of(this.situation);
    }

    /**
     * Getter for the reason of the MoodEvent
     * @return
     *      The 200-or-less reason of the MoodEvent
     */
    public Optional<String> getReason() {
        return Optional.of(this.reason);
    }

    /**
     * sets the postDate attribute from a string
     *
     * @param postDate the String representation of the post Date
     */
    public void setPostDate(String postDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");


        this.postDate = LocalDate.parse(postDate, formatter);
    }

    /**
     * sets the postTime attribute from a string
     *
     * @param postTime the String representation of the Post Time
     */
    public void setPostTime(String postTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.postTime = LocalTime.parse(postTime, formatter);
    }

    /**
     * Setter for the Mood object for the MoodEvent
     *
     * @param emotionalState the emotionalState that distinguishes the Mood
     */
    public void setMood(String emotionalState) {
        this.mood.setEmotion(emotionalState);
    }

    /**
     * Setter for the Mood event collaborators
     *
     * @param collaborators The list of collaborators for the situation attribute to be set to
     */
    public void setCollaborators(ArrayList<String> collaborators) {
        this.situation = collaborators;
    }


    /**
     * Setter for the MoodEvent ID of the MoodEvent
     *
     * @param id the id of the Mood Event to be set to
     */
    public void setMid(int id) {
        this.mid = id;
    }

    /**
     * Setter for the MoodEvent situation
     *
     * @param reason The String reason for the reason attribute to be set to
     */
    public void setReason(String reason) {
        // raise an exception if the reason exceeds the length limit
        if (reason.length() > 200) {
            throw new IllegalArgumentException();
        }

        this.reason = reason;
    }

    /**
     * Setter for the image attribute
     *
     * @param image an image in bitmap form
     */
    public void setImage(Bitmap image) {
        this.image = image;
    }

    /**
     * Getter for the image attribute
     *
     * @return a bitmap image
     */
    public Bitmap getImage() {
        return this.image;
    }


    /**
     * Checks if the MoodEvent has a geolocation
     *
     * @return A boolean value indicating whether the MoodEvent has a geolocation
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
     *
     * @return the string date of the MoodEvent date
     */
    public String returnPostFormattedDate() {
        // format the
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM, d, uuuu");

        return this.postDate.format(formatter);
    }

    /**
     * Creates a string-formatted time of the form "{HOUR}:{MINUTES}{DAY-SEGMENT}"
     *
     * @return the string time of the MoodEventTime e.g. '10:30PM'
     */
    public String returnPostFormattedTime() {
        // format the time with an hour (no leading 0), minutes (leading 0) and the time segment (AM PM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        return this.postTime.format(formatter);
    }

    /**
     * Creates a string-formatted date of the form "{YEAR}-{MONTH}-{DAY}"
     *
     * @return The date in the formatter pattern
     */
    public String userFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return this.postDate.format(formatter);
    }

    /**
     * Creates a string-formatted time of the form "{HR}:{MINUTE}"
     *
     * @return The time in the formatter pattern
     */
    public String userFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return this.postTime.format(formatter);
    }

    /**
     * preps all fields for database
     *
     * @return a map with field : firestore-allowable values
     */
    public Map<String, Object> prepFieldsForDatabase() {
        // convert the bitmap into a storeable string
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        this.image.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] bytes = output.toByteArray();
        String imageString = Base64.encodeToString(bytes, Base64.DEFAULT);

        // convert LocalDate and LocalTime into a storeable string
        String dateString = userFormattedDate();
        String timeString = userFormattedTime();

        Map<String, Object> moodEventFields = Map.of(
                "mid", this.mid,
                "emotionalState", this.mood.getEmotion(),
                "collaborators", this.situation,
                "reason", this.reason,
                "image", imageString,
                "datePosted", dateString,
                "timePosted", timeString
        );

        return moodEventFields;
    }
}