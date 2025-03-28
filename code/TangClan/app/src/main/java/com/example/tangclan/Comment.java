package com.example.tangclan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {
    private String cid;
    private String mid; // MoodEvent ID this comment belongs to
    private String uid; // User ID who posted the comment
    private String text;
    private Date timestamp; // Changed from LocalDate/LocalTime to Date

    public Comment() {
        // Default constructor required for Firestore
    }

    public Comment(String mid, String uid, String text) {
        this.mid = mid;
        this.uid = uid;
        this.text = text;
        this.timestamp = new Date(); // Current time
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
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    // Helper methods to format the date/time for display
    public String getFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timestamp);
    }

    public String getFormattedTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp);
    }
}