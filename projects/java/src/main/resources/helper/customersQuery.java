package main.resources.helper;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import main.java.scheduler.model.Appointment;
import main.java.scheduler.model.Customer;
import main.java.scheduler.model.Roster;
import main.java.scheduler.model.Schedule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public abstract class customersQuery {

    public static void getAllCustomers() throws SQLException {

        Roster.clear();
        String sql =
                "SELECT c.Customer_ID, c.Customer_Name, c.Address, " +
                "c.Postal_Code, c.Phone, c.Division_ID, d.Division " +
                "FROM customers c " +
                "JOIN first_level_divisions d ON c.Division_ID = d.Division_ID";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            int customerId = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String customerAddress = rs.getString("Address");
            String customerPost = rs.getString("Postal_Code");
            String customerPhone = rs.getString("Phone");
            String customerDivision = rs.getString("Division");

            Customer customer = new Customer(customerId, customerName, customerAddress, customerPost, customerPhone, customerDivision);
            Roster.addCustomer(customer);
        }
    }

    public static void insert(String name, String address, String post, String phone, String division) throws SQLException {
        int divisionId = divisionsQuery.getDivisionId(division);

        String sql = "INSERT INTO customers VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, name);
        ps.setString(2, address);
        ps.setString(3, post);
        ps.setString(4, phone);
        ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(6, "admin");
        ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(8, "admin");
        ps.setInt(9, divisionId);

        ps.executeUpdate();
    }

    public static void update(int id, String name, String address, String post, String phone, String division) throws SQLException {
        int divisionId = divisionsQuery.getDivisionId(division);

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

    }

    public static void delete(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, id);

        ps.executeUpdate();
    }
}
