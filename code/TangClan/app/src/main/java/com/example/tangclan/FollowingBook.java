package com.example.tangclan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the followers, following, and any outstanding follow requests of the session user
 */
public class FollowingBook {
    private ArrayList<String> following = new ArrayList<>();
    private ArrayList<String> followers = new ArrayList<>();
    private ArrayList<String> followRequests = new ArrayList<>();


    private ArrayList<String> myFollowRequests = new ArrayList<>();

    private Map<String, String> UIDtoFollowerUsername = new HashMap<>();
    private Map<String, String> UIDtoFollowingUsername = new HashMap<>();

    public FollowingBook() {
        
    }


    public void addFollower(String uid) {
        if (!followers.contains(uid)) {
            followers.add(uid);
        }
    }

    public void removeFollower(String uid) {
        followers.remove(uid);
    }


    public void addFollowing(String uid) {
        if (!following.contains(uid)) {
            following.add(uid);
        }
    }


    public void addRequestingFollower(String uid) {
        if (!followRequests.contains(uid)) {
            followRequests.add(uid);
        }
    }

    public void acceptFollowRequest(String uid) {
        if (followRequests.remove(uid)) {
            followers.add(uid);
        }
    }

    public void declineFollowRequest(String uid) {
        followRequests.remove(uid);
    }

    // Getters
    public ArrayList<String> getFollowing() {
        return following;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public ArrayList<String> getFollowRequests() {
        return followRequests;
    }

    public ArrayList<String> getMyFollowRequests() {
        return myFollowRequests;
    }

    public int getFollowerCount() {
        return followers.size();
    }

    public int getFollowingCount() {
        return following.size();
    }

    public void setFollowing(Object following) {
        this.following = convertToArrayList(following);
    }

    public void setFollowers(Object followers) {
        this.followers = convertToArrayList(followers);
    }

    public void setFollowRequests(Object followRequests) {
        this.followRequests = convertToArrayList(followRequests);
    }



    public void setMyFollowRequests(ArrayList<String> myFollowRequests) {
        this.myFollowRequests = myFollowRequests;
    }

    public void addMyRequest(String targetUid) {
        myFollowRequests.add(targetUid);
    }


   public void addEntryToFollowerMap(String uid, String username) {
        this.UIDtoFollowerUsername.put(uid, username);
   }

   public void addEntryToFollowingMap(String uid, String username) {
        this.UIDtoFollowingUsername.put(uid, username);
   }


    private ArrayList<String> convertToArrayList(Object input) {
        ArrayList<String> result = new ArrayList<>();
        if (input instanceof List) {
            for (Object item : (List<?>) input) {
                if (item != null) {
                    result.add(item.toString());
                }
            }
        } else if (input != null) {
            result.add(input.toString());
        }
        return result;
    }

    public Map<String, MoodEvent> getRecentMoodEvents(DatabaseBestie db) {
        Map<String, MoodEvent> uidToMoodEvent = new HashMap<>();

        if (following.isEmpty()) {
            // Mock data
            addMockMoodEvents(uidToMoodEvent);
        } else {
            // Real implementation
            for (String followingUid : following) {
                db.getLatestMoodEvent(followingUid, (latestEvent, emot) -> {
                    uidToMoodEvent.put(followingUid, latestEvent);
                });
            }
        }
        return uidToMoodEvent;
    }

    /**
     * Gets the usernames of all user followers
     * @return
     *      An ArrayList of all follower usernames
     */
    public ArrayList<String> getFollowerUsernames() {
        return new ArrayList<>(this.UIDtoFollowerUsername.values());
    }

    /**
     * Gets the usernames of all following
     * @return
     *      An ArrayList of all following usernames
     */
    public ArrayList<String> getFollowingUsernames() {
        return new ArrayList<>(this.UIDtoFollowingUsername.values());
    }

    /**
     * Resets the followed and following data
     */

    public void resetFollowingUsernames() {
        this.UIDtoFollowingUsername = new HashMap<>();
    }

    public void resetFollowerUsernames() {
        this.UIDtoFollowerUsername = new HashMap<>();
    }


    /**
     Gets the username attached to a give uid
     * @return
     *      A String that is the username associated with a uid
     */
    public String getUIDtoFollowingUsername(String uid) {
        Map<String, String> moodmap = UIDtoFollowingUsername;
        return moodmap.get(uid);
    }

    public String getUIDtoFollowerUsername(String uid) {
        Map<String, String> moodmap = UIDtoFollowerUsername;
        return moodmap.get(uid);
    }



    private void addMockMoodEvents(Map<String, MoodEvent> uidToMoodEvent) {
        MoodEvent event1 = createMockEvent("happy", "1000", "2025-03-18", "12:29:39.673202",
                "i went to school today", "With friends");
        uidToMoodEvent.put("user1", event1);

        MoodEvent event2 = createMockEvent("sad", "2000", "2025-03-17", "14:45:22.123456",
                "i didnt go to school today", "At home");
        uidToMoodEvent.put("user2", event2);

        MoodEvent event3 = createMockEvent("anxious", "3000", "2025-03-18", "18:30:15.987654",
                "i am anxiously waiting for my 429 midterm marks", "Party time");
        uidToMoodEvent.put("user3", event3);

        MoodEvent event4 = createMockEvent("calm", "4000", "2025-03-16", "09:15:45.456789",
                "", "Reading");
        uidToMoodEvent.put("user4", event4);
    }

    private MoodEvent createMockEvent(String mood, String mid, String date, String time,
                                      String reason, String collaborator) {
        MoodEvent event = new MoodEvent(mood);
        event.setMid(mid);
        event.setPostDate(date);
        event.setPostTime(time);
        event.setReason(reason);
        event.setCollaborators(new ArrayList<>(List.of(collaborator)));
        return event;
    }
}