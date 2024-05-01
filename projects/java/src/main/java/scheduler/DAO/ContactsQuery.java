package main.java.scheduler.DAO;

import main.java.scheduler.model.Contact;
import main.java.scheduler.model.Roster;
import main.resources.helper.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This abstract class provides methods to query contacts from the database. */
public abstract class ContactsQuery {

    /** Retrieves all contacts from the database and populates the Contact roster. */
    public static void retrieveAllContacts() {

        try {
            String sql = "SELECT * FROM contacts";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("Contact_ID");
                String name = rs.getString("Contact_Name");

                Contact contact = new Contact(id, name);
                Roster.addToRoster(contact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
