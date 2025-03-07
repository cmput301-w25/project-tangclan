package com.example.tangclan;

import static java.lang.Integer.parseInt;

import java.io.Serializable;

// This has info pertaining to a user that they may edit through 'Edit Profile'
public class Profile extends User implements Serializable {//NOTE: EXTENDS MoodEventBook and FollowingBook
    private String displayName;
    private String username;
    private String password;
    private String email;
    private String age;

    public Profile(String displayName, String username, String password, String email){
        super();
        this.username=username;
        this.password=password;
        this.email=email;
    }

    public Profile(String displayName, String username, String password, String email, String age){
        super();
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
        if (password.matches(".+[!#$%^&*|].+")){
            spChar = true;
        }
        if((password.length()>=8)){
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
