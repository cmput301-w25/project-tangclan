package com.example.tangclan;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the followers, following, and any outstanding follow requests of the session user
 */
public class FollowingBook {
    private ArrayList<String> following;
    private ArrayList<String> followers;
    private ArrayList<String> followRequests;

    /**
     * Constructor for the FollowingBook object
     */
    public FollowingBook() {
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.followRequests = new ArrayList<>();
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
     * Adds a user to the following list
     * @param uid
     *      the uid of the user to follow
     */
    public void addFollowing(String uid) {
        if (!following.contains(uid)) {
            following.add(uid);
        }
    }

    /**
     * queries the database for latest MoodEvents and places them in a Map where the key is the uid
     * of a user. This is a temporary implementation that creates mock data until the following
     * system is fully implemented.
     *
     * @param db The database connector
     * @return Map with keys = uid and values = Latest Mood Event
     */
    public Map<String, MoodEvent> getRecentMoodEvents(DatabaseBestie db) {
        Map<String, MoodEvent> uidToMoodEvent = new HashMap<>();

        // For testing purposes until following system is implemented
        if (following.isEmpty()) {
            // Create some dummy events from "followed" users
            MoodEvent event1 = new MoodEvent("happy");
            event1.setMid("1000");
            event1.setReason("i went to school today");
            event1.setPostDate("2025-03-18"); // Use "yyyy-MM-dd" format
            event1.setPostTime("12:29:39.673202"); // Use "HH:mm:ss.SSSSSS" format
            event1.setCollaborators(new ArrayList<>(List.of("With friends")));
            uidToMoodEvent.put("user1", event1);

            MoodEvent event2 = new MoodEvent("sad");
            event2.setMid("2000");
            event2.setPostDate("2025-03-17"); // Use "yyyy-MM-dd" format
            event2.setReason("i didnt go to school today. i lost my 5% participation mark");
            event2.setPostTime("14:45:22.123456"); // Use "HH:mm:ss.SSSSSS" format
            event2.setCollaborators(new ArrayList<>(List.of("At home")));
            uidToMoodEvent.put("user2", event2);

            MoodEvent event3 = new MoodEvent("anxious");
            event1.setMid("3000");
            event3.setPostDate("2025-03-18"); // Use "yyyy-MM-dd" format
            event3.setReason("i am anxiously waiting for my 429 midterm marks. it was curved. maybe i got like a 30%");
            event3.setPostTime("18:30:15.987654"); // Use "HH:mm:ss.SSSSSS" format
            event3.setCollaborators(new ArrayList<>(List.of("Party time")));
            uidToMoodEvent.put("user3", event3);

            MoodEvent event4 = new MoodEvent("calm");
            event1.setMid("4000");
            event4.setPostDate("2025-03-16"); // Use "yyyy-MM-dd" format
            event4.setPostTime("09:15:45.456789"); // Use "HH:mm:ss.SSSSSS" format
            event4.setCollaborators(new ArrayList<>(List.of("Reading")));
            uidToMoodEvent.put("user4", event4);

            return uidToMoodEvent;
        }

        // Real implementation for when following is set up
        for (String followingUid : following) {
            db.getLatestMoodEvent(followingUid, (latestEvent, emot) -> {
                uidToMoodEvent.put(followingUid, latestEvent);
            });
        }
        return uidToMoodEvent;
    }

    // Add a follow request
    public void addRequestingFollower(String uid) {
        if (!followRequests.contains(uid)) {
            followRequests.add(uid);
        }
    }
    // Accept a follow request
    public void acceptFollowRequest(String uid) {
        if (followRequests.contains(uid)) {
            followRequests.remove(uid);
            followers.add(uid);
        } else {
            throw new IllegalArgumentException("No follow request from this user!");
        }
    }

    // Decline a follow request
    public void declineFollowRequest(String uid) {
        if (followRequests.contains(uid)) {
            followRequests.remove(uid);
        } else {
            throw new IllegalArgumentException("No follow request from this user!");
        }
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

    public void setFollowRequests(ArrayList<String> followRequests) {
        this.followRequests = followRequests;
    }

    public ArrayList<String> getFollowRequests() {
        return followRequests;
    }

    //private Profile getOwnerProfile() {
    //    // Implement logic to return the profile owning this FollowingBook
    //    return null;
    //}
}