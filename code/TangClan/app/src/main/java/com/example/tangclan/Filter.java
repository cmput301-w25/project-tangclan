package com.example.tangclan;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Filter {

    public static List<MoodEvent> filterByTimeRange(List<MoodEvent> events, LocalDate startDate, LocalDate endDate) {
        return events.stream()
                .filter(event -> !event.getPostDate().isBefore(startDate) && !event.getPostDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public static List<MoodEvent> filterByEmotionalState(List<MoodEvent> events, String moodType) {
        return events.stream()
                .filter(event -> event.getMoodEmotionalState().equalsIgnoreCase(moodType))
                .collect(Collectors.toList());
    }

    public static List<MoodEvent> filterByKeywords(List<MoodEvent> events, List<String> keywords) {
        return events.stream()
                .filter(event -> event.getReason().isPresent() &&
                        keywords.stream().anyMatch(keyword -> event.getReason().get().toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }

    public static List<MoodEvent> filterByGeolocation(List<MoodEvent> events) {
        return events.stream()
                .filter(MoodEvent::hasGeolocation)
                .collect(Collectors.toList());
    }
}