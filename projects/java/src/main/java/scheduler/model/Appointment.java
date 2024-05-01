package main.java.scheduler.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Contains methods to create and modify appointment objects. */
public class Appointment {
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private Contact contact;
    private Customer customer;
    private User user;

    /**
     * Constructor
     * @param id          the ID of the appointment
     * @param title       the title of the appointment
     * @param description the description of the appointment
     * @param location    the location of the appointment
     * @param contact     the contact associated with the appointment
     * @param type        the type of the appointment
     * @param date        the date of the appointment
     * @param start       the start time of the appointment
     * @param end         the end time of the appointment
     * @param customer    the customer associated with the appointment
     * @param user        the user who created the appointment
     */
    public Appointment (int id, String title, String description, String location, Contact contact, String type,
                       LocalDate date, LocalTime start, LocalTime end, Customer customer, User user) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.date = date;
        this.start = start;
        this.end = end;
        this.customer = customer;
        this.user = user;
    }

    /** Getter for appointment id */
    public int getId() {
        return id;
    }

    /**
     * Setter for appointment id
     * @param id the ID of the appointment
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Getter for appointment title */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for appointment title
     * @param title the Title of the appointment
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Getter for appointment description */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for appointment description
     * @param description the Description of the appointment
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Getter for appointment location */
    public String getLocation() {
        return location;
    }

    /**
     * Setter for appointment location
     * @param location  the Location of the appointment
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /** Getter for appointment contact */
    public Contact getContact() {
        return contact;
    }

    /**
     * Setter for appointment contact
     * @param contact the Contact of the appointment
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /** Getter for appointment type */
    public String getType() {
        return type;
    }

    /**
     * Setter for appointment type
     * @param type the Type of the appointment
     */
    public void setType(String type) {
        this.type = type;
    }

    /** Getter for appointment date */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Setter for appointment date
     * @param date the Date of the appointment
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /** Getter for appointment start time. */
    public LocalTime getStart() {
        return start;
    }

    /**
     * Setter for appointment start time
     * @param start the Start Time of the appointment
     */
    public void setStart(LocalTime start) {
        this.start = start;
    }

    /** Getter for appointment end time */
    public LocalTime getEnd() {
        return end;
    }

    /**
     * Setter for appointment end time
     * @param end the End Time of the appointment
     */
    public void setEnd(LocalTime end) {
        this.end = end;
    }

    /** Getter for appointment customer */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Setter for appointment customer
     * @param customer the Customer of the appointment
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /** Getter for appointment user */
    public User getUser() {
        return user;
    }

    /**
     * Setter for appointment user
     * @param user the User of the appointment
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return A formatted string representation of the start time of the appointment.
     * Used by main form tableview.
     */
    public String getFormattedStart() {
        return start.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    /**
     * @return A formatted string representation of the end time of the appointment.
     * Used by main form tableview.
     */
    public String getFormattedEnd() {
        return end.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

}
