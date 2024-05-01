package main.java.scheduler.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.java.scheduler.model.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/** Controller class for the main form. */
public class MainFormController implements Initializable {
    // Table, columns, and controls for appointments tab
    public TableView tableSchedule;
    public TableColumn colApptTitle;
    public TableColumn colApptDescription;
    public TableColumn colApptLocation;
    public TableColumn colApptContact;
    public TableColumn colApptType;
    public TableColumn colApptStart;
    public TableColumn colApptEnd;
    public TableColumn colApptCustomer;
    public TableColumn colApptUser;
    public TableColumn colApptId;

    // Table, columns, and controls for reports tab
    public TableView tableTypeReport;
    public TableColumn colTypeMonth;
    public TableColumn colTypeType;
    public TableColumn colTypeCount;
    public ComboBox comboBoxAvgType;
    public TableView tableAverageLengthReport;
    public TableColumn colAverageType;
    public TableColumn colAverageTotal;
    public TableColumn colAverageLength;

    @FXML
    private ComboBox<String> comboBoxDateRange;
    private String[] dateRanges = {"All","This Week","This Month"};
    private String[] appointmentTypes = {"All Types", "Planning Session", "De-Briefing", "Consultation", "Other"};
    private String[] months = {"All Months","January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

    // Table and columns for customers tab
    public TableView tableCustomer;
    public TableColumn colCustId;
    public TableColumn colCustName;
    public TableColumn colCustAddress;
    public TableColumn colCustPhone;
    public TableColumn colCustState;
    public TableColumn colCustCountry;
    public TableColumn colCustPost;
    public TableColumn colApptDate;

    // Controls for tally report
    public ComboBox comboBoxType;
    public ComboBox comboBoxMonth;

    // Table, columns, and controls for contact report
    public ComboBox comboBoxContact;
    public TableView tableContactReport;
    public TableColumn colContactId;
    public TableColumn colContactTitle;
    public TableColumn colContactType;
    public TableColumn colContactDescription;
    public TableColumn colContactDate;
    public TableColumn colContactStart;
    public TableColumn colContactEnd;
    public TableColumn colContactCustomer;

    // Table, columns, and controls for customer report
    public ComboBox comboBoxCustomer;
    public TableView tableCustomerReport;
    public TableColumn colCustomerId;
    public TableColumn colCustomerTitle;
    public TableColumn colCustomerType;
    public TableColumn colCustomerDescription;
    public TableColumn colCustomerDate;
    public TableColumn colCustomerStart;
    public TableColumn colCustomerEnd;
    public TableColumn colCustomerContact;

    //Variables for dragging the window
    private double xOffset = 0;
    private double yOffset = 0;




    /** Initializes the main form. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeControls();
        initializeTables();
        upcomingAppointmentStatus();
    }

    /** Populates form controls and sets initial values. */
    private void initializeControls() {
        // Configure the appointment choice box
        comboBoxDateRange.getItems().addAll(dateRanges);
        comboBoxDateRange.setValue("All");
        comboBoxDateRange.setOnAction(this::getDateRange);

        comboBoxType.getItems().addAll(appointmentTypes);
        comboBoxType.setValue("All Types");
        comboBoxMonth.getItems().addAll(months);
        comboBoxMonth.setValue("All Months");

        comboBoxAvgType.getItems().addAll(appointmentTypes);
        comboBoxAvgType.setValue("All Types");

        comboBoxContact.setItems(Roster.getAllContacts());

        comboBoxCustomer.setItems(Roster.getAllCustomers());
    }

    /** Populates form tables and sets initial values. */
    private void initializeTables() {
        // Set up the appointments table
        tableSchedule.setItems(Schedule.getSchedule());
        colApptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colApptDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colApptLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colApptContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colApptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colApptDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colApptStart.setCellValueFactory(new PropertyValueFactory<>("formattedStart"));
        colApptEnd.setCellValueFactory(new PropertyValueFactory<>("formattedEnd"));
        colApptCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        colApptUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        colApptId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Set up the customers table
        tableCustomer.setItems(Roster.getAllCustomers());
        colCustId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCustPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colCustState.setCellValueFactory(new PropertyValueFactory<>("division"));
        colCustCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        colCustPost.setCellValueFactory(new PropertyValueFactory<>("post"));

        getMonthTypeReport();
        getAverageLengthReport();
    }

    /**
     * Checks the schedule for appointments starting within 15 minutes of current system time.
     * Displays an alert regarding the status of upcoming appointments.
     */
    private void upcomingAppointmentStatus () {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a");
        LocalDate currentDay = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime inFifteen = currentTime.plusMinutes(15);
        LocalDate appointmentDate = null;
        LocalTime appointmentStart = null;
        int appointmentId = 0;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upcoming Appointments");

        boolean upcomingAppointment = false;
        for (Appointment a : Schedule.getSchedule()) {
            appointmentId = a.getId();
            appointmentDate = a.getDate();
            appointmentStart = a.getStart();
            if(appointmentDate.equals(currentDay) && (appointmentStart.isAfter(currentTime) && appointmentStart.isBefore(inFifteen))) {
                upcomingAppointment = true;
                break;
            }
        }

        if(upcomingAppointment) {
            alert.setHeaderText("There is at least one appointment within fifteen minutes!");
            alert.setContentText("Appointment: " + appointmentId + " is scheduled for today, " + appointmentDate + " at " + appointmentStart.format(dtf));
        } else {
            alert.setHeaderText("There are no appointments starting within 15 minutes.");
        }
        alert.showAndWait();
    }

    /**
     * Updates appointment table view based on the selected date range.
     * @param event The event triggered by the selection change.
     */
    private void getDateRange(Event event) {
        String currDate = comboBoxDateRange.getValue();
        if(currDate.equals("All")) {
            tableSchedule.setItems(Schedule.getSchedule());
        } else if (currDate.equals("This Week")) {
            tableSchedule.setItems(Schedule.getWeeklySchedule());
        } else if (currDate.equals("This Month")) {
            tableSchedule.setItems(Schedule.getMonthlySchedule());
        }
    }


    /**
     * Retrieves the contact report based on the selected contact.
     * Uses a lambda expression predicate in the forEach method for concise code.
     * The lambda expression adds the appointment to the byContact list if it meets the requirements of the conditional.
     */
    public void getContactReport() {
        Contact selectedContact = (Contact) comboBoxContact.getValue();
        ObservableList<Appointment> byContact = FXCollections.observableArrayList();

        Schedule.getSchedule().forEach((a -> {
            if (a.getContact().equals(selectedContact)) {
                byContact.add(a);
            }
        }));


        tableContactReport.setItems(byContact);
        colContactId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colContactTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colContactType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colContactDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colContactDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colContactStart.setCellValueFactory(new PropertyValueFactory<>("formattedStart"));
        colContactEnd.setCellValueFactory(new PropertyValueFactory<>("formattedEnd"));
        colContactCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
    }

    /**
     * Retrieves the customer report based on the selected customer.
     * Uses a lambda expression predicate in the forEach method for concise code.
     * The lambda expression adds the appointment to the byCustomer list if it meets the requirements of the conditional.
     */
    public void getCustomerReport() {
        Customer selectedCustomer = (Customer) comboBoxCustomer.getValue();
        ObservableList<Appointment> byCustomer = FXCollections.observableArrayList();

        Schedule.getSchedule().forEach((a -> {
            if (a.getCustomer().equals(selectedCustomer)) {
                byCustomer.add(a);
            }
        }));

        tableCustomerReport.setItems(byCustomer);
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustomerTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCustomerType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCustomerDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCustomerDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCustomerStart.setCellValueFactory(new PropertyValueFactory<>("formattedStart"));
        colCustomerEnd.setCellValueFactory(new PropertyValueFactory<>("formattedEnd"));
        colCustomerContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
    }

    /** Retrieves and displays the Month Type Report data based on the selected type and month. */
    public void getMonthTypeReport() {
        String selectedType = comboBoxType.getValue().toString();
        String selectedMonth = comboBoxMonth.getValue().toString();

        tableTypeReport.setItems(Reports.getMonthTypeReportData(selectedType, selectedMonth));
        colTypeMonth.setCellValueFactory(new PropertyValueFactory<>("month"));
        colTypeType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colTypeCount.setCellValueFactory(new PropertyValueFactory<>("count"));
    }

    /** Retrieves and displays the Average Length Report data based on the selected type. */
    public void getAverageLengthReport() {
        String selectedType = comboBoxAvgType.getValue().toString();

        tableAverageLengthReport.setItems(Reports.getTypeAvgLengthReportData(selectedType));
        colAverageType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAverageTotal.setCellValueFactory(new PropertyValueFactory<>("count"));
        colAverageLength.setCellValueFactory(new PropertyValueFactory<>("avgLength"));
    }

    /**
     * Closes the application window.
     * @param mouseEvent The event triggered by the button click.
     */
    public void onCloseXClick(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Minimizes the application window.
     * @param event The event triggered by the button click.
     */
    public void onMinClick(MouseEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Logs out and switches to the log in form.
     * Uses lambda expressions to concisely implement EventHandler interface for window dragging.
     * @param actionEvent The event triggered by the button click.
     * @throws IOException if an error occurs loading the log in form.
     */
    public void onLogOutClick(ActionEvent actionEvent) throws IOException {
        // Load the log in form FXML file
        FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("/main/java/scheduler/view/log-in-form.fxml"));
        Parent logInForm = logInLoader.load();

        // Create a new scene with the log in form and set it on the primary stage
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();


        // Makes window draggable
        logInForm.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        logInForm.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - xOffset);
            stage.setY(mouseEvent.getScreenY() - yOffset);
        });

