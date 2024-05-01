package main.java.scheduler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.java.scheduler.model.*;
import main.java.scheduler.util.DateTimeUtil;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/** Controller class for appointment form. */
public class AppointmentFormController implements Initializable {
    public Label labelAppointmentHeader;
    public Label labelAppointmentId;
    public Label labelError;
    public TextField fieldTitle;
    public TextField fieldLocation;
    public Button buttonCancel;
    public Button buttonSave;
    public DatePicker datePicker;
    public TextArea fieldDescription;

    public TableView tableCustomer;
    public TableColumn colCustomerId;
    public TableColumn colCustomerName;

    public TableView tableUser;
    public TableColumn colUserId;
    public TableColumn colUserName;

    public ComboBox comboType;
    public ComboBox comboContact;
    public ComboBox comboStartHour;
    public ComboBox comboStartMinute;
    public ComboBox comboEndHour;
    public ComboBox comboEndMinute;
    public ComboBox<String> comboStartMeridiem;
    public ComboBox<String> comboEndMeridiem;

    private Appointment selectedAppointment = null;
    private boolean editMode = false;
    private String css = "-fx-border-color: red;";

    /** Initializes the appointment form view. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelAppointmentHeader.setText("Add New Appointment");
        initializeControls();
    }

    /** Populates tables and controls and sets initial values. */
    private void initializeControls () {
        tableCustomer.setItems(Roster.getAllCustomers());
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));

        tableUser.setItems(Roster.getAllUsers());
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));

        comboContact.setItems(Roster.getAllContacts());
        comboType.getItems().addAll("Planning Session", "De-Briefing", "Consultation", "Other");
        comboType.setValue("Other");

        comboStartHour.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        comboStartHour.setValue("9");

        comboStartMinute.getItems().addAll("00", "15", "30", "45");
        comboStartMinute.setValue("00");

        comboStartMeridiem.getItems().addAll("AM", "PM");
        comboStartMeridiem.setValue("AM");

        comboEndHour.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        comboEndHour.setValue("10");

        comboEndMinute.getItems().addAll("00", "15", "30", "45");
        comboEndMinute.setValue("00");

        comboEndMeridiem.getItems().addAll("AM", "PM");
        comboEndMeridiem.setValue("AM");
    }

    /**
     * Sets the selectedAppointment.
     * Populates form fields and controls with appointment data.
     * @param selectedAppointment The appointment for which form values are set
     */
    public void editAppointment(Appointment selectedAppointment) {
        this.selectedAppointment = selectedAppointment;
        this.editMode = true;
        labelAppointmentHeader.setText("Edit Existing Appointment");
        labelAppointmentId.setText(String.valueOf(selectedAppointment.getId()));

        if(selectedAppointment != null) {
            fieldTitle.setText(selectedAppointment.getTitle());
            fieldLocation.setText(selectedAppointment.getLocation());
            comboType.setValue(selectedAppointment.getType());
            fieldDescription.setText(selectedAppointment.getDescription());
            comboContact.setValue(selectedAppointment.getContact());
            datePicker.setValue(selectedAppointment.getDate());

            setTimeCombos(selectedAppointment);

            tableCustomer.getSelectionModel().select(selectedAppointment.getCustomer());
            tableUser.getSelectionModel().select(selectedAppointment.getUser());
        }
    }

    /**
     * Collects and validates form data.
     * Creates/updates appointments with collected form data.
     * Closes the appointment form.
     * @param actionEvent The action event triggered by the save button
     */
    public void onSaveClick(ActionEvent actionEvent) {
        boolean formIsComplete = validateForm();

        if(formIsComplete) {
            try {
                // Collect data from text fields and combobox selections
                String title = fieldTitle.getText().trim().substring(0, Math.min(50, fieldTitle.getText().trim().length()));
                String description = fieldDescription.getText().trim().substring(0, Math.min(50, fieldDescription.getText().trim().length()));
                String location = fieldLocation.getText().trim().substring(0, Math.min(50, fieldLocation.getText().trim().length()));
                String type = (String) comboType.getValue();
                LocalDate date = datePicker.getValue();
                String startHour = comboStartHour.getValue().toString();
                String startMinute = comboStartMinute.getValue().toString();
                String startMeridiem = comboStartMeridiem.getValue();
                String endHour = comboEndHour.getValue().toString();
                String endMinute = comboEndMinute.getValue().toString();
                String endMeridiem = comboEndMeridiem.getValue();
                Customer selectedCustomer = (Customer) tableCustomer.getSelectionModel().getSelectedItem();
                User selectedUser = (User) tableUser.getSelectionModel().getSelectedItem();
                Contact selectedContact = (Contact) comboContact.getSelectionModel().getSelectedItem();

                // Retrieve id numbers based on selections
                int customerId = selectedCustomer.getId();
                int userId = selectedUser.getId();
                int contactId = selectedContact.getId();

                // Convert time and date selections from system time to UTC in string format
                String start = DateTimeUtil.convertToUTCString(date, startHour, startMinute, startMeridiem);
                String end = DateTimeUtil.convertToUTCString(date, endHour, endMinute, endMeridiem);

                // Verify start and end times are valid
                if(!validateTime(start, end)) {
                    return;
                }

                // Verify new appointment does not overlap with existing
                if(appointmentOverlap(selectedCustomer, date, start, end)) {
                    return;
                }

                // Create or update appointment with collected data
                if (editMode) {
                    int appointmentId = selectedAppointment.getId();
                    Schedule.updateAppointment(appointmentId, title, description, location, type, start, end, customerId, userId, contactId);
                } else {
                    Schedule.createAppointment(title, description, location, type, start, end, customerId, userId, contactId);
                }

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error Saving Data");
                alert.setContentText("An error occurred while saving the data.");

                alert.showAndWait();
                return;
            }
        } else {
            return;
        }

        // Close appointment form
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /** Closes the form without saving. */
    public void onCancelClick() {
        Stage stage = (Stage) buttonCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Converts 24-hour to 12-hour time and sets combo values.
     * @param a The appointment for which time values are set.
     */
    private void setTimeCombos (Appointment a) {
        LocalTime startTime = a.getStart();
        LocalTime endTime = a.getEnd();

        comboStartHour.setValue(startTime.format(DateTimeFormatter.ofPattern("h")));
        comboStartMinute.setValue(startTime.format(DateTimeFormatter.ofPattern("mm")));
        comboStartMeridiem.setValue(startTime.format(DateTimeFormatter.ofPattern("a")));

        comboEndHour.setValue(endTime.format(DateTimeFormatter.ofPattern("h")));
        comboEndMinute.setValue(endTime.format(DateTimeFormatter.ofPattern("mm")));
        comboEndMeridiem.setValue(endTime.format(DateTimeFormatter.ofPattern("a")));
    }

    /**
     * Verifies that all appointment details have been filled.
     * Highlights empty fields and displays an error message if validation fails.
     * @return True if all fields are filled, otherwise False
     */
    private boolean validateForm() {
        boolean isValid = true;

        if (fieldTitle.getText().trim().isEmpty()) {
            fieldTitle.setStyle(css);
            isValid = false;
        } else fieldTitle.setStyle("");

        if (fieldDescription.getText().trim().isEmpty()) {
            fieldDescription.setStyle(css);
            isValid = false;
        } else fieldDescription.setStyle("");

        if (fieldLocation.getText().trim().isEmpty()) {
            fieldLocation.setStyle(css);
            isValid = false;
        } else fieldLocation.setStyle("");

        if (comboType.getValue() == null) {
            comboType.setStyle(css);
            isValid = false;
        } else comboType.setStyle("");

        if (datePicker.getValue() == null) {
            datePicker.setStyle(css);
            isValid = false;
        } else datePicker.setStyle("");

        if (comboStartHour.getValue() == null || comboStartMinute.getValue() == null) {
            comboStartHour.setStyle(css);
            comboStartMinute.setStyle(css);
            isValid = false;
        } else {
            comboStartHour.setStyle("");
            comboStartMinute.setStyle("");
        }

        if (comboEndHour.getValue() == null || comboEndMinute.getValue() == null) {
            comboEndHour.setStyle(css);
            comboEndMinute.setStyle(css);
            isValid = false;
        } else {
            comboEndHour.setStyle("");
            comboEndMinute.setStyle("");
        }

        if (tableCustomer.getSelectionModel().getSelectedItem() == null) {
            tableCustomer.setStyle(css);
            isValid = false;
        } else tableCustomer.setStyle("");

        if (tableUser.getSelectionModel().getSelectedItem() == null) {
            tableUser.setStyle(css);
            isValid = false;
        } else tableUser.setStyle("");

        if (comboContact.getSelectionModel().getSelectedItem() == null) {
            comboContact.setStyle(css);
            isValid = false;
        } else comboContact.setStyle("");

        if (!isValid) {
            labelError.setText("Error: Please fill in all required fields");
        } else {
            labelError.setText("");
        }

        return isValid;
    }

    /**
     * Validates the start and end times of an appointment.
     * Highlights time selectors and displays an error message if validation fails.
     * @param start The start time of the appointment in UTC string format.
     * @param end   The end time of the appointment in UTC string format.
     * @return True if times are valid, otherwise False
     */
    private boolean validateTime (String start, String end) {
        String timeValidity = DateTimeUtil.checkTime(start, end);
        if (!timeValidity.equals("valid")) {
            comboStartHour.setStyle(css);
            comboStartMinute.setStyle(css);
            comboEndHour.setStyle(css);
            comboEndMinute.setStyle(css);
            labelError.setText(timeValidity);
            return false;
        } else {
            comboStartHour.setStyle("");
            comboStartMinute.setStyle("");
            comboEndHour.setStyle("");
            comboEndMinute.setStyle("");
            labelError.setText("");
            return true;
        }
    }

    /**
     * Checks if an appointment conflicts with an existing appointment for the given customer and date.
     * Uses stream operations and lambda expression predicates for concise code.
     * The first lambda filters the schedule stream based on appointment customer and date.
     * The second lambda checks each matching appointment for overlap status.
     * @param selectedCustomer The customer whose appointments will be checked.
     * @param date The date of the appointment.
     * @param start The start time of the appointment in UTC string format.
     * @param end The end time of the appointment in UTC string format.
     * @return True if there is a conflict with an existing appointment, False otherwise.
     */
    private boolean appointmentOverlap (Customer selectedCustomer, LocalDate date, String start, String end) {
        boolean overlaps = Schedule.getSchedule().stream()
                .filter(a -> a.getCustomer().equals(selectedCustomer) && a.getDate().equals(date)
                    && !(selectedAppointment != null && a.getId() == selectedAppointment.getId()))
                .anyMatch(a -> DateTimeUtil.overlapsExistingAppointment(a, start, end));

        if (overlaps) {
            comboStartHour.setStyle(css);
            comboStartMinute.setStyle(css);
            comboEndHour.setStyle(css);
            comboEndMinute.setStyle(css);
            labelError.setText("Error: Appointment conflicts with an existing appointment");
        } else {
            comboStartHour.setStyle("");
            comboStartMinute.setStyle("");
            comboEndHour.setStyle("");
            comboEndMinute.setStyle("");
            labelError.setText("");
        }
        return overlaps;
    }
}
