package com.example.tangclan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Feed {

    private FollowingBook followingBook;
    private MoodEventBook moodEventBook;
    private List<MoodEvent> feedEvents;

    public Feed(FollowingBook followingBook, MoodEventBook moodEventBook) {
        this.followingBook = followingBook;
        this.moodEventBook = moodEventBook;
        this.feedEvents = new ArrayList<>();
    }

    public void loadFeed() {
        feedEvents.clear();
        feedEvents.addAll(followingBook.getRecentMoodEvents());
        feedEvents.addAll(moodEventBook.getAllMoodEvents());
        sortFeedByDateTime();
    }

    public void sortFeedByDateTime() {
        feedEvents.sort(Comparator.comparing(MoodEvent::getPostDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(MoodEvent::getPostTime, Comparator.nullsLast(Comparator.reverseOrder())));
    }

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

    public List<MoodEvent> filterByTriggers(List<String> triggers) {
        return feedEvents.stream()
                .filter(event -> event.getTriggers() != null && event.getTriggers().stream().anyMatch(triggers::contains))
                .collect(Collectors.toList());
    }

    public List<MoodEvent> filterBySituationKeywords(List<String> keywords) {
        return feedEvents.stream()
                .filter(event -> event.getSituation() != null && keywords.stream()
                        .anyMatch(keyword -> event.getSituation().toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }

    public void displayFeed() {
        feedEvents.forEach(System.out::println);
    }

    public void displayFeedOnMap() {
        feedEvents.stream()
                .filter(MoodEvent::hasGeolocation)
                .forEach(event -> System.out.println("Displaying on map: " + event));
    }

    public List<MoodEvent> getFeedEvents() {
        return feedEvents;
    }
}
