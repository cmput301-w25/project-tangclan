package com.example.tangclan;

import javax.inject.Singleton;

/**
 * US 3.01.01
 * This stores an instance of the current logged-in user
 */
public class LoggedInUser extends Profile {
    private static LoggedInUser instance = null;

    // Private constructor to prevent external instantiation
    private LoggedInUser() {
        super();
    }

    /**
     * Get the Singleton instance of LoggedInUser
     * @return The Singleton instance
     */
    public static LoggedInUser getInstance() {
        if (instance == null) {
            synchronized (LoggedInUser.class) { // Thread-safe initialization
                if (instance == null) {
                    instance = new LoggedInUser();
                }
            }
        }
        return instance;
    }

    /**
     * Set the user's profile data
     * @param profile The profile data to set
     */
    public void setProfileData(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }

        // Copy profile data to the LoggedInUser instance
        this.setDisplayName(profile.getDisplayName());
        this.setUsername(profile.getUsername());
        this.setPassword(profile.getPassword());
        this.setEmail(profile.getEmail());
        this.setAge(profile.getAge());
       
        // Set other profile data as needed
    }



    /**
     * Clear the user's profile data (e.g., on logout)
     */


    /**
     * Reset the Singleton instance (for testing or special cases)
     */
    public static void resetInstance() {
        instance = null;
    }
}