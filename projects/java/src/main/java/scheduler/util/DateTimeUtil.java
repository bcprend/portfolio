package main.java.scheduler.util;

import main.java.scheduler.model.Appointment;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time conversions and manipulations.
 */
public final class DateTimeUtil {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static ZoneId localZoneId = ZoneId.systemDefault();
    private static ZoneId utcZoneId = ZoneId.of("UTC");

    /**
     * Converts the given date, hour, minute, and meridiem values to a UTC string.
     * The values are collected from the appointment form.
     * @param date     The date to convert.
     * @param hour     The hour value in 12-hour format.
     * @param minute   The minute value.
     * @param meridiem The meridiem (AM/PM) value.
     * @return The UTC string representation of the converted date and time.
     */
    public static String convertToUTCString(LocalDate date, String hour, String minute, String meridiem) {
        int parsedMinute = Integer.parseInt(minute);
        int parsedHour = Integer.parseInt(hour);

        // Convert to 24-hour
        if (parsedHour == 12) {
            parsedHour -= 12;
        }
        if (meridiem.equals("PM")) {
            parsedHour += 12;
        }

        // Create LocalTime with adjusted hour and minute
        LocalTime time = LocalTime.of(parsedHour, parsedMinute);

        // Create LocalDateTime with date and time
        LocalDateTime ldt = LocalDateTime.of(date, time);

        // Convert LocalDateTime to ZonedDateTime with system default time zone
        ZonedDateTime zdt = ZonedDateTime.of(ldt, localZoneId);

        // Convert ZonedDateTime from local to UTC
        ZonedDateTime utcZonedDateTime = zdt.withZoneSameInstant(utcZoneId);

        // Format the UTC ZonedDateTime to a string representation
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return utcZonedDateTime.format(dtf);
    }

    /**
     * Converts the given UTC timestamp ZonedDateTime in the system's time zone.
     * Timestamps are retrieved from the database in String format, parsed, and converted
     * before being displayed in the scheduling application.
     * @param timestampString The timestamp string to be converted
     */
    public static ZonedDateTime convertToLocalZDT(String timestampString) {
        // Parse string from database into LocalDateTime
        LocalDateTime utcLDT = LocalDateTime.parse(timestampString, dtf);

        // Attach UTC time zone to the LocalDateTime
        ZonedDateTime utcZDT = ZonedDateTime.of(utcLDT, utcZoneId);

        // Convert UTC ZonedDateTime to User's Time Zone
        ZonedDateTime userZonedDateTime = utcZDT.withZoneSameInstant(localZoneId);

        return userZonedDateTime;
    }

    /** Returns the current time in UTC string format. */
    public static String nowStringUTC() {
        ZonedDateTime localZDT = ZonedDateTime.of(LocalDateTime.now(), localZoneId);
        ZonedDateTime utcZDT = localZDT.withZoneSameInstant(utcZoneId);
        return utcZDT.format(dtf);
    }

    /** Converts the given time and date from system time to UTC */
    public static LocalTime convertToUTC(LocalTime localTime, LocalDate date) {
        LocalDateTime ldt = LocalDateTime.of(date, localTime);
        ZonedDateTime zdt = ZonedDateTime.of(ldt, localZoneId);
        ZonedDateTime zdtUTC = zdt.withZoneSameInstant(utcZoneId);
        return zdtUTC.toLocalTime();
    }

    /**
     * Checks whether the given appointment times overlap with an existing appointment.
     * @param appointment The existing appointment
     * @param newAppointmentStartString The start time of the new appointment in string format
     * @param newAppointmentEndString The end time of the new appointment in string format
     * @return Returns True if there is an overlap, otherwise False
     * */
    public static boolean overlapsExistingAppointment (Appointment appointment, String newAppointmentStartString, String newAppointmentEndString) {
        // Convert existing appointment times to UTC
        LocalDate existingDate = appointment.getDate();
        LocalTime existingStart = convertToUTC(appointment.getStart(), existingDate);
        LocalTime existingEnd = convertToUTC(appointment.getEnd(), existingDate);

        LocalTime newAppointmentStart = LocalTime.parse(newAppointmentStartString, dtf);
        LocalTime newAppointmentEnd = LocalTime.parse(newAppointmentEndString, dtf);

        if ((newAppointmentStart.isBefore(existingStart) && (newAppointmentEnd.isBefore(existingStart) || newAppointmentEnd.equals(existingStart)))
                || ((newAppointmentStart.isAfter(existingEnd) || newAppointmentStart.equals(existingEnd)) && newAppointmentEnd.isAfter(existingEnd))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks the validity of an appointment time slot based on the given start and end times.
     * Returns an error message if the end time precedes the start time or if the appointment falls outside business hours.
     * Otherwise, returns "valid" to indicate a valid appointment.
     * @param startString The appointment start time
     * @param endString The appointment end time
     * @return The validity status or error message in string format
     */
    public static String checkTime (String startString, String endString) {
        // Establish business hours relative to appointment start date and time
        LocalDate businessDate = LocalDate.parse(startString, dtf);
        LocalDateTime businessOpen = LocalDateTime.of(businessDate, LocalTime.of(12, 0));
        LocalDateTime businessClose = businessOpen.plusHours(14);

        //
        LocalDateTime appointmentStart = LocalDateTime.parse(startString, dtf);
        LocalDateTime appointmentEnd = LocalDateTime.parse(endString, dtf);

        if (appointmentEnd.isBefore(appointmentStart) || appointmentEnd.equals(appointmentStart)) {
            return "Error: End time must be later than start time";
        } else if (!((appointmentStart.equals(businessOpen) || appointmentStart.isAfter(businessOpen))
                && (appointmentEnd.equals(businessClose) || appointmentEnd.isBefore(businessClose)))) {
            return "Error: Appointment falls outside business hours";
        } else {
            return "valid";
        }
    }

}
