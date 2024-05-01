package main.java.scheduler.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.scheduler.DAO.AppointmentsQuery;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Contains the schedule of appointments.
 * Provides methods to access and manipulate appointments.
 * Stores appointments on observable lists, depending on appointment date.
 */
public class Schedule {
    /** List of all appointments. */
    private static ObservableList<Appointment> schedule = FXCollections.observableArrayList();
    /** List of appointments falling within the current week. */
    private static ObservableList<Appointment> weeklySchedule = FXCollections.observableArrayList();
    /** List of appointments falling within the current month. */
    private static ObservableList<Appointment> monthlySchedule = FXCollections.observableArrayList();

    /**
     * Constructs an appointment and adds it to the database.
     * @param title       the title of the appointment
     * @param description the description of the appointment
     * @param location    the location of the appointment
     * @param contactId   the contactId associated with the appointment
     * @param type        the type of the appointment
     * @param start       the start datetime of the appointment
     * @param end         the end datetime of the appointment
     * @param customerId  the customerId associated with the appointment
     * @param userId      the userId who created the appointment
     */
    public static void createAppointment (String title, String description, String location, String type, String start,
                                          String end, int customerId, int userId, int contactId) {
        AppointmentsQuery.insert(title, description, location, type, start, end, customerId, userId, contactId);
    }
    /**
     * Updates the details of an Appointment with the specified details.
     * @param appointmentId the appointmentId of the appointment
     * @param title         the title of the appointment
     * @param description   the description of the appointment
     * @param location      the location of the appointment
     * @param type          the type of the appointment
     * @param start         the start datetime of the appointment
     * @param end           the end datetime of the appointment
     * @param customerId    the customerId associated with the appointment
     * @param userId        the userId who created the appointment
     * @param contactId     the contactId associated with the appointment
     */
    public static void updateAppointment (int appointmentId, String title, String description, String location, String type,
                                          String start, String end, int customerId, int userId, int contactId) {
        AppointmentsQuery.update(appointmentId, title, description, location, type, start, end, customerId, userId, contactId);
    }

    /**
     * Deletes an appointment from the schedule and the database.
     * @param appointment the appointment to delete
     */
    public static void deleteAppointment(Appointment appointment) {
        int appointmentId = appointment.getId();
        AppointmentsQuery.delete(appointmentId);
    }

    /**
     * Adds appointments to the schedule.
     * @param appointment the appointment to be added
     */
    public static void addAppointment (Appointment appointment) {
        // Add all appointments to schedule by default
        schedule.add(appointment);

        if(appointment.getDate().getYear() == LocalDate.now().getYear()) {
            // Add appointment to monthly schedule if this month
            if (appointment.getDate().getMonth() == LocalDate.now().getMonth()) {
                monthlySchedule.add(appointment);
            }

            // Add appointment to weekly schedule if this week
            TemporalField weekField = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
            if (appointment.getDate().get(weekField) == LocalDate.now().get(weekField)) {
                weeklySchedule.add(appointment);
            }
        }
    }

    /** Returns the list of all appointments. */
    public static ObservableList<Appointment> getSchedule() {
        return schedule;
    }

    /** Returns the list of appointments that fall within the current week. */
    public static ObservableList<Appointment> getWeeklySchedule() {
        return weeklySchedule;
    }

    /** Returns the list of appointments that fall within the current month. */
    public static ObservableList<Appointment> getMonthlySchedule() {
        return monthlySchedule;
    }

    /** Clears the current schedule and retrieves all appointments from the database. */
    public static void refreshSchedule() {
        schedule.clear();
        weeklySchedule.clear();
        monthlySchedule.clear();
        AppointmentsQuery.retrieveAllAppointments();
    }

}
