package com.example.tangclan;

import static java.lang.Integer.parseInt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.firestore.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Contains wrapper functions for method calls on an instance of FireBaseFirestore.
 * Contains Helper functions for querying existing data.
 */
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
     *      Callback to handle the generated uid
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

    /**
     * Callback interface for retrieving a user
     */
    public interface UserCallback {
        void onUserRetrieved(Profile user);
    }

    /**
     * This passes data of the user with the corresponding uid to a callback function
     * @param uid
     *      This is the uid of the user whose data we want to obtain
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

    /**
     * Passes the email corresponding to a given username (or null if username is not taken) to a callback function
     * @param username
     *      the username to check for
     * @param callback
     *      the function that handles the email value
     */
    public void findEmailByUsername(String username, findEmailCallback callback) {
        usersRef.whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String email = document.getString("email");
                        callback.onEmailFound(email);
                    } else {
                        callback.onEmailFound(null);
                    }
                });
    }

    /**
     * Callback function for when email is successfully
     */
    public interface findEmailCallback {
        void onEmailFound(String email);
    }

    public void findProfileByUsername(String username, findProfileCallback callback) {
        usersRef.whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        callback.onProfileFound(document.toObject(Profile.class));
                    } else {
                        callback.onProfileFound(null);
                    }
                });
    }

    /**
     * Callback function for when profile is successfully found
     */
    public interface findProfileCallback {
        void onProfileFound(Profile profile);
    }

    /**
     * Finds email
     * @param email
     *      the email to look for
     * @param callback
     *      the function that handles the email value
     */
    public void checkEmailExists(String email, findEmailCallback callback) {
        usersRef.whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        callback.onEmailFound(email);
                    } else {
                        callback.onEmailFound(null);
                    }
                });
    }

    /**
     * updates the password in the database to match what's stored by FirebaseAuth for that user
     * @param email
     *      the email of the user who's password is to be updated
     * @param password
     *      the current password stored for that user by FirebaseAuth
     */
    public void updatePasswordSameAsFirebaseAuth(String email, String password) {
        usersRef.whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String uid = document.getString("uid");
                        if (uid != null) {
                            DocumentReference user = usersRef.document(uid);

                            user.update("password", password)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Password updated successfully"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating password", e));
                        }
                    }
                });
    }


    /**
     * This updates the details of an existing user
     * @param uid
     *      The id for the user to update
     * @param user
     *      This is the user with updated info
     */
    public void updateUser(String uid, Profile user) {
        usersRef.document(String.valueOf(uid))
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User details successfully updated!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating User details", e));
    }


    // MOODEVENTS COLLECTION METHODS ---------------------------------------------------------------
    /**
     * This adds a mood event details to the "moodEvents" collection
     * @param event
     *      This is the mood event to be added
     * @param month
     *      This is the month (ex. sep-2025) that holds the collection that the mood event will be added to
     */
    public void addMoodEvent(MoodEvent event, String month, String uid) {
        generateMid(mid -> {
            event.setMid(mid);
            Map<String, String> data = Map.of("postedBy", uid);
            moodEventsRef.document(month).collection("events").document(String.valueOf(mid))
                            .set(data);
            moodEventsRef.document(month).collection("events").document(String.valueOf(mid))
                    .set(event.prepFieldsForDatabase(), SetOptions.merge());
        });
    }

    /**
     * This updates the data in an existing mood event
     * @param mid
     *      This is the id of the mood event to be updated
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
                            int mid = parseInt(document.getString("mid")); // will never return null as mid is set when adding an event
                            String emotionalState = document.getString("emotionalState");
                            String situation = document.getString("situation");
                            ArrayList<String> triggers = (ArrayList<String>) document.get("triggers");
                            String postDate = document.getString("datePosted");
                            String postTime = document.getString("timePosted");

                            // mechanism to revert a string back into the bitmap
                            String imageString = document.getString("image");
                            Bitmap image;
                            if (imageString != null) {
                                byte[] imgByteArray = Base64.decode(imageString, Base64.DEFAULT);
                                image = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
                            } else {
                                image = null;
                            }

                            MoodEvent moodEvent = new MoodEvent(emotionalState, triggers, situation);
                            moodEvent.setPostDate(postDate);
                            moodEvent.setPostTime(postTime);
                            moodEvent.setImage(image);


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
     * Retrieves all mood events of the user
     * @param uid
     *      the user ID of the current user
     * @param callback
     *      function to be called when the task is successful.
     */
    public void getAllMoodEvents(String uid, MoodEventsCallback callback) {
        // query should search all collections with id 'events' regardless of month
        db.collectionGroup("events")
                .whereEqualTo("postedBy", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<MoodEvent> moodEvents = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int mid = parseInt(document.getString("mid")); // will never return null as mid is set when adding an event
                            String emotionalState = document.getString("emotionalState");
                            String situation = document.getString("situation");
                            ArrayList<String> triggers = (ArrayList<String>) document.get("triggers");
                            String postDate = document.getString("datePosted");
                            String postTime = document.getString("timePosted");

                            // mechanism to revert a string back into the bitmap
                            String imageString = document.getString("image");
                            Bitmap image;
                            if (imageString != null) {
                                byte[] imgByteArray = Base64.decode(imageString, Base64.DEFAULT);
                                image = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
                            } else {
                                image = null;
                            }

                            MoodEvent moodEvent = new MoodEvent(emotionalState, triggers, situation);
                            moodEvent.setPostDate(postDate);
                            moodEvent.setPostTime(postTime);
                            moodEvent.setImage(image);


                            moodEvents.add(moodEvent);
                        }
                        callback.onMoodEventsRetrieved(moodEvents);
                    }
                    else {
                        Log.e(TAG, "Error getting mood events: ", task.getException());
                        callback.onMoodEventsRetrieved(new ArrayList<>());
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
     * Given the uid of a user, retrieve their latest MoodEvent
     * @param uid
     *      uid of the user which we want to retrieve the latest MoodEvent from
     * @param callback
     *      callback to handle the retrieved MoodEvent
     */
    public void getLatestMoodEvent(String uid, MoodEventCallback callback) {
         db.collectionGroup("events")
                .whereEqualTo("postedBy", uid)
                .orderBy("postDate")
                .limit(1) // limit only to the latest post
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        int mid = parseInt(document.getString("mid"));
                        String emotionalState = document.getString("emotionalState");
                        String situation = document.getString("situation");
                        ArrayList<String> triggers = (ArrayList<String>) document.get("triggers");
                        String postDate = document.getString("datePosted");
                        String postTime = document.getString("timePosted");

                        // mechanism to revert a string back into the bitmap
                        String imageString = document.getString("image");
                        Bitmap image;
                        if (imageString != null) {
                            byte[] imgByteArray = Base64.decode(imageString, Base64.DEFAULT);
                            image = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
                        } else {
                            image = null;
                        }

                        MoodEvent moodEvent = new MoodEvent(emotionalState, triggers, situation);
                        moodEvent.setPostDate(postDate);
                        moodEvent.setPostTime(postTime);
                        moodEvent.setImage(image);

                        callback.onMoodEventRetrieved(moodEvent);
                    }
                });

         // do nothing in the vacuous case
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

    /**
     * Callback interface for retrieving a user's followers
     */
    public interface FollowersCallback {
        /**
         * Called when a user's followers are found
         * @param followers
         *      List of user UIDs beloning to users who follow a specific user.
         */
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
