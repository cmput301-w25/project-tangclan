package com.example.tangclan;

import java.util.ArrayList;
import java.util.Date;

public class User {
    private String uid; // apparently Firebase makes a unique id for the user DOCUMENT, we can use that
    private Date dateAccCreated;
    private Date lastPosted;
    private FollowingBook followingBook;
    private MoodEventBook moodEventBook;

    public User() {
        this.uid = null;
        this.dateAccCreated = new Date(); // gets current Date, probably should be formatted
        this.lastPosted = null;  // null for a new user
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getDateAccCreated() {
        return dateAccCreated;
    }

    public void setDateAccCreated(Date dateAccCreated) {
        this.dateAccCreated = dateAccCreated;
    }

    public Date getLastPosted() {
        return lastPosted;
    }

    public void setLastPosted(Date lastPosted) {
        this.lastPosted = lastPosted;
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
     * initializes the user's FollowinbBook by querying the database for all user followers,
     * following, and any outstanding follow requests
     * @param db
     */
    public void initializeFollowingBookFromDatabase(DatabaseBestie db) {
        db.getFollowers(this.uid, followers -> User.this.followingBook.setFollowers(followers));
    }
}
