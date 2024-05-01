package main.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/** Controller class with event handlers and methods for login form functionality. */
public class LogInController {
    /** Variable for dragging window along x axis. */
    private double xOffset = 0;
    /** Variable for dragging window along y axis. */
    private double yOffset = 0;

    /** Exits the program. */
    public void onCloseXClick(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void onButtonLogInClick(ActionEvent actionEvent) throws IOException {

        // Load the main form FXML file
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/main/view/main-form.fxml"));
        Parent mainForm = mainLoader.load();

        // Get the controller of the main form
        MainFormController mainFormController = mainLoader.getController();

        // Create a new scene with the main form and set it on the primary stage
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Hides window minimize, maximize, and close buttons
        //stage.initStyle(StageStyle.UNDECORATED);

        // Makes window draggable
        mainForm.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            }
        });

        mainForm.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() - xOffset);
                stage.setY(mouseEvent.getScreenY() - yOffset);
            }
        });
        Scene scene = new Scene(mainForm);
        String css = this.getClass().getResource("/main/view/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();

        //TODO: perform login validation

    }
}