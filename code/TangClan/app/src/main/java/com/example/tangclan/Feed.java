package com.example.tangclan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Feed {

    private FollowingBook followingBook;
    private List<MoodEvent> feedEvents;

    public Feed(FollowingBook followingBook){
        this.followingBook=followingBook;
        this.feedEvents=new ArrayList<>();
    }

    public void loadFeed(){
        feedEvents.clear();
        for (User user: followingBook.getFollowedUsers()){
            feedEvents.addAll(user.getMoodEvents());
        }
        sortFeedByDate();
    }

    public void sortFeedByDate(){
        feedEvents.sort(Comparator.comparing(MoodEvent::getPostDate).reversed());

    }

    public List<MoodEvent> filterByMoodType(String moodType){
        List<MoodEvent> filteredList = new ArrayList<>();
        for (MoodEvent event:feedEvents){
            if (event.getMoodType().equalsIgnoreCase(moodType)){
                filteredList.add(event);
            }
        }
        return filteredList;
    }

    public List<MoodEvent> filterByExplanationKeywords(List<String> keywords){
        List<MoodEvent> filteredList = new ArrayList<>();
        for (MoodEvent event:feedEvents){
            String explanation = event.getExplanation();
            for (String keyword : keywords){
                if (explanation != null && explanation.toLowerCase().contains(keyword.toLowerCase())){
                    filteredList.add(event);
                    break;
                }
            }
        }
        return filteredList;
    }

    public void displayFeed(){
        for (MoodEvent event : feedEvents){
            System.out.println(event);
        }
    }






}
