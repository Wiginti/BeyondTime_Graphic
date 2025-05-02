package fr.beyondtime.controller.game;

import fr.beyondtime.model.game.GameState;
import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.screens.GameScreen;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

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
     * @param levelName The name of the level.
     */
    public GameController(Stage stage, String levelName) {
        this.gameState = new GameState(levelName);
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

        // Vérifier si le héros est sur une case sortie
        Hero hero = gameState.getHero();
        if (hero != null) {
            int heroX = (int) (hero.getX() / 50); // 50 est la taille d'une case
            int heroY = (int) (hero.getY() / 50);
            
            GridPane mapGrid = gameState.getMap().getMapGrid();
            for (Node node : mapGrid.getChildren()) {
                Integer nodeX = GridPane.getColumnIndex(node);
                Integer nodeY = GridPane.getRowIndex(node);
                if (nodeX == null) nodeX = 0;
                if (nodeY == null) nodeY = 0;
                
                if (nodeX == heroX && nodeY == heroY && node instanceof StackPane) {
                    StackPane stackPane = (StackPane) node;
                    Object tileObj = stackPane.getProperties().get("tile");
                    if (tileObj instanceof Tile) {
                        Tile tile = (Tile) tileObj;
                        if (tile.isExit()) {
                            gameState.setGameOver(true);
                            stopGame();
                            System.out.println("Félicitations ! Vous avez terminé le niveau " + gameState.getCurrentLevel() + " !");
                            return;
                        }
                    }
                }
            }
        }
    }

    private Node getCellAt(int x, int y) {
        for (Node node : gameState.getMap().getMapGrid().getChildren()) {
            Integer nodeX = GridPane.getColumnIndex(node);
            Integer nodeY = GridPane.getRowIndex(node);
            if (nodeX == null) nodeX = 0;
            if (nodeY == null) nodeY = 0;
            if (nodeX == x && nodeY == y) return node;
        }
        return null;
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