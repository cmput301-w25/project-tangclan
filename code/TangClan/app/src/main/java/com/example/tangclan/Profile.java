package com.example.tangclan;

import java.io.*;
public class Profile {//NOTE: EXTENDS MoodEventBook and FollowingBook
    private String username;
    private String password;
    private String email;
    private String age;//Note: Mentioned in meeting everything is stored as strings may need to change?????
    public Profile(String username, String password, String email,String age){
        this.username=username;
        this.password=password;
        this.email=email;
        this.age=age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        //Limit length of username to 15
        if ((username.length()>15) || (username.length()<=0)){
            throw new IllegalArgumentException();
        }

        //TODO:
        //Idea for "Storing Unique username"
        //Looping through the database check if any matching.

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
        final String SPECIAL_CHARACTERS = "!,#,$,%,^,&,*,|";
        boolean upCase = false;
        boolean loCase = false;
        boolean isDigit = false;
        boolean spChar = false;
        boolean isLength=false;
        if (password.matches(".+[A-Z].+")){
            upCase = true;
        }
        if (password.matches(".+[a-z].+")){
            loCase = true;
        }
        if (password.matches(".+[1-9].+")){
            isDigit = true;
        }
        if (SPECIAL_CHARACTERS.contains(password)){
            spChar = true;
        }
        if((password.length()<=20&&password.length()>0)){
            isLength=true;
        }

        return (upCase && loCase && isDigit && spChar && isLength);
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
        //TODO:
        //Verifying correct input(if needed)
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        if(Integer.parseInt(age)<18){
            throw new IllegalArgumentException();
        }
        this.age = age;//may need to do conversion to string before setting
    }
    //methods for filter(already presented inside of new feed class with filter created?), methods are inherited
    //On CRC cards "Manages Permissions for followers" Referring to this user story?: "As a participant, I want to grant another participant permission to follow my most recent moodevent
    //Told: Is done inside of followingBook
    // ASK:Mood event history? is that supposed to be stored in MoodEventBook(yes it is)



}
