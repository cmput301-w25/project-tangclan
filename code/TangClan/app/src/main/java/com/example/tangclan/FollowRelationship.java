package com.example.tangclan;

/* NOTES:
 * This class establishes a follower-followee relationship to be stored in the database
 * FollowRelationships can be checked to populate a user's FollowingBook.
 * For example, you would look through the 'follows' collection and if you see a relationship
 * where userA is a follower and userB is a followee, then you may could add userA's profile
 * to the list of followers in userB's following book.
 */
public class FollowRelationship {
    private String id;
    private String uidFollowee;
    private String uidFollower;

    public FollowRelationship( String uidFollowee, String uidFollower) {
        this.id = "0"; // placeholder
        this.uidFollowee = uidFollowee;
        this.uidFollower = uidFollower;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUidFollowee() {
        return uidFollowee;
    }

    public void setUidFollowee(String uidFollowee) {
        this.uidFollowee = uidFollowee;
    }

    public String getUidFollower() {
        return uidFollower;
    }

    public void setUidFollower(String uidFollower) {
        this.uidFollower = uidFollower;
    }
}