        Scene scene = new Scene(logInForm);
        String css = this.getClass().getResource("/main/java/scheduler/view/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens the appointment form to add a new appointment.
     * @throws IOException if an error occurs loading the appointment form.
     */
    public void onAddAppointmentClick() throws IOException {
        MainFormAction.addAppointment();
    }

    /**
     * Opens the appointment form to edit the selected appointment.
     * @throws IOException if an error occurs loading the appointment form.
     */
    public void onEditAppointmentClick() throws IOException {
        Appointment selectedAppointment = (Appointment) tableSchedule.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            MainFormAction.editAppointment(selectedAppointment);
        }
    }

    /**
     * Deletes the selected appointment.
     */
    public void onDeleteAppointmentClick() {
        Appointment selectedAppointment = (Appointment) tableSchedule.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            int id = selectedAppointment.getId();
            String customer = selectedAppointment.getCustomer().getName();
            String type = selectedAppointment.getType();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Appointment");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Appointment " + id + " for " + customer + " of type " + type + " will be deleted.");

            ButtonType buttonTypeOk = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(buttonTypeOk, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == buttonTypeOk) {
                MainFormAction.deleteAppointment(selectedAppointment);
            }
        }
    }

    /**
     * Opens the customer form to add a new customer.
     * @throws IOException if an error occurs loading the customer form.
     */
    public void onAddCustomerClick() throws IOException {
            MainFormAction.addCustomer();
    }

    /**
     * Opens the customer form to edit the selected customer.
     * @throws IOException if an error occurs loading the customer form.
     */
    public void onEditCustomerClick() throws IOException {
        Customer selectedCustomer = (Customer) tableCustomer.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            MainFormAction.editCustomer(selectedCustomer);
        }
    }

    /**
     * Deletes the selected customer and all associated appointments.
     */
    public void onDeleteCustomerClick() {
        Customer selectedCustomer = (Customer) tableCustomer.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            int id = selectedCustomer.getId();
            String name = selectedCustomer.getName();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Customer");
            alert.setHeaderText("Are you sure you want to delete customer " + id + ", " + name + "?");
            alert.setContentText("This action will also delete all of " + name + "'s appointments.");

            ButtonType buttonTypeOk = new ButtonType("Delete Customer and All Appointments", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(buttonTypeOk, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == buttonTypeOk) {
                MainFormAction.deleteCustomer(selectedCustomer);
            }
        }
    }

}
