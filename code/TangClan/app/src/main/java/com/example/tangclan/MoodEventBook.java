package com.example.tangclan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;

public class MoodEventBook {

    private List<MoodEvent> moodEvents;

    //constructor
    public MoodEventBook(){
        this.moodEvents = new ArrayList<>();
    }

    public void addMoodEvent(MoodEvent event){
        moodEvents.add(event);
    }

    public void deleteMoodEvent(MoodEvent event){
        moodEvents.remove(event);
    }

    public MoodEvent getMoodEvent(int index){
        if (index>=0 && index < moodEvents.size()){
            return moodEvents.get(index);
        }
        return null;
    }

    public int getMoodEventCount(){
        return moodEvents.size();
    }

    public void sortMoodEvents(){
        moodEvents.sort(Comparator.comparing(MoodEvent::getPostDate).reversed());
    }

    public List<MoodEvent> filterByDate(LocalDate date){
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event: moodEvents){
            if (event.getPostDate().equals(date)){
                result.add(event);
            }
        }
        return result;
    }

    public List<MoodEvent> filterByMoodType(String moodType){
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event: moodEvents){
            if (event.getMoodEmotionalState().equalsIgnoreCase(moodType)){
                result.add(event);
            }
        }
        return result;
    }

    public List<MoodEvent> filterByExplanationKeywords(List<String> keywords){
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents){
            String explanation = event.getSituation();
            for (String keyword : keywords){
                if (explanation != null && explanation.toLowerCase().contains(keyword.toLowerCase())){
                    result.add(event);
                    break;
                }
            }
        }
        return result;
    }


    public List<MoodEvent> filterByTriggers(String trigger){
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents){
            ArrayList<String> eventTriggers = event.getTriggers();
            if (eventTriggers != null) {
                for (String eventTrigger : eventTriggers){
                    if (eventTrigger.toLowerCase().contains(trigger.toLowerCase())){
                        result.add(event);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public void displayMoodEvents(){
        for (MoodEvent event : moodEvents){
            System.out.println(event);
        }
    }


    public void displayMoodEventsOnMap(){
        for (MoodEvent event: moodEvents){

            System.out.println("Displaying on map: " + event);
        }
    }


    public List<MoodEvent> getMoodEventsWithImages() {
        List<MoodEvent> result = new ArrayList<>();
        for (MoodEvent event : moodEvents) {
            if (event.getImageUri() != null) {
                result.add(event);
            }
        }
        return result;
    }


    public MoodEvent findMoodEventById(int id) {
        for (MoodEvent event : moodEvents) {
            if (event.getMid() == id) {
                return event;
            }
        }
        return null;
    }
}