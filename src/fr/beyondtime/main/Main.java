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
        primaryStage.setTitle("BeyondTime");

        MenuScreen menuScreen = new MenuScreen(primaryStage);
        Scene scene = menuScreen.getMenuScene();
        primaryStage.setScene(scene);

        // Appliquer la résolution choisie (plein écran si 1920x1080, sinon centré)
        GameConfig config = GameConfig.getInstance();
        Resolution res = config.getCurrentResolution();
        config.applySafeResolutionToStage(primaryStage);

        // Ajout d'une classe CSS selon la résolution
        if (res.getWidth() <= 800 && res.getHeight() <= 600) {
            scene.getRoot().getStyleClass().add("small-ui");
        } else {
            scene.getRoot().getStyleClass().add("normal-ui");
        }

        // Charger la feuille de style CSS
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

        // Toggle plein écran avec F11
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });

        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 