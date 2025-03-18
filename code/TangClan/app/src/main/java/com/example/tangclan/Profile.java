package com.example.tangclan;

import static java.lang.Integer.parseInt;
import java.io.Serializable;
import java.sql.Blob;

/**
 * Represents a Profile class
 * This has info pertaining to a user that they may edit through 'Edit Profile'
 * RELATED USER STORIES:
 *      US 03.01.01
 */
public class Profile extends User implements Serializable {//NOTE: EXTENDS MoodEventBook and FollowingBook
    private String displayName;
    private String username;
    private String password;
    private String email;
    private String age;

    private Blob profilePic;

    private MoodEventBook MoodEventBook;
    private FollowingBook followingBook;



    /**
     * Constructor for database purposes
     */
    public Profile() {
        super();
        this.username=null;
        this.password=null;
        this.email=null;
    }

    /**
     * Constructor with all fields
     * @param displayName
     *      The name displayed on MooodEvents
     * @param username
     *      Unique username of the Profile
     * @param password
     *      Password of the user
     * @param email
     * `    Email address of the user
     * @param photo
     *      Profile picture of the user
     */
    public Profile(String displayName, String username, String password, String email, Blob photo){
        super();
        this.displayName = displayName;
        this.username=username;
        this.password=password;
        this.email=email;
        this.age= null;
        this.profilePic = photo;

    }

    /**
     * Constrctor with age set
     * @param displayName
     *      Name to be displayed on mood events
     * @param username
     *      Username of the user
     * @param password
     *      Password of the user
     * @param email
     *      Email address linked to the account
     * @param age
     *      Age of the user
     * @param photo
     *      Profile picture of the user
     */
    public Profile(String displayName, String username, String password, String email, String age, Blob photo){
        super();
        this.displayName = displayName;
        this.username=username;
        this.password=password;
        this.email=email;
        this.age=age;
        this.profilePic = photo;
    }

    /**
     * Getter for the username
     * @return
     *      The profile's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the display name
     * @return
     *      The profile's display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Setter for the display name
     * @param displayName
     *      The name to display
     */
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
        if (age == null) {
            return;
        }
        else if ((Integer.parseInt(age) < 18)) {
            throw new IllegalArgumentException();
        }
        this.age = age;
    }


    /**
     * Getter for the user's MoodEventBook
     * @return
     *      The MoodEventBook associated with this profile
     */

    public MoodEventBook getMoodEventBook() {
        if (MoodEventBook == null) {
            MoodEventBook = new MoodEventBook(); // Initialize if null
        }
        return MoodEventBook;
    }
    public FollowingBook getFollowingBook() {
        if (followingBook == null) {
            followingBook = new FollowingBook(); // Initialize if null
        }
        return followingBook;
    }

    /**
     * Setter for the user's FollowingBook
     * @param followingBook The FollowingBook to assign to this profile
     */
    public void setFollowingBook(FollowingBook followingBook) {
        this.followingBook = followingBook;
    }
    //methods for filter(already presented inside of new feed class with filter created?), methods are inherited


    //TODO:
    //On CRC cards "Manages Permissions for followers" Referring to this user story?: "As a participant, I want to grant another participant permission to follow my most recent moodevent
    //Idea:



}
