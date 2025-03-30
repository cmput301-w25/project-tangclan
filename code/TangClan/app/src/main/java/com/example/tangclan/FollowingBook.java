package com.example.tangclan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the followers, following, and any outstanding follow requests of the session user
 */
public class FollowingBook {
    private ArrayList<String> following = new ArrayList<>();
    private ArrayList<String> followers = new ArrayList<>();
    private ArrayList<String> followRequests = new ArrayList<>();

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

    public void getRecentMoodEvents(DatabaseBestie db, DatabaseBestie.MoodEventsCallback callback) {
        ArrayList<MoodEvent> allEvents = new ArrayList<>();
        AtomicInteger remaining = new AtomicInteger(following.size());

        if (following.isEmpty()) {
            callback.onMoodEventsRetrieved(new ArrayList<>());
            return;
        }

        for (String followingUid : following) {
            db.getAllMoodEvents(followingUid, events -> {
                synchronized (allEvents) {
                    allEvents.addAll(events);

                    if (remaining.decrementAndGet() == 0) {
                        // Sort all events by date/time
                        allEvents.sort(Comparator
                                .comparing(MoodEvent::getPostDate, Comparator.reverseOrder())
                                .thenComparing(MoodEvent::getPostTime, Comparator.reverseOrder()));

                        // Take only the 3 most recent
                        ArrayList<MoodEvent> recentEvents = new ArrayList<>();
                        int limit = Math.min(3, allEvents.size());
                        for (int i = 0; i < limit; i++) {
                            recentEvents.add(allEvents.get(i));
                        }
                        callback.onMoodEventsRetrieved(recentEvents);
                    }
                }
            });
        }
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