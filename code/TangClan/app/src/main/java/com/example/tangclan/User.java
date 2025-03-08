package com.example.tangclan;

import java.util.Date;

public class User {
    private String uid; // apparently Firebase makes a unique id for the user DOCUMENT, we can use that
    private Date dateAccCreated;
    private Date lastPosted;

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
}
