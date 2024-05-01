package main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.helper.JDBC;

/**
 * This class launches the main form of appointment application
 */
public class Main extends Application {
    /** Variable for dragging window along x axis. */
    private double xOffset = 0;
    /** Variable for dragging window along y axis. */
    private double yOffset = 0;

    /** Launches the login form. */
    @Override
    public void start(Stage stage) throws Exception{

        // Load the log in form FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/log-in-form.fxml"));
        Parent root = loader.load();

        // Set the favicon
        stage.getIcons().add(new Image(getClass().getResourceAsStream("img/favicon.png")));

        // Hides window minimize, maximize, and close buttons
        stage.initStyle(StageStyle.UNDECORATED);

        // Makes window draggable
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() - xOffset);
                stage.setY(mouseEvent.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root);
        String css = this.getClass().getResource("view/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Welcome, please log in");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {

        JDBC.openConnection();
        JDBC.closeConnection();
        launch(args);


    }
}
