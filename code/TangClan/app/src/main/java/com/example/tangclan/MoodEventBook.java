package com.example.tangclan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Date;


public class MoodEventBook {

    private List<MoodEvent> moodEvents; //list stores all mood event objects


    //constructor
    public MoodEventBook(){
        this.moodEvents = new ArrayList<>(); //initalizes empty array list of mood events
    }

    public void addMoodEvent(MoodEvent event){ //adds new MoodEvent to the list
        moodEvents.add(event);
    }

    public void deleteMoodEvent(MoodEvent event){ //removes mood event from the list
        moodEvents.remove(event);
    }

    public static MoodEvent getMoodEvent(int index){ //obtains moodevent by its index in the list
        if (index>=0 && index < moodEvents.size()){
            return moodEvents.get(index);
        }
        return null;
    }

    public int getMoodEventCount(){ //count the MoodEvents in the list
        return moodEvents.size();
    }

    public void sortMoodEvents(){ //sorts mood events in descending order
        moodEvents.sort(Comparator.comparing(MoodEvent::getPostDate).reversed());  //https://www.geeksforgeeks.org/comparator-reversed-method-in-java-with-examples/
    }

    public List<MoodEvent> filterByDate(LocalDate date){ //filter events by date
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event: moodEvents){
            if (event.getPostDate().equals(date)){
                result.add(event);
            }
        }
        return result;
    }

    public List<MoodEvent> filterByMoodType(String moodType){ //filter by specific type of mood
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event: moodEvents){
            if (event.getMoodEmotionalState().equalsIgnoreCase(moodType)){  //https://www.geeksforgeeks.org/java-string-equalsignorecase-method-with-examples/
                result.add(event);
            }
        }
        return result;
    }

    public List<MoodEvent> filterByExplanationKeywords(List<String> keywords){ //filter by keywords that user inputs
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents){
            // we check for each mood event if the situation is present
            if (event.getSituation().isPresent()) {

                // get the mood event situation
                String explanation = event.getSituation().get();

                // parse the situation and match the first keyword
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

    public void displayMoodEvents(){ //displays the moodevents to console
        for (MoodEvent event : moodEvents){
            System.out.println(event);
        }
    }

    public void displayMoodEventsOnMap(){ //displays events with geolocation
        for (MoodEvent event: moodEvents){
            if (event.hasGeolocation()){
                System.out.println("Displaying on map: " + event);
            }
        }
    }






}

//https://developer.android.com/guide/topics/resources/drawable-resource#Shape