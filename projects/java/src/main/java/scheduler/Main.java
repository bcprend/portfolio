package main.java.scheduler;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.scheduler.DAO.*;
import main.resources.helper.JDBC;

import java.sql.SQLException;
import java.util.Locale;

public class Main extends Application {
    // Variables for dragging the window
    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Launches the login form.
     * Uses lambda expressions to concisely implement EventHandler interface for window dragging.
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Loads the log in form FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/scheduler/view/log-in-form.fxml"));
        Parent root = loader.load();

        // Sets the favicon
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/img/favicon.png")));

        // Hides window minimize, maximize, and close buttons
        stage.initStyle(StageStyle.UNDECORATED);

        // Makes window draggable
        root.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        root.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - xOffset);
            stage.setY(mouseEvent.getScreenY() - yOffset);
        });

        Scene scene = new Scene(root);
        String css = this.getClass().getResource("view/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Welcome, please log in");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens connection to database and pulls all division, contact, user, customer, and appointment data.
     * Launches the login screen, closes connection to database.
     */
    public static void main(String[] args) throws SQLException {
        /**
        //Locale.setDefault(new Locale("fr")); // For language testing
         */

        JDBC.openConnection();

        DivisionsQuery.retrieveAllDivisions();
        ContactsQuery.retrieveAllContacts();
        UsersQuery.retrieveAllUsers();
        CustomersQuery.retrieveAllCustomers();
        AppointmentsQuery.retrieveAllAppointments();

        launch(args);

        JDBC.closeConnection();

    }
}
