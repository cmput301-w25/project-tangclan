package com.example.tangclan;

import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//part of US 01.01.01, US 01.04.01, and US 01.06.01
/**
 * MoodEventBook manages a collection of MoodEvent objects.
 * It supports adding, removing, sorting, and filtering mood events.
 */
public class MoodEventBook {
    private ArrayList<MoodEvent> moodEvents;

    // Constructor
    public MoodEventBook() {

        this.moodEvents = new ArrayList<>();

        // Add a listener for when data changes
        DatabaseBestie db = new DatabaseBestie();
        sortMoodEvents();
    }

    /**
     * Safely replaces the mood events list with a new copy.
     *
     * @param moodEvents The new list of mood events.
     */
    public void setMoodEvents(ArrayList<MoodEvent> moodEvents) {
        this.moodEvents = new ArrayList<>(moodEvents);
        sortMoodEvents(); // Ensure proper sorting
    }
    /**
     * Adds a new mood event to the list.
     *
     * @param event The mood event to be added.
     */
    public void addMoodEvent(MoodEvent event) {
        moodEvents.add(0, event); // Insert at beginning to maintain reverse chronological order
    }

    /**
     * Removes a mood event from the list.
     *
     * @param event The mood event to be removed.
     */
    public void deleteMoodEvent(MoodEvent event) {
        moodEvents.remove(event);
    }

    public void updateMoodEvents() {
        DatabaseBestie db = new DatabaseBestie();
        String id, month;
        if (!moodEvents.isEmpty()) {
            for (MoodEvent event: moodEvents) {
                id = event.getMid();
                month = event.userFormattedDate().substring(3);
                db.getMoodEventByMid(id, month, (updatedEvent, emot) -> {
                    event.setMood(emot);
                    event.setCollaborators(updatedEvent.getCollaborators().orElse(new ArrayList<>()));
                    event.setReason(updatedEvent.getReason().orElse(""));
                    event.setImage(updatedEvent.getImage());
                });
            }
        }
    }

    /**
     * Retrieves a mood event at a given index.
     *
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
     *
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
     *
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
     *
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
     *
     * @param keywords The list of keywords to search for.
     * @return A list of mood events containing any of the keywords in the explanation.
     */
    public List<MoodEvent> filterByExplanationKeywords(List<String> keywords) {
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents) {

            String explanation = event.getReason().get();
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
    /*public void displayMoodEventsOnMap() {
        for (MoodEvent event : moodEvents) {
            if (event.hasGeolocation()) {
                System.out.println("Displaying on map: " + event);
            }
        }
    }*/

    /**
     * Grabs all of the Mood Event objects as a List stored within the MoodEventBook
     *
     * @return A list of all MoodEvents
     */
    public ArrayList<MoodEvent> getAllMoodEvents() {
        // Return a copy of the list (already in reverse chronological order)
        return new ArrayList<>(moodEvents);
    }

    public List<MoodEvent> getMoodEventList() {
        // Return a copy of the list (already in reverse chronological order)
        return new ArrayList<>(moodEvents);
    }
}


