package main.java.scheduler.model;

/** Contains methods to create and modify contact objects. */
public class Contact {
    private int id;
    private String name;

    /**
     * Constructor
     * @param id   The ID of the contact.
     * @param name The name of the contact.
     */
    public Contact (int id, String name) {
        this.id = id;
        this.name = name;
    }

    /** Getter for contact id */
    public int getId() {
        return id;
    }

    /**
     * Setter for contact id
     * @param id The ID to be set for the contact.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Getter for contact name */
    public String getName() {
        return name;
    }

    /**
     * Setter for contact name
     * @param name The name to be set for the contact.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Overrides the toString method and returns the id and name of the contact. */
    @Override
    public String toString() {
        return (id + " - " + name);
    }
}
