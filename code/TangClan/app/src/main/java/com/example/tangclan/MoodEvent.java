package com.example.tangclan;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.function.Consumer;

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
    private Double latitude = null;
    private Double longitude = null;


    /**
     * default constructor for MoodEvent
     * @param emotionalState
     *      emotional state used for the Mood constructor
     */
    public MoodEvent(String emotionalState, Context context) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);

        // create an instance of the LocationManager
        LocationManager moodLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // check if the fine location and coarse location permissions have been granted by the user (enabled during account setup)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // get the current location & bind longitude and latitude to the longitude and latitude attributes of the MoodEvent class
            moodLocationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    context.getApplicationContext().getMainExecutor(),
                    location -> {
                        this.latitude = location.getLatitude();
                        this.longitude = location.getLongitude();
                    }
            );
        }
    }

    /**
     * constructor for MoodEvent with trigger
     * @param emotionalState
     *      emotional state used for the Mood constructor
     * @param trigger
     *      optional list of strings representing the triggers for the MoodEvent
     */
    public MoodEvent(String emotionalState, ArrayList<String> trigger, Context context) {
        this.postTime = LocalTime.now();
        this.postDate = LocalDate.now();

        this.mood = new Mood(emotionalState);
        this.triggers = trigger;

        LocationManager moodLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // check if the fine location and coarse location permissions have been granted by the user (enabled during account setup)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // get the current location & bind longitude and latitude to the longitude and latitude attributes of the MoodEvent class
            moodLocationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    context.getApplicationContext().getMainExecutor(),
                    location -> {
                        this.latitude = location.getLatitude();
                        this.longitude = location.getLongitude();
                    }
            );
        }

    }

    /**
     * constructor for MoodEvent with social situation
     * @param emotionalState
     *      emotional state used for the Mood constructor
     * @param situation
     *      optional string representing a social situation (20 char or 3 word max)
     */
    public MoodEvent(String emotionalState, String situation, Context context) {
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

        LocationManager moodLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // check if the fine location and coarse location permissions have been granted by the user (enabled during account setup)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // get the current location & bind longitude and latitude to the longitude and latitude attributes of the MoodEvent class
            moodLocationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    context.getApplicationContext().getMainExecutor(),
                    location -> {
                        this.latitude = location.getLatitude();
                        this.longitude = location.getLongitude();
                    }
            );
        }
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
    public MoodEvent(String emotionalState, ArrayList<String> trigger, String situation, Context context) {
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

        LocationManager moodLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // check if the fine location and coarse location permissions have been granted by the user (enabled during account setup)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // get the current location & bind longitude and latitude to the longitude and latitude attributes of the MoodEvent class
            moodLocationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    context.getApplicationContext().getMainExecutor(),
                    location -> {
                        this.latitude = location.getLatitude();
                        this.longitude = location.getLongitude();
                    }
            );
        }
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

    public boolean hasGeolocation() {
        return (this.latitude != null && this.longitude != null);
    }


    // helpers
}
