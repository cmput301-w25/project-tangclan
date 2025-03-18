package com.example.tangclan;

import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class WizVIew extends ViewModel {
    private String emotionalState;
    private ArrayList<String> triggers = new ArrayList<>();
    private ArrayList<String> socialSituation;
    private Bitmap optionalPicture;
    private String reason;

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

    public ArrayList<String> getSocialSituation() {
        return socialSituation;
    }
    public void setSocialSituation(ArrayList<String> socialSituation) {
        this.socialSituation = socialSituation;
    }
    public Bitmap getOptionalPicture() {
        return optionalPicture;
    }

    public void setOptionalPicture(Bitmap optionalPicture) {
        this.optionalPicture = optionalPicture;
    }

    public void setReason(String reason){
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

