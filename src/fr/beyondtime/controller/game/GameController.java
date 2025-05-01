package fr.beyondtime.controller.game;

import fr.beyondtime.model.game.GameState;
import fr.beyondtime.view.screens.GameScreen;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

/**
 * Controls the main game flow, including the game loop and state management.
 */
public class GameController {
    private GameState gameState;
    @SuppressWarnings("unused")
	private GameScreen gameScreen;
    private AnimationTimer gameLoop;

    /**
     * Constructs a GameController.
     * Initializes the game state, creates the game screen, and sets up the game loop.
     *
     * @param stage The primary stage of the application.
     */
    public GameController(Stage stage) {
        this.gameState = new GameState();
        this.gameScreen = new GameScreen(stage, gameState);
        
        initializeGameLoop();
    }

    /**
     * Initializes the main game loop using AnimationTimer.
     * The handle method calls the update method on each frame.
     */
    private void initializeGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
    }

    /**
     * Updates the game state.
     * Currently checks for game over condition based on hero health.
     */
    private void update() {
        if (gameState.getHealth() <= 0) {
            gameState.setGameOver(true);
            stopGame();
            System.out.println("Game Over!");
            return;
        }
    }

    /**
     * Starts the game loop.
     */
    public void startGame() {
        System.out.println("GameController: Starting game loop...");
        gameLoop.start();
    }

    /**
     * Pauses the game loop.
     */
    public void pauseGame() {
        System.out.println("GameController: Pausing game loop...");
        gameLoop.stop();
    }

    /**
     * Resumes the game loop if it was paused.
     */
    public void resumeGame() {
        System.out.println("GameController: Resuming game loop...");
        gameLoop.start();
    }

    /**
     * Stops the game loop and sets the game state to game over.
     */
    public void stopGame() {
        System.out.println("GameController: Stopping game loop...");
        gameLoop.stop();
        gameState.setGameOver(true);
    }
} 