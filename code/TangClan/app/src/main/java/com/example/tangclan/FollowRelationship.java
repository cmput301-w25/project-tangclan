package com.example.tangclan;

/**
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

    /**
     * Constructor
     * @param uidFollowee user being followed
     * @param uidFollower user following
     */
    public FollowRelationship(String uidFollowee, String uidFollower) {
        this.id = "0"; // placeholder
        this.uidFollowee = uidFollowee;
        this.uidFollower = uidFollower;
    }

    /**
     * Getter for the id
     * @return
     *      the Id of the relationship in the database
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for the id
     * @param id
     *      the Id of a new relationship to add to the database
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for uid of the Followee
     * @return
     *       the Id of the user being followed
     */
    public String getUidFollowee() {
        return uidFollowee;
    }

    /**
     * Setter for uid of the Followee
     * @param uidFollowee
     *       the Id of the user being followed
     */
    public void setUidFollowee(String uidFollowee) {
        this.uidFollowee = uidFollowee;
    }

    /**
     * Getter for uid of the Follower
     * @return
     *      the Id of the user following someone
     */
    public String getUidFollower() {
        return uidFollower;
    }

    /**
     * Setter for uid of the Follower
     * @param uidFollower
     *      the Id of the user following someone
     */
    public void setUidFollower(String uidFollower) {
        this.uidFollower = uidFollower;
    }
}