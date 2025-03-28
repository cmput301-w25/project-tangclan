package com.example.tangclan;

import android.graphics.Bitmap;

import android.util.Base64;




import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
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
    private String mid;
    private LocalTime postTime;
    private LocalDate postDate;
    private Mood mood;
    private String reason = null;
    private String setting = null;
    private ArrayList<String> situation = null;
    private Bitmap image = null;
    private Double latitude = null;
    private Double longitude = null;


    private boolean privacyOn = false;

    private ArrayList<String> commentIds = new ArrayList<>();

    private List<String> comments = new ArrayList<>();



    /**
     * Default constructor (required for Firestore)
     */
    public MoodEvent() {
        this.mid = ""; // Placeholder, should be set later
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();
    }

    /**
     * default constructor for MoodEvent
     *
     * @param emotionalState emotional state used for the Mood constructor
     */
    public MoodEvent(String emotionalState) {
        this.mid = "";
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();
        this.mood = new Mood(emotionalState.toLowerCase());

    } //

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
    public String getMid() {
        return this.mid;
    }

    /**
     * Getter for the post date
     *
     * @return The date the MoodEvent was posted
     */
    public LocalDate getPostDate() {
        return this.postDate;
    } //

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
        return Optional.ofNullable(this.situation);
    }

    /**
     * Getter for the reason of the MoodEvent
     * @return
     *      The 200-or-less reason of the MoodEvent
     */
    public Optional<String> getReason() {
        return Optional.ofNullable(this.reason);
    }

    /**
     * sets the postDate attribute from a string
     *
     * @param postDate the String representation of the post Date
     */
    public void setPostDate(String postDate) {
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("dd-MMM-yyyy"), // Format 1
                DateTimeFormatter.ofPattern("yyyy-MM-dd")   // Format 2
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                this.postDate = LocalDate.parse(postDate, formatter);
                return; // Exit if parsing succeeds
            } catch (DateTimeParseException e) {
                // Try the next formatter
            }
        }

        // If no formatter works, throw an exception
        throw new IllegalArgumentException("Invalid date format: " + postDate);
    }

    /**
     * sets the postTime attribute from a string
     *
     * @param postTime the String representation of the Post Time
     */
    public void setPostTime(String postTime) {
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"), // Format 1
                DateTimeFormatter.ofPattern("HH:mm:ss"),        // Format 2
                DateTimeFormatter.ofPattern("HH:mm")           // Format 3
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                this.postTime = LocalTime.parse(postTime, formatter);
                return; // Exit if parsing succeeds
            } catch (DateTimeParseException e) {
                // Try the next formatter
            }
        }

        // If no formatter works, throw an exception
        throw new IllegalArgumentException("Invalid time format: " + postTime);
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
     * Setter for Mood event setting (Alone, with one other person, etc..)
     * @param setting the setting
     */
    public void setSetting(String setting) {
        this.setting = setting;
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
    public void setMid(String id) {
        this.mid = id;
    }

    /**
     * Setter for the MoodEvent situation
     *
     * @param reason The String reason for the reason attribute to be set to
     */
    public void setReason(String reason) {
        // raise an exception if the reason exceeds the length limit
        if (reason != null && reason.length() > 200) {
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

    public boolean isPrivacyOn() {
        return privacyOn;
    }

    public void setPrivacyOn(boolean privacyOn) {
        this.privacyOn = privacyOn;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

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
        String imageString = null;
        if (this.image != null) {
            byte[] bytes = ImageValidator.compressBitmapToSize(this.image);
            imageString = Base64.encodeToString(bytes, Base64.DEFAULT);
        }

        // convert LocalDate and LocalTime into a storeable string
        String dateString = userFormattedDate();
        String timeString = userFormattedTime();

        Map<String, Object> moodEventFields = new HashMap<>();
        moodEventFields.put("mid", this.mid);
        moodEventFields.put("emotionalState", this.mood.getEmotion());
        moodEventFields.put("collaborators", this.situation);
        moodEventFields.put("setting",this.setting);
        moodEventFields.put("reason", this.reason);
        moodEventFields.put("image", imageString);
        moodEventFields.put("privateMood",this.privacyOn);
        moodEventFields.put("datePosted", dateString);
        moodEventFields.put("timePosted", timeString);



        return moodEventFields;
    }

    public ArrayList<String> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(ArrayList<String> commentIds) {
        this.commentIds = commentIds;
    }

    public void addCommentId(String commentId) {
        this.commentIds.add(commentId);
    }

    public String getSetting() {
        return setting;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public void addComment(String comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }
}