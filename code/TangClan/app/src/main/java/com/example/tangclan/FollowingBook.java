package com.example.tangclan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FollowingBook {
    private ArrayList<String> following;
    private ArrayList<String> followers;
    //private ArrayList<Followers> followRequests;

    public FollowingBook() {
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        //this.followRequests = new ArrayList<>();
    }

    // Handle follow requests
    //public void addFollowRequest(Profile sender) {
    //    followRequests.add(new FollowRequest(sender));
    //}

    //public void acceptFollowRequest(Profile sender) {
    //    followRequests.removeIf(request -> request.getSender().equals(sender));
    //    followers.add(sender);
    //}

    //public void rejectFollowRequest(Profile sender) {
    //   followRequests.removeIf(request -> request.getSender().equals(sender));
    //}

    // Manage following relationships
    //public void follow(Profile profile) {
    //    if (!following.contains(profile)) {
    //        following.add(profile);
    //        profile.getFollowingBook().addFollower(this);
    //    }
    //}

    //public void unfollow(Profile profile) {
    //    following.remove(profile);
    //    profile.getFollowingBook().removeFollower(this);
    //}

    /**
     * adds a follower to the following book's follower list
     * @param uid
     *      the uid of the follower to be added
     */
    public void addFollower(String uid) {
        if (followers.contains(uid)) {
            throw new IllegalArgumentException("User is already followed by this uid!");
        }
        followers.add(uid);
    }

    /**
     * removes a follower from the FollowingBook's follower list
     * @param uid
     *      the uid of the follower to be removed
     */
    public void removeFollower(String uid) {
        if (followers.contains(uid)) {
            followers.remove(uid);
        } else {
            throw new IllegalArgumentException("No such follower in this user's followers!");
        }
    }

    /**
     * queries the database for latest MoodEvents and places them in a Map where the key is the uid
     * of a user
     * @return
     *      Map with keys = uid and values = Latest Mood Event
     */
    public ArrayList<MoodEvent> getRecentMoodEvents() {
        ArrayList<MoodEvent> recentEvents = new ArrayList<MoodEvent>();
        DatabaseBestie db = new DatabaseBestie();
        Map<String, MoodEvent> uidToMoodEvent = new HashMap<>();
        for (String followingUid : following) {
            db.getLatestMoodEvent(followingUid, latestEvent -> {
                uidToMoodEvent.put(followingUid, latestEvent);
                recentEvents.add(latestEvent);
            });
        }
        return recentEvents;
    }

    // Filter mood events
    //public List<MoodEvent> filterMoodEventsByDate(String date) {
    //    return getRecentMoodEvents().stream()
    //            .filter(event -> MoodEvent.getPostDate().equals(date))
    //            .collect(Collectors.toList());
    //}

    //public List<MoodEvent> filterMoodEventsByMoodType(String moodType) {
    //    return getRecentMoodEvents().stream()
    //            .filter(event -> MoodEvent.getMood().getEmotion().equals(moodType))
    //            .collect(Collectors.toList());
    //}

    //public List<MoodEvent> filterMoodEventsByKeyword(String keyword) {
    //    return getRecentMoodEvents().stream()
    //            .filter(event -> MoodEvent.getSituation().contains(keyword))
    //            .collect(Collectors.toList());
    //}

    // Getters
    public int getFollowerCount() {
        return followers.size();
    }

    public int getFollowingCount() {
        return following.size();
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    //public List<FollowRequest> getFollowRequests() {
    //    return new ArrayList<>(followRequests);
    //}

    // setters
    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    //private Profile getOwnerProfile() {
    //    // Implement logic to return the profile owning this FollowingBook
    //    return null;
    //}
}
