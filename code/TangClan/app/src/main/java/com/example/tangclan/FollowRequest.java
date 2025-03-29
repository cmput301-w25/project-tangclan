package com.example.tangclan;

import java.util.Date;

/**
 * Represents a follow request between users
 */
public class FollowRequest {
    private String rid;
    private String requesterUid;
    private String targetUid;
    private Date timestamp;
    private String status;

    /**
     * No-argument constructor for Firebase deserialization
     */
    public FollowRequest() {
    }

    /**
     * Constructor with required fields
     * @param requesterUid The UID of the user making the request
     * @param targetUid The UID of the user receiving the request
     */
    public FollowRequest(String requesterUid, String targetUid) {
        this.requesterUid = requesterUid;
        this.targetUid = targetUid;
        this.timestamp = new Date();
        this.status = "pending";
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRequesterUid() {
        return requesterUid;
    }

    public void setRequesterUid(String requesterUid) {
        this.requesterUid = requesterUid;
    }

    public String getTargetUid() {
        return targetUid;
    }

    public void setTargetUid(String targetUid) {
        this.targetUid = targetUid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}