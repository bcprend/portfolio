package main.java.scheduler.DAO;

import main.java.scheduler.model.Customer;
import main.java.scheduler.model.Division;
import main.java.scheduler.model.Roster;
import main.java.scheduler.util.DateTimeUtil;
import main.resources.helper.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** This abstract class provides methods to query, insert, update, and delete customers from the database. */
public abstract class CustomersQuery {

    /** Retrieves all customers from the database and populates the Customer roster. */
    public static void retrieveAllCustomers() {

        try {
            String sql =
                    "SELECT c.Customer_ID, c.Customer_Name, c.Address, " +
                    "c.Postal_Code, c.Phone, c.Division_ID, d.Division " +
                    "FROM customers c " +
                    "JOIN first_level_divisions d ON c.Division_ID = d.Division_ID";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int customerId = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String customerAddress = rs.getString("Address");
                String customerPost = rs.getString("Postal_Code");
                String customerPhone = rs.getString("Phone");
                String customerDivision = rs.getString("Division");

                Customer customer = new Customer(customerId, customerName, customerAddress, customerPost, customerPhone, customerDivision);
                Roster.addToRoster(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new customer into the database.
     * @param name     the name of the customer
     * @param address  the address of the customer
     * @param post     the postal code of the customer
     * @param phone    the phone number of the customer
     * @param division the division of the customer
     */
    public static void insert(String name, String address, String post, String phone, String division) {
        int divisionId = Division.getDivisionId(division);

        try {
            String sql = "INSERT INTO customers VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, post);
            ps.setString(4, phone);
            ps.setString(5, DateTimeUtil.nowStringUTC());
            ps.setString(6, "admin");
            ps.setString(7, DateTimeUtil.nowStringUTC());
            ps.setString(8, "admin");
            ps.setInt(9, divisionId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing customer in the database.
     * @param id       the ID of the customer to update
     * @param name     the updated name of the customer
     * @param address  the updated address of the customer
     * @param post     the updated postal code of the customer
     * @param phone    the updated phone number of the customer
     * @param division the updated division of the customer
     */
    public static void update(int id, String name, String address, String post, String phone, String division) {
        int divisionId = Division.getDivisionId(division);

        try {
            String sql =
                    "UPDATE customers " +
                    "SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? " +
                    "WHERE Customer_ID = ?";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, post);
            ps.setString(4, phone);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, "admin");
            ps.setInt(7, divisionId);
            ps.setInt(8, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a customer from the database.
     * @param id the ID of the customer to delete
     */
    public static void deleteById(int id) {

        try {
            String sql = "DELETE FROM customers WHERE Customer_ID = ?";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
