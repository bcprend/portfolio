package main.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainFormController implements Initializable {
    /** Variable for dragging window along x axis. */
    private double xOffset = 0;
    /** Variable for dragging window along y axis. */
    private double yOffset = 0;

    @FXML
    private Label testLabel;

    @FXML
    private ChoiceBox<String> appointmentChoiceBox;

    private String[] dateRanges = {"All","Weekly","Monthly"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentChoiceBox.getItems().addAll(dateRanges);
        appointmentChoiceBox.setValue("Weekly");
        appointmentChoiceBox.setOnAction(this::getDateRange);
    }

    private void getDateRange(javafx.event.ActionEvent actionEvent) {
        String currDate = appointmentChoiceBox.getValue();
        testLabel.setText(currDate);
    }

    public void onCloseXClick() {
        System.exit(0);
    }

    public void onMinClick(MouseEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    public void onLogOutClick(ActionEvent actionEvent) throws IOException {

        // Load the log in form FXML file
        FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("/main/view/log-in-form.fxml"));
        Parent logInForm = logInLoader.load();

        // Get the controller of the log in form
        LogInController logInController = logInLoader.getController();

        // Create a new scene with the log in form and set it on the primary stage
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();


        // Makes window draggable
        logInForm.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            }
        });

        logInForm.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() - xOffset);
                stage.setY(mouseEvent.getScreenY() - yOffset);
            }
        });
        Scene scene = new Scene(logInForm);
        String css = this.getClass().getResource("/main/view/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();

    }

}
