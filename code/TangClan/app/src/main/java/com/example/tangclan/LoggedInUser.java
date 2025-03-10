package com.example.tangclan;

import javax.inject.Singleton;

public class LoggedInUser extends Profile {
    private static LoggedInUser instance = null;
    protected LoggedInUser() {
        super();
    }
    public static LoggedInUser getInstance() {
        if (instance == null) {
            instance = new LoggedInUser();
        }
        return instance;
    }
}
