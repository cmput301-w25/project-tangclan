package com.example.tangclan;

import java.time.LocalDate;
import java.util.ArrayList;
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
                .filter(event -> {
                    // if (event.getSituation().isPresent()) {
                    if (event.getSituation() != null) {
                        String explanation = event.getSituation().get();

                        return keywords.stream().anyMatch(keyword -> explanation.toLowerCase().contains(keyword.toLowerCase()));
                    }

                    return false;
                    //String explanation = event.getSituation();
                    //if (explanation != null) {
                    //    return keywords.stream().anyMatch(keyword -> explanation.toLowerCase().contains(keyword.toLowerCase()));
                    //}
                    //return false;
                })
                .collect(Collectors.toList());
    }

    public static List<MoodEvent> filterByGeolocation(List<MoodEvent> events) {
        return events.stream()
                .filter(MoodEvent::hasGeolocation)
                .collect(Collectors.toList());
    }


}

//https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html

