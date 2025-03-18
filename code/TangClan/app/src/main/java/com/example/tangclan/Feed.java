package com.example.tangclan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Feed class which facilitates sorting and loading the feed
 * USER STORIES:
 *      US 01.04.01
 *          01.04.01b
 */
public class Feed {

    private FollowingBook followingBook;
    private MoodEventBook moodEventBook;
    private List<MoodEvent> feedEvents;

    /**
     *  Constructor for the feed class
     * @param followingBook
     *      the following book of the current user
     * @param moodEventBook
     *      moodEventBook of the current user
     */
    public Feed(FollowingBook followingBook, MoodEventBook moodEventBook) {
        this.followingBook = followingBook;
        this.moodEventBook = moodEventBook;
        this.feedEvents = new ArrayList<>();
    }

    /**
     * Adds all feed events to a list, and sorts by date and time
     */
    public void loadFeed() {
        feedEvents.clear();
        feedEvents.addAll(followingBook.getRecentMoodEvents(new DatabaseBestie()).values());
        feedEvents.addAll(moodEventBook.getAllMoodEvents());
        sortFeedByDateTime();
    }

    /**
     * Sorts the feedEvents list by Date and Time
     */
    public void sortFeedByDateTime() {
        feedEvents.sort(Comparator.comparing(MoodEvent::getPostDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(MoodEvent::getPostTime, Comparator.nullsLast(Comparator.reverseOrder())));
    }

    /**
     * filters the feed events by a specific date
     * @param date
     *      a date used in the query
     * @return
     *      all Mood Events on the given date
     */
    public List<MoodEvent> filterByDate(LocalDate date) {
        return feedEvents.stream()
                .filter(event -> event.getPostDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<MoodEvent> filterByMoodType(String moodType) {
        return feedEvents.stream()
                .filter(event -> event.getMoodEmotionalState().equalsIgnoreCase(moodType))
                .collect(Collectors.toList());
    }

    /**
     * Filters MoodEvents whose situations match the keyword
     * @param keywords
     *      The keywords to match
     * @return
     *      All Mood Events whose situations match the keyword
     */
    public List<MoodEvent> filterBySituationKeywords(List<String> keywords) {
        return feedEvents.stream()
                .filter(event -> event.getReason().isPresent() && keywords.stream()
                        .anyMatch(keyword -> event.getReason().get().toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * Displays the feed
     */
    public void displayFeed() {
        feedEvents.forEach(System.out::println);
    }

    /**
     * Displays the feed on the map, according to geolocation
     */
    public void displayFeedOnMap() {
        feedEvents.stream()
                .filter(MoodEvent::hasGeolocation)
                .forEach(event -> System.out.println("Displaying on map: " + event));
    }

    /**
     * Getter for the feed events
     * @return
     *      All of the MoodEvents in feedEvents
     */
    public List<MoodEvent> getFeedEvents() {
        return feedEvents;
    }

    /**
     * Getter for FollowingBook
     * @return
     *      The FollowingBook object
     */
    public FollowingBook getFollowingBook() {
        return followingBook;
    }
}
