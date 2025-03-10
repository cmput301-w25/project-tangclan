package com.example.tangclan;

import java.util.ArrayList;
import java.util.Date;

/**
 * Holds additional data tied to a user
 */
public class User {
    private String uid; // apparently Firebase makes a unique id for the user DOCUMENT, we can use that
    private Date dateAccCreated;
    private Date lastPosted;
    private FollowingBook followingBook;
    private MoodEventBook moodEventBook;

    /**
     * Constructor
     */
    public User() {
        this.uid = null;
        this.dateAccCreated = new Date(); // gets current Date, probably should be formatted
        this.lastPosted = null;  // null for a new user
        this.moodEventBook = new MoodEventBook();
    }

    /**
     * Getter for the uid
     * @return
     *      the id of the user
     */
    public String getUid() {
        return uid;
    }

    /**
     * Setter for the uid
     * @param uid
     *      the id of the user
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Getter for the date that the user's account was created
     * @return
     *      date of account creation
     */
    public Date getDateAccCreated() {
        return dateAccCreated;
    }

    /**
     * Setter for the date that the user's account was created
     * @param dateAccCreated
     *      date of account creation
     */
    public void setDateAccCreated(Date dateAccCreated) {
        this.dateAccCreated = dateAccCreated;
    }

    /**
     * Getter for the date of when the user last posted
     * @return
     *      date of when the user last posted
     */
    public Date getLastPosted() {
        return lastPosted;
    }

    /**
     * Setter for the date of when the user last posted
     * @param lastPosted
     *      date of when the user last posted
     */
    public void setLastPosted(Date lastPosted) {
        this.lastPosted = lastPosted;
    }

    public MoodEventBook getMoodEventBook() {
        return moodEventBook;
    }




    /**
     * initializes the user's moodEventBook by querying the database for all user
     * MoodEvents
     * @param db
     *      a Database wrapper with functionality to grab all mood events from user
     */
    public void initializeMoodEventBookFromDatabase(DatabaseBestie db) {
        // call getAllMoodEvents with callback that sets the MoodEventBook list to the callback arg
        db.getAllMoodEvents(this.uid, events -> User.this.moodEventBook.setMoodEvents(events));
    }

    /**
     * initializes the user's FollowingBook by querying the database for all user followers,
     * following, and any outstanding follow requests
     * @param db
     *      Moodly's database wrapper with functionality to grab follower and following
     *      for a user
     */
    public void initializeFollowingBookFromDatabase(DatabaseBestie db) {
        db.getFollowers(this.uid, followers -> User.this.followingBook.setFollowers(followers));
        db.getFollowing(this.uid, following -> User.this.followingBook.setFollowing(following));
        //TODO: get any outstanding follow requests
        // not relevant until next project part
    }

    /**
     * Updates the MoodEventBook and database in parallel, and sets dateLastPosted
     * @param event
     *      the MoodEvent to be added
     * @param db
     *      Moodly's database wrapper with functionality to add a MoodEvent to the database
     */
    public void post(MoodEvent event, DatabaseBestie db) {
        String postDate = event.userFormattedDate();
        // update MoodEventBook and database in parallel
        db.addMoodEvent(event, postDate.substring(5), this.uid); // substr from 5 excludes the year
        this.moodEventBook.addMoodEvent(event);

        //update date last posted
        this.setLastPosted(postDate);
    }
}
