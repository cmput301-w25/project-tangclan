package com.example.tangclan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FollowingBook {
    private List<Profile> following;
    private List<Profile> followers;
    private List<FollowRequest> followRequests;

    public FollowingBook() {
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.followRequests = new ArrayList<>();
    }

    // Handle follow requests
    public void addFollowRequest(Profile sender) {
        followRequests.add(new FollowRequest(sender));
    }

    public void acceptFollowRequest(Profile sender) {
        followRequests.removeIf(request -> request.getSender().equals(sender));
        followers.add(sender);
    }

    public void rejectFollowRequest(Profile sender) {
        followRequests.removeIf(request -> request.getSender().equals(sender));
    }

    // Manage following relationships
    public void follow(Profile profile) {
        if (!following.contains(profile)) {
            following.add(profile);
            profile.getFollowingBook().addFollower(this);
        }
    }

    public void unfollow(Profile profile) {
        following.remove(profile);
        profile.getFollowingBook().removeFollower(this);
    }

    public void addFollower(FollowingBook followerBook) {
        Profile follower = followerBook.getOwnerProfile();
        if (!followers.contains(follower)) {
            followers.add(follower);
        }
    }

    public void removeFollower(FollowingBook followerBook) {
        followers.remove(followerBook.getOwnerProfile());
    }

    // Fetch recent mood events from followed users
    public List<MoodEvent> getRecentMoodEvents() {
        return following.stream()
                .flatMap(profile -> MoodEventBook.getMoodEvent().getRecentEvents().stream())
                .collect(Collectors.toList());
    }

    // Filter mood events
    public List<MoodEvent> filterMoodEventsByDate(String date) {
        return getRecentMoodEvents().stream()
                .filter(event -> MoodEvent.getPostDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<MoodEvent> filterMoodEventsByMoodType(String moodType) {
        return getRecentMoodEvents().stream()
                .filter(event -> MoodEvent.getMood().equals(moodType))
                .collect(Collectors.toList());
    }

    public List<MoodEvent> filterMoodEventsByKeyword(String keyword) {
        return getRecentMoodEvents().stream()
                .filter(event -> MoodEvent.getTextExplanation().contains(keyword))
                .collect(Collectors.toList());
    }

    // Getters
    public int getFollowerCount() {
        return followers.size();
    }

    public int getFollowingCount() {
        return following.size();
    }

    public List<Profile> getFollowing() {
        return new ArrayList<>(following);
    }

    public List<Profile> getFollowers() {
        return new ArrayList<>(followers);
    }

    public List<FollowRequest> getFollowRequests() {
        return new ArrayList<>(followRequests);
    }

    private Profile getOwnerProfile() {
        // Implement logic to return the profile owning this FollowingBook
        return null;
    }
}
