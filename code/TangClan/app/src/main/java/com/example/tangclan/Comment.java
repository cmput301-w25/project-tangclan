package com.example.tangclan;

import java.time.LocalDate;
import java.time.LocalTime;

public class Comment {
    private String cid;
    private String mid; // MoodEvent ID this comment belongs to
    private String uid; // User ID who posted the comment
    private String text;
    private LocalDate postDate;
    private LocalTime postTime;

    public Comment() {
        // Default constructor required for Firestore
    }

    public Comment(String mid, String uid, String text) {
        this.mid = mid;
        this.uid = uid;
        this.text = text;
        this.postDate = LocalDate.now();
        this.postTime = LocalTime.now();
    }

    // Getters and setters
    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }
    public String getMid() { return mid; }
    public void setMid(String mid) { this.mid = mid; }
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDate getPostDate() { return postDate; }
    public void setPostDate(LocalDate postDate) { this.postDate = postDate; }
    public LocalTime getPostTime() { return postTime; }
    public void setPostTime(LocalTime postTime) { this.postTime = postTime; }
}