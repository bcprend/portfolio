package main.java.scheduler.model;

/** Contains methods to create and modify customer objects. */
public class Customer {
    private int id;
    private String name;
    private String address;
    private String post;
    private String phone;
    private String division;
    private String country;

    /**
     * Constructor
     * @param id        the ID of the customer
     * @param name      the Name of the customer
     * @param address   the Address of the customer
     * @param post      the Post of the customer
     * @param division  the Division of the customer
     */
    public Customer (int id, String name, String address, String post, String phone, String division) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.post = post;
        this.phone = phone;
        this.division = division;
        this.country = Division.getCountry(division);
    }

    /** Getter for customer id */
    public int getId() {
        return id;
    }

    /**
     * Setter for customer id
     * @param id the ID of the customer
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Getter for customer name */
    public String getName() {
        return name;
    }

    /**
     * Setter for customer name
     * @param name the Name of the appointment
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Getter for customer address */
    public String getAddress() {
        return address;
    }

    /**
     * Setter for customer address
     * @param address the Address of the appointment
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** Getter for customer post */
    public String getPost() {
        return post;
    }

    /**
     * Setter for customer post
     * @param post the Post of the appointment
     */
    public void setPost(String post) {
        this.post = post;
    }

    /** Getter for customer phone */
    public String getPhone() {
        return phone;
    }

    /**
     * Setter for customer phone
     * @param phone the Phone of the appointment
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** Getter for customer division */
    public String getDivision() {
        return division;
    }

    /**
     * Setter for customer division
     * @param division the Division of the appointment
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /** Getter for customer country */
    public String getCountry() {
        return country;
    }

    /** Overrides toString method and returns the id and name of the customer. */
    @Override
    public String toString() {
        return (id + " - " + name);
    }

}
