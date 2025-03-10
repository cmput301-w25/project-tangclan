package com.example.tangclan;

import javax.inject.Singleton;

/**
 * US 3.01.01
 * This stores an instance of the current logged in user
 */
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
