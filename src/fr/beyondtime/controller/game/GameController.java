package fr.beyondtime.controller.game;

import fr.beyondtime.model.game.GameState;
import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.screens.MenuScreen;
import fr.beyondtime.view.screens.VictoryScreen;
import fr.beyondtime.view.screens.GameOverScreen;
import fr.beyondtime.util.MapManager;
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
    private Stage stage;
    private AnimationTimer gameLoop;
    private boolean isPaused;
    private int monstersKilled;

    /**
     * Constructs a GameController.
     *
     * @param stage The primary stage of the application.
     * @param gameState The existing game state.
     */
    public GameController(Stage stage, GameState gameState) {
        this.stage = stage;
        this.gameState = gameState;
        this.isPaused = false;
        this.monstersKilled = 0;
        
        // Initialiser le timer pour la boucle de jeu
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused) {
                    update();
                }
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
            showGameOverScreen();
            return;
        }

        // Vérifier si le héros est sur une case sortie
        Hero hero = gameState.getHero();
        if (hero != null) {
            // Récupérer les coordonnées exactes du héros
            double exactX = hero.getX();
            double exactY = hero.getY();
            
            // Calculer les indices de la grille
            int heroX = (int) Math.floor(exactX / 50); // 50 est la taille d'une case
            int heroY = (int) Math.floor(exactY / 50);
            
            GridPane mapGrid = gameState.getMap().getMapGrid();
            for (Node node : mapGrid.getChildren()) {
                if (node instanceof StackPane) {
                    Integer nodeX = GridPane.getColumnIndex(node);
                    Integer nodeY = GridPane.getRowIndex(node);
                    
                    if (nodeX != null && nodeY != null && nodeX == heroX && nodeY == heroY) {
                        StackPane cell = (StackPane) node;
                        Tile tile = (Tile) cell.getProperties().get("tile");
                        
                        if (tile != null && tile.isExit()) {
                            System.out.println("Le héros a atteint la sortie !");
                            isPaused = true; // Mettre le jeu en pause
                            showVictoryScreen();
                            return;
                        }
                    }
                }
            }
        }
    }

    private void showVictoryScreen() {
        // Store the current level for the callbacks
        String currentLevel = gameState.getCurrentLevel();
        
        // Define callbacks before creating the VictoryScreen
        Runnable onMenuClick = () -> {
            // Retour au menu
            stage.setScene(new MenuScreen(stage).getMenuScene());
            stopGame();
        };

        Runnable onNextLevelClick = () -> {
            // Get the next level based on current level
            String nextLevel = switch (currentLevel) {
                case "Préhistoire" -> "Égypte Antique";
                case "Égypte Antique" -> "2nde Guerre Mondiale";
                case "Level 1 - Prehistory" -> "Égypte Antique";
                case "Level 2 - Ancient Egypt" -> "2nde Guerre Mondiale";
                case "Niveau 1 - Préhistoire" -> "Égypte Antique";
                case "Niveau 2 - Égypte Antique" -> "2nde Guerre Mondiale";
                default -> null;
            };
            
            // Proceed to next level if available
            if (nextLevel != null) {
                System.out.println("Passage au niveau suivant: " + nextLevel);
                MapManager.selectAndLoadMap(stage, nextLevel);
                stopGame();
            }
        };

        // Create and show the VictoryScreen with the prepared callbacks
        VictoryScreen victoryScreen = new VictoryScreen(
            stage,
            currentLevel,
            monstersKilled,
            onMenuClick,
            onNextLevelClick
        );
        victoryScreen.show();
    }

    private void showGameOverScreen() {
        // Créer et afficher l'écran de game over avec le callback pour retourner au menu
        GameOverScreen gameOverScreen = new GameOverScreen(
            stage,
            monstersKilled,
            () -> {
                stage.setScene(new MenuScreen(stage).getMenuScene());
            }
        );
        gameOverScreen.show();
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
        isPaused = true;
    }

    /**
     * Resumes the game loop if it was paused.
     */
    public void resumeGame() {
        System.out.println("GameController: Resuming game loop...");
        isPaused = false;
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

    /**
     * Increments the monster kill count.
     */
    public void incrementMonstersKilled() {
        monstersKilled++;
    }
} 