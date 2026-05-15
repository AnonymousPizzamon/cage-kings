package com.example.cagekingsfx;

/**
 * This class represents a user with a username and password
 */
public class User {
    private String username;
    private String password;

    /**
     * Constructor for the User to set the username and password
     *
     * @param username the user's username
     * @param password the user's password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     *
     * @return the user's username
     */
    public String getUsername() { return username; }

    /**
     *
     * @return the user's password
     */
    public String getPassword() { return password; }
}