package com.example.tangclan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Date;


/**
 * MoodEventBook manages a collection of MoodEvent objects.
 * It supports adding, removing, sorting, and filtering mood events.
 */
public class MoodEventBook {
    private ArrayList<MoodEvent> moodEvents;
    //mooc event bookkkknnn
    // Constructor
    public MoodEventBook() {
        this.moodEvents = new ArrayList<>();
    }

    /**
     * Safely replaces the mood events list with a new copy.
     * @param moodEvents The new list of mood events.
     */
    public void setMoodEvents(ArrayList<MoodEvent> moodEvents) {
        this.moodEvents = new ArrayList<>(moodEvents); // Copy to prevent unintended modifications
    }

    /**
     * Adds a new mood event to the list.
     * @param event The mood event to be added.
     */
    public void addMoodEvent(MoodEvent event) {
        if (event != null) {
            moodEvents.add(event);
        }
    }

    /**
     * Removes a mood event from the list.
     * @param event The mood event to be removed.
     */
    public void deleteMoodEvent(MoodEvent event) {
        moodEvents.remove(event);
    }

    /**
     * Retrieves a mood event at a given index.
     * @param index The index of the mood event.
     * @return The mood event if valid, otherwise null.
     */
    public MoodEvent getMoodEvent(int index) {
        if (index >= 0 && index < moodEvents.size()) {
            return moodEvents.get(index);
        }
        return null;
    }

    /**
     * Returns the total number of mood events.
     * @return The count of mood events.
     */
    public int getMoodEventCount() {
        return moodEvents.size();
    }

    /**
     * Sorts the mood events in reverse chronological order based on date and time.
     */
    public void sortMoodEvents() {
        moodEvents.sort(Comparator
                .comparing(MoodEvent::getPostDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(MoodEvent::getPostTime, Comparator.nullsLast(Comparator.reverseOrder()))
        );
    }

    /**
     * Filters mood events by a specific date.
     * @param date The date to filter by.
     * @return A list of mood events that match the given date.
     */
    public List<MoodEvent> filterByDate(LocalDate date) {
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents) {
            if (event.getPostDate().equals(date)) {
                result.add(event);
            }
        }
        return result;
    }

    /**
     * Filters mood events by mood type.
     * @param moodType The mood type to filter by.
     * @return A list of mood events that match the given mood type.
     */
    public List<MoodEvent> filterByMoodType(String moodType) {
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents) {
            if (event.getMoodEmotionalState().equalsIgnoreCase(moodType)) {
                result.add(event);
            }
        }
        return result;
    }

    /**
     * Filters mood events that contain specific keywords in the explanation.
     * @param keywords The list of keywords to search for.
     * @return A list of mood events containing any of the keywords in the explanation.
     */
    public List<MoodEvent> filterByExplanationKeywords(List<String> keywords) {
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents) {
            String explanation = event.getSituation();
            if (explanation != null) {
                String lowerCaseExplanation = explanation.toLowerCase();
                for (String keyword : keywords) {
                    if (lowerCaseExplanation.contains(keyword.toLowerCase())) {
                        result.add(event);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Displays all mood events in the console.
     */
    public void displayMoodEvents() {
        for (MoodEvent event : moodEvents) {
            System.out.println(event);
        }
    }

    /**
     * Displays mood events that have geolocation data.
     * Placeholder for integrating with a mapping UI.
     */
    public void displayMoodEventsOnMap() {
        for (MoodEvent event : moodEvents) {
            if (event.hasGeolocation()) {
                System.out.println("Displaying on map: " + event);
            }
        }
    }

    /**
     * Grabs all of the Mood Event objects as a List stored within the MoodEventBook
     * @return
     *      A list of all MoodEvents
     */
    public List<MoodEvent> getAllMoodEvents() {
        return new ArrayList<>(moodEvents);
    }

}
