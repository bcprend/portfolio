package main.resources.helper;

import main.java.scheduler.model.Appointment;
import main.java.scheduler.model.Schedule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public abstract class appointmentsQuery {

    public static void getAllAppointments() throws SQLException {

        String sql =
                "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, " +
                "a.End, c.Customer_Name, u.User_Name, t.Contact_Name " +
                "FROM appointments a " +
                "JOIN customers c on a.Customer_ID = c.Customer_ID " +
                "JOIN users u on a.User_ID = u.User_ID " +
                "JOIN contacts t on a.Contact_ID = t.Contact_ID";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            int appointmentId = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentContact = rs.getString("Contact_Name");
            String appointmentType = rs.getString("Type");
            LocalDateTime appointmentStart = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime appointmentEnd = rs.getTimestamp("End").toLocalDateTime();
            String appointmentCustomer = rs.getString("Customer_Name");
            String appointmentUser = rs.getString("User_Name");

            Appointment appointment = new Appointment(appointmentId, appointmentTitle, appointmentDescription, appointmentLocation,
                    appointmentContact, appointmentType, appointmentStart, appointmentEnd, appointmentCustomer, appointmentUser);

            Schedule.addAppointment(appointment);

        }
    }
}
