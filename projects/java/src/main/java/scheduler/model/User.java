package main.java.scheduler.model;

/** Contains methods to create and modify user objects. */
public class User {
    private int id;
    private String userName;

    /**
     * Constructor
     * @param id   The ID of the user.
     * @param userName The user name of the user.
     */
    public User (int id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    /** Getter for user id */
    public int getId() {
        return id;
    }

    /**
     * Setter for user id
     * @param id The ID to be set for the user.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Getter for user name */
    public String getUserName() {
        return userName;
    }

    /**
     * Setter for user name
     * @param userName The user name to be set for the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** Overrides toString method and returns the name of the user. */
    @Override
    public String toString() {
        return this.userName;
    }
}
