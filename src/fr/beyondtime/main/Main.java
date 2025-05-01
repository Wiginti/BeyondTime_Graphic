package fr.beyondtime.main;

import fr.beyondtime.model.config.GameConfig;
import fr.beyondtime.model.config.GameConfig.Resolution;
import fr.beyondtime.view.screens.MenuScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;

/**
 * The main entry point for the BeyondTime application.
 * Sets up the primary stage using resolution from GameConfig, loads the menu, and applies styling.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Récupère la résolution choisie par l'utilisateur (ou par défaut : 1280x720)
        Resolution res = GameConfig.getInstance().getCurrentResolution();
        int width = res.getWidth();
        int height = res.getHeight();

        primaryStage.setTitle("BeyondTime");
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.setResizable(false); // Empêche le redimensionnement manuel

        MenuScreen menuScreen = new MenuScreen(primaryStage);
        Scene scene = menuScreen.getMenuScene();

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

    public static void main(String[] args) {
        launch(args);
    }
}
