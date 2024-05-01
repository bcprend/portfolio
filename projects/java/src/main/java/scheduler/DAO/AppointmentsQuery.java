package main.java.scheduler.DAO;

import main.java.scheduler.model.*;
import main.java.scheduler.util.DateTimeUtil;
import main.resources.helper.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;

/** This abstract class provides methods to query, insert, update, and delete appointments from the database. */
public abstract class AppointmentsQuery {

    /** Retrieves all appointments from the database and populates the Schedule with the retrieved data. */
    public static void retrieveAllAppointments() {

        try {
            String sql = "SELECT * FROM appointments";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                String startString = rs.getString("Start");
                String endString = rs.getString("End");
                Customer customer = Roster.getCustomer(rs.getInt("Customer_ID"));
                User user = Roster.getUser(rs.getInt("User_ID"));
                Contact contact = Roster.getContact(rs.getInt("Contact_ID"));

                // Converts database UTC timestamp strings to ZoneDateTimes in user's time zone
                ZonedDateTime startZDT = DateTimeUtil.convertToLocalZDT(startString);
                ZonedDateTime endZDT = DateTimeUtil.convertToLocalZDT(endString);

                // Establishes LocalDate and LocalTimes for appointment object
                LocalDate date = startZDT.toLocalDate();
                LocalTime start = startZDT.toLocalTime();
                LocalTime end = endZDT.toLocalTime();

                Appointment appointment = new Appointment(id, title, description, location,
                        contact, type, date, start, end, customer, user);

                Schedule.addAppointment(appointment);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new appointment into the database.
     * @param title      the title of the appointment
     * @param description the description of the appointment
     * @param location   the location of the appointment
     * @param type       the type of the appointment
     * @param start      the start time of the appointment in string format ("yyyy-MM-dd HH:mm:ss")
     * @param end        the end time of the appointment in string format ("yyyy-MM-dd HH:mm:ss")
     * @param customerId the ID of the customer associated with the appointment
     * @param userId     the ID of the user associated with the appointment
     * @param contactId  the ID of the contact associated with the appointment
     */
    public static void insert(String title, String description, String location, String type, String start,
                              String end, int customerId, int userId, int contactId) {
        try {
            String sql = "INSERT INTO appointments VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setString(5, start);
            ps.setString(6, end);
            ps.setString(7, DateTimeUtil.nowStringUTC());
            ps.setString(8, "admin");
            ps.setString(9, DateTimeUtil.nowStringUTC());
            ps.setString(10, "admin");
            ps.setInt(11, customerId);
            ps.setInt(12, userId);
            ps.setInt(13, contactId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing appointment in the database.
     * @param appointmentId the ID of the appointment to update
     * @param title         the updated title of the appointment
     * @param description   the updated description of the appointment
     * @param location      the updated location of the appointment
     * @param type          the updated type of the appointment
     * @param start         the updated start time of the appointment in string format ("yyyy-MM-dd HH:mm:ss")
     * @param end           the updated end time of the appointment in string format ("yyyy-MM-dd HH:mm:ss")
     * @param customerId    the updated ID of the customer associated with the appointment
     * @param userId        the updated ID of the user associated with the appointment
     * @param contactId     the updated ID of the contact associated with the appointment
     */
    public static void update(int appointmentId, String title, String description, String location, String type, String start,
                              String end, int customerId, int userId, int contactId) {
        try {
            String sql =
                    "UPDATE appointments " +
                    "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, " +
                            "Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                            "WHERE Appointment_ID = ?";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setString(5, start);
            ps.setString(6, end);
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, "admin");
            ps.setInt(9, customerId);
            ps.setInt(10, userId);
            ps.setInt(11, contactId);
            ps.setInt(12, appointmentId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an appointment from the database.
     * @param appointmentId the ID of the appointment to delete
     */
    public static void delete (int appointmentId) {
        try {
            String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";

            PreparedStatement ps = JDBC.connection.prepareStatement(sql);

            ps.setInt(1, appointmentId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
