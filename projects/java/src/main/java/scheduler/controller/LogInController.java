package main.java.scheduler.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.java.scheduler.DAO.UsersQuery;
import main.java.scheduler.util.DateTimeUtil;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/** Controller class for login form. */
public class LogInController implements Initializable {
    public Button buttonLogIn;
    public Label labelUserLocation;
    public Label labelUsername;
    public Label labelPassword;
    public Label labelLocation;
    public TextField userField;
    public PasswordField passwordField;
    public Label labelLoginError;


    // Variables for dragging the window
    private double xOffset = 0;
    private double yOffset = 0;

    private Locale userLocale = Locale.getDefault();

    ResourceBundle rb = ResourceBundle.getBundle("main.resources.bundle.messages", userLocale);

    /** Initializes login form. Sets label text based on system language settings. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (userLocale.getLanguage().equals("fr")) {
        labelUsername.setText(rb.getString("username"));
        labelPassword.setText(rb.getString("password"));
        labelLocation.setText(rb.getString("location"));
        buttonLogIn.setText(rb.getString("login"));
        }

        String zoneId = ZoneId.systemDefault().getId();
        labelUserLocation.setText(zoneId);

    }
    
    /**
     * Exits the program.
     * @param mouseEvent The mouse event triggered by the close button
     * */
    public void onCloseXClick(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Validates login credentials and launches main form if valid, otherwise displays an error.
     * Logs login attempts.
     * @param actionEvent The action event triggered by the login button
     */
    public void onButtonLogInClick(ActionEvent actionEvent) {
        labelLoginError.setText("");
        userField.setStyle("all: unset");
        passwordField.setStyle("all: unset");
        String user = userField.getText();
        String pass = passwordField.getText();
        Boolean isValid = UsersQuery.validateLogin(user, pass);

        try {
            if (user.isEmpty()) {
                userField.setStyle("-fx-text-box-border : red;");
                labelLoginError.setText(rb.getString("nouser"));
                return;
            }

            logAttempt(isValid, user);

            if (isValid) {
                proceedToMain(actionEvent);
            } else {
                userField.setStyle("-fx-text-box-border : red;");
                passwordField.setStyle("-fx-text-box-border : red;");
                labelLoginError.setText(rb.getString("invalid"));
            }
        } catch (IOException e) {
            System.out.println("Unable to log attempt: " + e.getMessage());
        }

    }

    /**
     * Writes UTC timestamp, username, and login status to text file.
     * @param success The success status of the login attempt
     * @param user The user associated with the login attempt
     */
    private void logAttempt(Boolean success, String user) {
        try {
            String logFile = "login_activity.txt";
            FileWriter fw = new FileWriter(logFile, true);
            PrintWriter pw = new PrintWriter(fw);

            String timeStamp = DateTimeUtil.nowStringUTC();
            String logEntry = (success ? "Successful" : "Failed") + " login attempt by " + user + " at " + timeStamp + "UTC";

            pw.println(logEntry);
            pw.close();

        } catch(IOException e) {
            System.out.println("Error logging attempt: " + e.getMessage());
        }

    }

    /**
     * Launches the main form.
     * Uses lambda expressions to concisely implement EventHandler interface for window dragging.
     * @param actionEvent The action event triggered for proceeding to the main form.
     * @throws IOException if an I/O error occurs while loading the FXML file
     */
    private void proceedToMain(ActionEvent actionEvent) throws IOException {
        // Load the main form FXML file
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/main/java/scheduler/view/main-form.fxml"));
        Parent mainForm = mainLoader.load();

        // Create a new scene with the main form and set it on the primary stage
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Makes window draggable
        mainForm.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        mainForm.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - xOffset);
            stage.setY(mouseEvent.getScreenY() - yOffset);
        });

        Scene scene = new Scene(mainForm);
        String css = this.getClass().getResource("/main/java/scheduler/view/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

}