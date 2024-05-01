package main.java.scheduler.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.scheduler.model.Appointment;
import main.java.scheduler.model.Customer;
import main.java.scheduler.model.Roster;
import main.java.scheduler.model.Schedule;

import java.io.IOException;
import java.time.LocalDate;

/** Contains methods that handle actions and operations related to the main form. */
public class MainFormAction {

    private static String css = MainFormAction.class.getResource("/main/java/scheduler/view/style.css").toExternalForm();

    /**
     * Opens the customer form to add a new customer.
     * @throws IOException if an error occurs loading the FXML file
     */
    public static void addCustomer() throws IOException {
        FXMLLoader loader = new FXMLLoader (MainFormAction.class.getResource("/main/java/scheduler/view/customer-form.fxml"));
        Scene scene = new Scene (loader.load());
        Stage stage = new Stage();
        scene.getStylesheets().add(css);
        stage.setTitle("Add New Customer");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        Roster.refreshCustomers();
        Roster.getAllCustomers();
    }

    /**
     * Opens the customer form to edit an existing customer.
     * @param selectedCustomer the selected customer to edit
     * @throws IOException if an error occurs loading the FXML file
     */
    public static void editCustomer(Customer selectedCustomer) throws IOException {
        FXMLLoader loader = new FXMLLoader (MainFormAction.class.getResource("/main/java/scheduler/view/customer-form.fxml"));
        Parent root = loader.load();

        CustomerFormController customerFormController = loader.getController();
        customerFormController.editCustomer(selectedCustomer);

        Stage stage = new Stage();
        stage.setTitle("Edit Customer");
        Scene scene = new Scene (root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        Roster.refreshCustomers();
        Roster.getAllCustomers();
    }

    /**
     * Deletes a customer from the roster.
     * Uses stream operations and lambda expression predicates for concise code.
     * The first lambda filters the schedule stream based on the selected customer.
     * The second lambda calls the delete appointment method for each matching appointment.
     * @param customer the customer to delete
     */
    public static void deleteCustomer(Customer customer) {
        String name = customer.getName();
        int id = customer.getId();

        // Delete all customer's appointments
        Schedule.getSchedule().stream()
                .filter(a -> a.getCustomer() == customer)
                .forEach(a -> Schedule.deleteAppointment(a));

        Roster.deleteCustomer(customer);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Deleted");
        alert.setHeaderText("Customer " + id + ", " + name + " has been deleted.");
        alert.showAndWait();

        Roster.refreshCustomers();
        Roster.getAllCustomers();
        Schedule.refreshSchedule();
        Schedule.getSchedule();
    }

    /**
     * Opens the appointment form to add a new appointment.
     * @throws IOException if an error occurs loading the FXML file
     */
    public static void addAppointment() throws IOException {
        FXMLLoader loader = new FXMLLoader (MainFormAction.class.getResource("/main/java/scheduler/view/appointment-form.fxml"));
        Scene scene = new Scene (loader.load());
        Stage stage = new Stage();
        scene.getStylesheets().add(css);
        stage.setTitle("Add New Appointment");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        Schedule.refreshSchedule();
        Schedule.getSchedule();
    }

    /**
     * Opens the appointment form to edit an existing appointment.
     * @param selectedAppointment the selected appointment to edit
     * @throws IOException if an error occurs loading the FXML file
     */
    public static void editAppointment(Appointment selectedAppointment) throws IOException {
        FXMLLoader loader = new FXMLLoader (MainFormAction.class.getResource("/main/java/scheduler/view/appointment-form.fxml"));
        Parent root = loader.load();

        AppointmentFormController appointmentFormController = loader.getController();
        appointmentFormController.editAppointment(selectedAppointment);

        Stage stage = new Stage();
        stage.setTitle("Edit Appointment");
        Scene scene = new Scene (root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        Schedule.refreshSchedule();
        Schedule.getSchedule();
    }

    /**
     * Deletes the given appointment from the schedule and the database, then retrieves
     * an updated schedule.
     * @param appointment the appointment to be deleted
     */
    public static void deleteAppointment(Appointment appointment) {
        int id = appointment.getId();
        LocalDate date = appointment.getDate();
        String customer = appointment.getCustomer().getName();
        String type = appointment.getType();

        Schedule.deleteAppointment(appointment);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Deleted");
        alert.setHeaderText("Appointment " + id + " for " + customer + " of type " + type + " on " + date + " has been deleted.");
        alert.showAndWait();

        Schedule.refreshSchedule();
        Schedule.getSchedule();
    }
}
