package main.java.scheduler.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.scheduler.DAO.CustomersQuery;


/**
 * Contains the rosters of customers, contacts, and users.
 * Provides methods to access and manipulate objects of each type.
 * Stores all objects of each type on observable lists.
 * The lists are initially populated via the retrieveAll methods within their respective DAOs
 * which are called upon application launch.
 */
public class Roster {

    /** List of all Customers. */
    private static ObservableList<Customer> customers = FXCollections.observableArrayList();
    /** List of all Contacts. */
    private static ObservableList<Contact> contacts = FXCollections.observableArrayList();
    /** List of all Users. */
    private static ObservableList<User> users = FXCollections.observableArrayList();

    /**
     * Adds an object to the roster corresponding to its type.
     * @param object the object to be added
     */
    public static void addToRoster (Object object) {
        if (object instanceof Customer) {
            customers.add((Customer) object);
        } else if (object instanceof User) {
            users.add((User) object);
        } else if (object instanceof Contact) {
            contacts.add((Contact) object);
        }
    }

    /**
     * Constructs a customer with the given details and inserts it into the database.
     * @param name the name of the customer
     * @param address the address of the customer
     * @param post the postal code of the customer
     * @param phone the phone number of the customer
     * @param division the division of the customer
     */
    public static void createCustomer(String name, String address, String post, String phone, String division){
        CustomersQuery.insert(name, address, post, phone, division);
    }

    /**
     * Updates the details of a customer with the given ID in the database.
     * @param id the ID of the customer to update
     * @param name the new name of the customer
     * @param address the new address of the customer
     * @param post the new postal code of the customer
     * @param phone the new phone number of the customer
     * @param division the new division of the customer
     */
    public static void updateCustomer(int id, String name, String address, String post, String phone, String division) {
        CustomersQuery.update(id, name, address, post, phone, division);
    }

    /**
     * Deletes a customer from the roster and the database.
     * @param customer the customer to delete
     */
    public static void deleteCustomer (Customer customer) {
        CustomersQuery.deleteById(customer.getId());
    }

    /** Refreshes the list of customers by clearing it and retrieving all customers from the database. */
    public static void refreshCustomers() {
        customers.clear();
        CustomersQuery.retrieveAllCustomers();
    }

    /**
     * Returns the customer with the specified ID from the roster.
     * @param customerId the ID of the customer to retrieve
     */
    public static Customer getCustomer(int customerId) {
        for (Customer customer : customers) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    /** Returns the roster of customers. */
    public static ObservableList<Customer> getAllCustomers() {
        return customers;
    }

    /**
     * Returns the contact with the specified ID from the roster.
     * @param contactId the ID of the contact to retrieve
     */
    public static Contact getContact(int contactId) {
        for (Contact contact : contacts) {
            if (contact.getId() == contactId) {
                return contact;
            }
        }
        return null;
    }

    /** Returns the roster of contacts. */
    public static ObservableList<Contact> getAllContacts() {
        return contacts;
    }

    /**
     * Returns the user with the specified ID from the roster.
     * @param userId the ID of the user to retrieve
     */
    public static User getUser(int userId) {
        for (User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    /** Returns the roster of users. */
    public static ObservableList<User> getAllUsers() {
        return users;
    }

}
