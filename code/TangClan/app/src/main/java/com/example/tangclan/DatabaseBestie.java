package com.example.tangclan;


import android.util.Log;
import com.google.firebase.firestore.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    public void addUser(Profile user) {
        db.collection("users").document(user.getUid()).set(user);
    }

    /**
     * This returns data of the user with the corresponding uid
     * @param uid
     *      This is the uid of the user whose data we want to obtain
     * @return
     *      a User object with the corresponding data of an existing user
     */
    public Profile getUser(String uid) {
        final Profile[] user = new Profile[1];
        DocumentReference usersRef = db.collection("users").document(uid);
        usersRef.get().addOnSuccessListener(documentSnapshot -> user[0] = documentSnapshot.toObject(Profile.class));
        return user[0];
    }
    // MOODEVENTS COLLECTION METHODS ---------------------------------------------------------------

    /**
     * This adds a mood event details to the "moodEvents" collection
     * @param event
     *      This is the mood event to be added
     * @param month
     *      This is the month (ex. sep-2025) that holds the collection that the mood event will be added to
     */
    public void addMoodEvent(MoodEvent event, String month) {
        db.collection("moodEvents").document(month).collection(month).document(event.getMid()).set(event);
    }


    /**
     * This updates the data in an existing mood event
     * @param event
     *      This is the event to be updated
     * @param month
     *      This is the month (ex. sep-2025) of when the to-be-updated mood event was initially added
     */
    public void updateMoodEvent(MoodEvent event, String month) {
        db.collection("moodEvents").document(month).collection(month).document(event.getMid()).update(event);
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
     * This returns a list of dictionaries containing details of mood events created by a specific user during the given month
     * @param uid
     *      This is the uid of the user who created the mood events in the returned list
     * @param month
     *      This is the month of when all the mood events in the returned list were created
     */
    public ArrayList<MoodEvent> getMoodEvents(String uid, String month) {
        ArrayList<MoodEvent> events = new ArrayList<>();
        db.collection("moodEvents").document(month).collection(month)
                .whereEqualTo("postedBy",uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Add to list of mids
                            MoodEvent mood = document.toObject(MoodEvent.class);
                            events.add(mood);


                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        return events;
    }

    /**
     * This return 1 mood event given its creator, date posted, time posted
     * @param uid
     * @param month
     * @param date
     * @param time
     * @return
     */
    public MoodEvent getMoodEvent(String uid, String month, LocalDate date, LocalTime time) {
        MoodEvent post = null;
        ArrayList<MoodEvent> moodEvents = getMoodEvents(uid, month);
        for (MoodEvent event: moodEvents) {
            if ((event.getPostDate() == date) && (event.getPostTime() == time)) {
                post = event;
            }
            else {
                Log.d(TAG, "No Mood Event found");
            }
        }
        return post;
    }


    // FOLLOWS COLLECTION METHODS ------------------------------------------------------------------

    /**
     * This adds a follow relationship to the "follows" collection
     * @param followRelationship
     *      This is the relationship to be added
     */
    public void addFollowRelationship(FollowRelationship followRelationship) {
        db.collection("follows").document(followRelationship.getId()).set(followRelationship);
    }

    /**
     * This removes an existing relationship from the "follows" collection
     * @param id
     *      This is the ID of the relationship to be deleted
     */
    public void deleteFollowRelationship(String id) {
        db.collection("follows").document(id).delete();
    }

    /**
     * This returns a list of UIDs identifying users that follow a specified user
     * @param uid
     *      This is the uid of the user being followed by users in the returned list
     * @return
     *      This is the list of followers of uid
     */
    public ArrayList<String> getFollowers(String uid) {
        ArrayList<String> followers = new ArrayList<>();
        db.collection("follows")
                .whereEqualTo("uidFollowee",uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Add to list of followers
                            Map<String, Object> data = document.getData();
                            followers.add((String) data.get("uidFollower"));

                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        return followers;
    }

    /**
     * This returns a list of UIDs identifying users that a specified user follows
     * @param uid
     *      This is the uid of the user that follows users in the returned list
     * @return
     *      This is the list of uids of users being followed
     */
    public ArrayList<String> getFollowing(String uid) {
        ArrayList<Profile> users = new ArrayList<>();
        ArrayList<String> following = new ArrayList<>();

        db.collection("follows")
                .whereEqualTo("uidFollower",uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Add to list of followers
                            Map<String, Object> data = document.getData();
                            following.add((String) data.get("uidFollowee"));

                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        for (String userId : following) {
            DocumentReference userRef = db.collection("users").document(userId);
            DocumentSnapshot document = userRef.get().getResult();

            if (document.exists()) {
                Profile user = document.toObject(Profile.class);
                user.setUid(document.getId()); // Set the ID manually
                users.add(user);
            } else {
                System.out.println("No user found with ID: " + userId);
                return null;
            }
        }
        return following;
    }
}
