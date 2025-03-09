package com.example.tangclan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MoodEventBook {
    private ArrayList<MoodEvent> moodEvents;

    // Constructor
    public MoodEventBook() {
        this.moodEvents = new ArrayList<>();
    }

    public void setMoodEvents(ArrayList<MoodEvent> moodEvents) {
        this.moodEvents = moodEvents;
    }

    public void addMoodEvent(MoodEvent event) {
        moodEvents.add(event);
    }

    public void deleteMoodEvent(MoodEvent event) {
        moodEvents.remove(event);
    }

    public MoodEvent getMoodEvent(int index) {
        if (index >= 0 && index < moodEvents.size()) {
            return moodEvents.get(index);
        }
        return null;
    }

    public int getMoodEventCount() {
        return moodEvents.size();
    }

    public void sortMoodEvents() {
        moodEvents.sort(Comparator.comparing(MoodEvent::getPostDate).reversed()
                .thenComparing(MoodEvent::getPostTime).reversed());
    }

    public List<MoodEvent> filterByDate(LocalDate date) {
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents) {
            if (event.getPostDate().equals(date)) {
                result.add(event);
            }
        }
        return result;
    }

    public List<MoodEvent> filterByMoodType(String moodType) {
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents) {
            if (event.getMoodEmotionalState().equalsIgnoreCase(moodType)) {
                result.add(event);
            }
        }
        return result;
    }

    public List<MoodEvent> filterByExplanationKeywords(List<String> keywords) {
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents) {
            String explanation = event.getSituation();
            if (explanation != null) {
                for (String keyword : keywords) {
                    if (explanation.toLowerCase().contains(keyword.toLowerCase())) {
                        result.add(event);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public void displayMoodEvents() {
        for (MoodEvent event : moodEvents) {
            System.out.println(event);
        }
    }

    public void displayMoodEventsOnMap() {
        for (MoodEvent event : moodEvents) {
            if (event.hasGeolocation()) {
                System.out.println("Displaying on map: " + event);
            }
        }
    }
}
