package com.example.tangclan;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class WizVIew extends ViewModel {
    private String emotionalState;
    private ArrayList<String> triggers = new ArrayList<>();
    private String socialSituation;

    public String getEmotionalState() {
        return emotionalState;
    }
    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    public ArrayList<String> getTriggers() {
        return triggers;
    }
    public void addTrigger(String trigger) {
        this.triggers.add(trigger);
    }

    public String getSocialSituation() {
        return socialSituation;
    }
    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
    }
}

