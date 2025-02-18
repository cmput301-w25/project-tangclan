package com.example.tangclan;

import java.util.ArrayList;
import java.util.Date;

public class User {
    private String username;
    private String email;
    private Date dateAccCreated;
    private Date lastPosted;
    private String uid; // apparently Firebase makes a unique id for the user DOCUMENT, we can use that
    private ArrayList<Integer> moodHistory;

    public User(String username, String email, Date date_created) {
        this.uid = uid;
        this.moodHistory = new ArrayList<>();
        this.username = username;
        this.email = email;
        this.dateAccCreated = date_created;
        this.lastPosted = null;
    }

    public String getUsername() {
        return username;
    }


    public String getEmail() {
        return email;
    }


    public Date getDateAccCreated() {
        return dateAccCreated;
    }


    public Date getLastPosted() {
        return lastPosted;
    }

    public String getUid() {
        return uid;
    }

    public ArrayList<Integer> getMoodHistory() {
        return moodHistory;
    }


    // setters

    public void setLastPosted(Date lastPosted) {
        this.lastPosted = lastPosted;
    }

    public void setMoodHistory(ArrayList<Integer> moodHistory) {
        this.moodHistory = moodHistory;
    }
}
