package com.example.tangclan;

import android.util.Log;

import com.google.firebase.firestore.*;

import java.util.Map;

public class DatabaseBestie {
    private static final String TAG = "DatabaseBestie";
    private static DatabaseBestie instance;
    private FirebaseFirestore db;

    /**
     * This gets an instance of the database
     */
    private DatabaseBestie() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * This constructs the database helper
     */
    public static synchronized DatabaseBestie getInstance() {
        if (instance == null) {
            instance = new DatabaseBestie();
        }
        return instance;
    }

    // USER COLLECTION METHODS ---------------------------------------------------------------------

    /**
     * This adds a user's data to the "users" collection
     * @param user
     *      This is the user to be added
     */
    public void addUser(User user) {
        db.collection("users").document(user.getUID()).set(user);
    }

    /**
     * This returns data of the user with the corresponding uid
     * @param uid
     *      This is the uid of the user whose data we want to obtain
     * @return
     *      a User object with the corresponding data of an existing user
     */
    public User getUser(String uid) {

    }

    // MOOD COLLECTION METHODS ---------------------------------------------------------------------

    /**
     * This adds a mood's details to the "moods" collection
     * @param mood
     *      This is the mood to be added
     */
    public void addMood(Mood mood) {
        db.collection("moods").document(mood.getState()).set(mood);
    }

    /**
     * This returns data of the mood with the corresponding emotional state
     *      @param emotionalState
     *           This is the emotional state (ex. 'Happy', 'Sad') of the mood whose data we want to obtain
     *      @return
     *           a Mood object with the specified emotion state and its associated data
     */
    public Mood getMood(String emotionalState) {
        db.collection("moods").document(emotionalState).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> fields = documentSnapshot.getData();

                        // create a mood object with the data to return
                    }}
        );
        // return mood;
    }

    // MOODEVENTS COLLECTION METHODS ---------------------------------------------------------------

    /**
     * This adds a mood event details to the "moodEvents" collection
     * @param event
     *      This is the mood event to be added
     * @param month
     *      This is the month (ex. sep-2025) that holds the collection that the mood event will be added to
     */
    public void addMoodEvent(moodEvent event, String month) {
        db.collection("moodEvents").document(month).collection(month).document(event.getMID()).set(event);
    }


    /**
     * This updates the data in an existing mood event
     * @param mid
     *      This is the MID of the mood event whose data will be updated
     * @param month
     *      This is the month (ex. sep-2025) of when the to-be-updated mood event was initially added
     */
    public void updateMoodEvent(String mid, String month) {

    }

    /**
     * This removes an existing mood event from the "moodEvents" collection
     * @param mid
     *      This is the MID of the mood event to be deleted
     * @param month
     *      This is the month (ex. sep-2025) of when the to-be-deleted mood event was initially added
     */
    public void deleteMoodEvent(String mid, String month) {
        db.collection("moodEvents").document(month).collection(month).document(mid).delete();
    }

    /**
     * This returns a mood event
     * @param mid
     *      This is the MID of the mood event to be returned
     * @param month
     *      This is the month (ex. sep-2025) of when the to-be-returned mood event was initially added
     * @return
     *      A MoodEvent object with the specified mid and its associated data
     */
    public MoodEvent getMoodEvent(String mid, String month) {

    }

    // FOLLOWS COLLECTION METHODS ------------------------------------------------------------------

    /**
     * This adds a follow relationship to the "follows" collection
     * @param followRelationship
     *      This is the relationship to be added
     */
    public void addFollowRelationship(FollowRelationship followRelationship) {
        db.collection("follows").document(followRelationship.getID()).set(followRelationship);
    }

    /**
     * This returns data of the followRelationship with the corresponding id
     * @param id
     *      This is the id of the relationship whose data will be obtained
     * @return
     *      a FollowRelationShip object with the corresponding id
     */
    public FollowRelationship getFollowRelationship(String id) {

    }
}
