package com.example.tangclan;

import static java.lang.Integer.parseInt;

import java.io.Serializable;
import java.sql.Blob;

// This has info pertaining to a user that they may edit through 'Edit Profile'
public class Profile extends User implements Serializable {//NOTE: EXTENDS MoodEventBook and FollowingBook
    private String displayName;
    private String username;
    private String password;
    private String email;
    private String age;

    private Blob profilePic;

    public Profile(String displayName, String username, String password, String email, Blob photo){
        super();
        this.username=username;
        this.password=password;
        this.email=email;
        this.profilePic = photo;
    }

    public Profile(String displayName, String username, String password, String email, String age, Blob photo){
        super();
        this.username=username;
        this.password=password;
        this.email=email;
        this.age=age;
        this.profilePic = photo;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setUsername(String username) {
        //Limit length of username to 15
        if ((username.length()>15) || (username.length()<=0)){
            throw new IllegalArgumentException();
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }


    /**
     * Helper function:
     * Checks if password contains at least one uppercase,lowercase,number,special character and between 1 to 20 characters
     * @param password
     * @return true/false
     */
    public static boolean validPassword(String password){
        return (password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"));
    }

    public void setPassword(String password) {

        if (!validPassword(password)){
            throw new IllegalArgumentException();
        }
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        if(parseInt(age) <18){
            throw new IllegalArgumentException();
        }
        this.age = age;//may need to do conversion to string before setting
    }
    //methods for filter(already presented inside of new feed class with filter created?), methods are inherited


    //TODO:
    //On CRC cards "Manages Permissions for followers" Referring to this user story?: "As a participant, I want to grant another participant permission to follow my most recent moodevent
    //Idea:

    //ASK:Mood event history? is that supposed to be stored in MoodEventBook(yes it is)
}
