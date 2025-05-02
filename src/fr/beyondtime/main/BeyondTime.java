package fr.beyondtime.main;

import fr.beyondtime.controller.game.GameController;
import fr.beyondtime.model.game.GameState;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Alternative main application class (potentially unused or for a different entry point).
 * This class initializes and starts the game directly using the GameController.
 */
public class BeyondTime extends Application {
    private GameController gameController;

    /**
     * Starts the JavaFX application by initializing the GameController and starting the game.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Beyond Time");
        
        // Création du GameState
        GameState gameState = new GameState("Préhistoire");
        
        // Initialisation du contrôleur principal
        gameController = new GameController(primaryStage, gameState);
        
        // Démarrage du jeu
        gameController.startGame();
    }

    /**
     * Called when the application is stopping.
     * Ensures the game loop is stopped cleanly.
     */
    @Override
    public void stop() {
        // Nettoyage des ressources
        if (gameController != null) {
            gameController.stopGame();
        }
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