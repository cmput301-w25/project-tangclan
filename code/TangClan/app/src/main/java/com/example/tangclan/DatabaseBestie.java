package com.example.tangclan;


import android.graphics.Movie;
import android.util.Log;
import com.google.firebase.firestore.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.Map;

public class DatabaseBestie {
    private static final String TAG = "DatabaseBestie";
    private static DatabaseBestie instance;
    private FirebaseFirestore db;
    private DocumentReference moodEventCounterRef;
    private DocumentReference userCounterRef;

    private DocumentReference followRelCounterRef;

    private CollectionReference usersRef;
    private CollectionReference moodEventsRef;
    private CollectionReference followsRef;

    /**
     * This gets an instance of the database
     */
    DatabaseBestie() {
        db = FirebaseFirestore.getInstance();
        // counter references
        moodEventCounterRef = db.collection("Counters").document("mood_event_counter");
        userCounterRef = db.collection("Counters").document("user_counter");
        followRelCounterRef = db.collection("Counters").document("follow_counter");

        // collection references
        usersRef = db.collection("users");
        moodEventsRef = db.collection("moodEvents");
        followsRef = db.collection("follows");
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

    // UNIVERSAL ID GEN SYSTEM ---------------------------------------------------------------------

    /**
     * Callback interface for retrieving gen. unique ids
     */

    public interface IdCallback {
        /**
         * Called when a unique id has been successfully generated
         *
         * @param id
         *      The generated unique id
         */
        void onIdGenerated(int id);
    }

    /**
     * Generates a unique ID by retrieving the last used ID from Firestore,
     * incrementing it, and updating Firestore to maintain uniqueness
     *
     * @param counterRef
     *      Reference to the Firestore document storing the last used id
     * @param field
     *      The field name storing the last used id in the Firestore doc
     * @param callback
     *      Callback to handle the generated unique id async
     */
    private void generateUniqueId(DocumentReference counterRef, String field, IdCallback callback) {
        counterRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                Long lastId = document.getLong(field);
                if (lastId == null) lastId = 0L;
                int newId = lastId.intValue() + 1;

                // Update Firestore with the new ID
                counterRef.update(field, newId).addOnSuccessListener(aVoid -> {
                    callback.onIdGenerated(newId);
                }).addOnFailureListener(e -> Log.e(TAG, "Failed to update counter", e));

            } else {
                // If document doesn't exist, initialize it
                counterRef.set(Collections.singletonMap(field, 1))
                        .addOnSuccessListener(aVoid -> callback.onIdGenerated(1))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to initialize counter", e));
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to retrieve counter", e));
    }

    /**
     * Generates a unique mid and provides it through a callback.
     *
     * @param callback
     *       Callback to handle the generated mid
     */
    public void generateMid(IdCallback callback) {
        generateUniqueId(moodEventCounterRef, "last_mid", callback);
    }

    /**
     * Generates a unique uid and provides it through a callback.
     *
     * @param callback
     *      Callback to handle the generated `uid
     */
    public void generateUid(IdCallback callback) {
        generateUniqueId(userCounterRef, "last_uid", callback);
    }

    /**
     * Generates a unique fid and provides it through a callback.
     *
     * @param callback
     *      Callback to handle the generated fid
     */
    public void generateFid(IdCallback callback) {
        generateUniqueId(followRelCounterRef, "last_fid", callback);
    }

    //----------------------------------------------------------------------------------------------
    // USER COLLECTION METHODS ---------------------------------------------------------------------
    //checked
    /**
     * This adds a user's data to the "users" collection
     * @param user
     *      This is the user to be added
     */
    public void addUser(Profile user) {
        generateUid(uid -> {
            user.setUid(String.valueOf(uid));
            usersRef.document(user.getUid()).set(user);
        });
    }

    public interface UserCallback {
        void onUserRetrieved(Profile user);
    }

    /**
     * This returns data of the user with the corresponding uid
     * @param uid
     *      This is the uid of the user whose data we want to obtain
     * @return
     *      a User object with the corresponding data of an existing user
     */
    public void getUser(String uid, UserCallback callback) {
        DocumentReference userRef = usersRef.document(uid);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                callback.onUserRetrieved(documentSnapshot.toObject(Profile.class));
            } else {
                callback.onUserRetrieved(null);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error retrieving user", e);
            callback.onUserRetrieved(null);
        });
    }

    // MOODEVENTS COLLECTION METHODS ---------------------------------------------------------------
    // checked
    /**
     * This adds a mood event details to the "moodEvents" collection
     * @param event
     *      This is the mood event to be added
     * @param month
     *      This is the month (ex. sep-2025) that holds the collection that the mood event will be added to
     */
    public void addMoodEvent(MoodEvent event, String month) {
        generateMid(mid -> {
            event.setMid(mid);
            moodEventsRef.document(month).collection("events").document(String.valueOf(mid)).set(event);
        });
    }
     //checked
     /**
     * This updates the data in an existing mood event
     * @param event
     *      This is the event to be updated
     * @param month
     *      This is the month (ex. sep-2025) of when the to-be-updated mood event was initially added
     */
     public void updateMoodEvent(String mid, MoodEvent event, String month) {
         moodEventsRef.document(month).collection("events")
                 .document(String.valueOf(mid))
                 .set(event)
                 .addOnSuccessListener(aVoid -> Log.d(TAG, "MoodEvent successfully updated!"))
                 .addOnFailureListener(e -> Log.e(TAG, "Error updating MoodEvent", e));
     }
    //checked
    /**
     * This removes an existing mood event from the "moodEvents" collection
     * @param mid
     *      This is the MID of the mood event to be deleted
     * @param month
     *      This is the month (ex. sep-2025) of when the to-be-deleted mood event was initially added
     */
    public void deleteMoodEvent(String mid, String month) {
        moodEventsRef.document(month).collection("events").document(String.valueOf(mid)).delete();
    }

    /**
     * Retrieves a list of MoodEvent objects created by a specific user during a given month
     *
     * @param uid
     *      The uid of the user who created the mood events
     * @param month
     *      The month (e.g., "sep-2025") for which mood events should be retrieved
     * @param callback
     *      Callback to handle the retrieved list of MoodEvent objects
     */
    public void getMoodEvents(String uid, String month, MoodEventsCallback callback) {
        moodEventsRef.document(month).collection("events")
                .whereEqualTo("postedBy", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<MoodEvent> events = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MoodEvent moodEvent = document.toObject(MoodEvent.class);
                            events.add(moodEvent);
                        }
                        callback.onMoodEventsRetrieved(events);
                    } else {
                        Log.e(TAG, "Error getting mood events: ", task.getException());
                        callback.onMoodEventsRetrieved(new ArrayList<>()); // Return empty list on failure
                    }
                });
    }

    /**
     * Callback interface for handling retrieved MoodEvent objects async
     */
    public interface MoodEventsCallback {
        /**
         * Called when the list of MoodEvents is successfully retrieved
         *
         * @param events The list of MoodEvent objects
         */
        void onMoodEventsRetrieved(ArrayList<MoodEvent> events);
    }

    /**
     * Retrieves a single MoodEvent based on creator, date, and time
     *
     * @param uid
     *      The uid of the user who created the mood event
     * @param month
     *      The month (e.g., "sep-2025") in which the mood event was created
     * @param date
     *      The exact date the mood event was posted
     * @param time
     *      The exact time the mood event was posted
     * @param callback
     *      Callback to handle the retrieved MoodEvent
     */
    public void getMoodEvent(String uid, String month, LocalDate date, LocalTime time, MoodEventCallback callback) {
        getMoodEvents(uid, month, events -> {
            MoodEvent post = null;
            for (MoodEvent event : events) {
                if (event.getPostDate().equals(date) && event.getPostTime().equals(time)) {
                    post = event;
                    break;
                }
            }
            callback.onMoodEventRetrieved(post);
        });
    }

    /**
     * Callback interface for retrieving a single MoodEvent async
     */
    public interface MoodEventCallback {
        /**
         * Called when a MoodEvent is retrieved
         *
         * @param event
         *      The retrieved MoodEvent, or null if not found
         */
        void onMoodEventRetrieved(MoodEvent event);
    }


    // FOLLOWS COLLECTION METHODS ------------------------------------------------------------------

    public interface FollowersCallback {
        void onFollowersRetrieved(ArrayList<String> followers);
    }

    /**
     * This adds a follow relationship to the "follows" collection
     * @param followRelationship
     *      This is the relationship to be added
     */
    public void addFollowRelationship(FollowRelationship followRelationship) {
        generateFid(uid -> {
            followRelationship.setId(String.valueOf(uid));
            followsRef.document(followRelationship.getId()).set(followRelationship);
        });
    }

    /**
     * This removes an existing relationship from the "follows" collection
     * @param id
     *      This is the ID of the relationship to be deleted
     */
    public void deleteFollowRelationship(String id) {
        followsRef.document(id).delete();
    }

    /**
     * This returns a list of UIDs identifying users that follow a specified user
     * @param uid
     *      This is the uid of the user being followed by users in the returned list
     * @return
     *      This is the list of followers of uid
     */
    public void getFollowers(String uid, FollowersCallback callback) {
        followsRef.whereEqualTo("uidFollowee", uid).get()
                .addOnCompleteListener(task -> {
                    ArrayList<String> followers = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            followers.add((String) data.get("uidFollower"));
                        }
                    }
                    callback.onFollowersRetrieved(followers);
                });
    }

    /**
     * This returns a list of UIDs identifying users that a specified user follows
     * @param uid
     *      This is the uid of the user that follows users in the returned list
     * @return
     *      This is the list of uids of users being followed
     */
    public void getFollowing(String uid, FollowingCallback callback) {
        followsRef.whereEqualTo("uidFollower", uid)
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<String> following = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            following.add((String) data.get("uidFollowee"));
                        }
                        callback.onFollowingRetrieved(following);
                    } else {
                        Log.e(TAG, "Error getting following: ", task.getException());
                        callback.onFollowingRetrieved(new ArrayList<>()); // Return empty list if error
                    }
                });
    }

    /**
     * Callback interface for retrieving a list of users that a specified user follows.
     */
    public interface FollowingCallback {
        /**
         * Called when the list of followed users is retrieved.
         *
         * @param following List of user UIDs that the user follows.
         */
        void onFollowingRetrieved(ArrayList<String> following);
    }
}
