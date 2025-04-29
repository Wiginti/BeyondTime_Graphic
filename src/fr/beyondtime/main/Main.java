package fr.beyondtime.main;

import fr.beyondtime.view.screens.MenuScreen;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.net.URL;
import fr.beyondtime.util.ImageLoader;

/**
 * The main entry point for the BeyondTime application.
 * Sets up the primary stage, loads the initial menu screen, and applies styling.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * Initializes the primary stage to fit the screen, creates the main menu,
     * applies CSS styling, sets up fullscreen toggle (F11), and shows the stage.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setTitle("BeyondTime");

        MenuScreen menuScreen = new MenuScreen(primaryStage);
        Scene scene = new Scene(menuScreen);
        
        final String cssPath = "/fr/beyondtime/resources/style.css"; 
        try {
            URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl == null) {
                System.err.println("ERROR in Main: CSS file not found at classpath: " + cssPath);
            } else {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("Main CSS loaded successfully from: " + cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("ERROR loading CSS file in Main: " + cssPath);
            e.printStackTrace();
        }

        primaryStage.setScene(scene);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });

        primaryStage.show();
    }

    /**
     * The main method, launching the JavaFX application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
