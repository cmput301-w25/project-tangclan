package com.example.tangclan;

import java.time.LocalDateTime;

/**
 * Represents a comment on a mood event.
 */
public class Comment {
    private String commentId;
    private String moodEventId; // The ID of the mood event this comment belongs to
    private String userId; // The ID of the user who posted the comment
    private String username; // The username of the user who posted the comment
    private String commentText;
    private LocalDateTime timestamp;

    public Comment() {
        // Default constructor required for Firestore
    }

    public Comment(String moodEventId, String userId, String username, String commentText) {
        this.moodEventId = moodEventId;
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getMoodEventId() {
        return moodEventId;
    }

    public void setMoodEventId(String moodEventId) {
        this.moodEventId = moodEventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}