package fr.beyondtime.controller;

import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.entities.HeroView;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Controls the Hero's movement, input handling, and collision detection.
 */
public class HeroController {
    private Hero hero;
    private HeroView heroView;
    private Runnable onUpdateCallback;

    /** The hero's current X position in world coordinates. */
    private double worldX;
    /** The hero's current Y position in world coordinates. */
    private double worldY;
    /** The movement speed of the hero in pixels per update. */
    private double speed = 2;

    /** The width of the hero's visual representation and collision box. */
    public static final int HERO_WIDTH = 32;
    /** The height of the hero's visual representation and collision box. */
    public static final int HERO_HEIGHT = 32;

    /** Flag indicating if the up movement key is pressed. */
    private boolean upPressed = false;
    /** Flag indicating if the down movement key is pressed. */
    private boolean downPressed = false;
    /** Flag indicating if the left movement key is pressed. */
    private boolean leftPressed = false;
    /** Flag indicating if the right movement key is pressed. */
    private boolean rightPressed = false;

    /** The visual grid representing the game map. */
    private GridPane mapGrid;
    /** The size (width and height) of each cell in the map grid. */
    private int cellSize;

    /**
     * Constructs a HeroController.
     *
     * @param hero     The Hero data model.
     * @param heroView The Hero visual representation.
     * @param mapGrid  The GridPane representing the map for collision checks.
     * @param cellSize The size of each cell in the mapGrid.
     */
    public HeroController(Hero hero, HeroView heroView, GridPane mapGrid, int cellSize) {
        this.hero = hero;
        this.heroView = heroView;
        this.mapGrid = mapGrid;
        this.cellSize = cellSize;
        
        // Initialiser la position du héros au centre de la carte
        int centerCol = mapGrid.getColumnCount() / 2;
        int centerRow = mapGrid.getRowCount() / 2;
        
        // Convertir la position de la grille en position du monde
        this.worldX = centerCol * cellSize;
        this.worldY = centerRow * cellSize;
        
        updateViewPosition();
        
        System.out.println("HeroController: Initial position - Col: " + centerCol + ", Row: " + centerRow);
        System.out.println("HeroController: World position - X: " + worldX + ", Y: " + worldY);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMovement();
            }
        }.start();
    }

    /**
     * Sets a callback function to be executed after the hero's position is updated.
     *
     * @param callback The Runnable to execute on update.
     */
    public void setOnUpdate(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    /**
     * Handles key press events to set movement flags.
     *
     * @param event The KeyEvent triggered.
     */
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = true;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = true;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = true;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = true;
    }

    /**
     * Handles key release events to unset movement flags.
     *
     * @param event The KeyEvent triggered.
     */
    public void handleKeyRelease(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = false;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = false;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = false;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = false;
    }

    /**
     * Updates the hero's position based on pressed keys and checks for collisions.
     * Called by the AnimationTimer loop.
     */
    private void updateMovement() {
        double nextWorldX = worldX;
        double nextWorldY = worldY;

        if (upPressed) nextWorldY -= speed;
        if (downPressed) nextWorldY += speed;
        if (leftPressed) nextWorldX -= speed;
        if (rightPressed) nextWorldX += speed;

        if (!checkCollision(nextWorldX, nextWorldY)) {
            worldX = nextWorldX;
            worldY = nextWorldY;
            updateViewPosition();
            
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        }
    }

    /**
     * Checks for collisions at the potential next world coordinates.
     * Considers map boundaries and impassable tiles.
     *
     * @param nextWorldX The potential next X coordinate.
     * @param nextWorldY The potential next Y coordinate.
     * @return true if a collision is detected, false otherwise.
     */
    private boolean checkCollision(double nextWorldX, double nextWorldY) {
        // Calculer les limites de la carte en pixels
        double mapWidth = mapGrid.getColumnCount() * cellSize;
        double mapHeight = mapGrid.getRowCount() * cellSize;
        
        // Vérifier les limites de la carte. Utiliser les constantes HERO_WIDTH/HEIGHT
        if (nextWorldX < 0 || nextWorldX + HERO_WIDTH >= mapWidth ||
            nextWorldY < 0 || nextWorldY + HERO_HEIGHT >= mapHeight) {
            System.out.println("Collision avec limite map: nextX=" + nextWorldX + ", nextY=" + nextWorldY);
            return true;
        }
        
        // Calculer les cellules occupées par le héros
        // Soustraire une petite valeur (epsilon) pour éviter les problèmes d'arrondi aux limites
        double epsilon = 0.0001;
        int leftCol = (int) (nextWorldX / cellSize);
        // Utiliser HERO_WIDTH
        int rightCol = (int) ((nextWorldX + HERO_WIDTH - epsilon) / cellSize);
        int topRow = (int) (nextWorldY / cellSize);
        // Utiliser HERO_HEIGHT
        int bottomRow = (int) ((nextWorldY + HERO_HEIGHT - epsilon) / cellSize);
        
        // Vérifier les collisions avec les tuiles
        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                // Vérifier si les indices sont valides (sécurité supplémentaire)
                if (col >= 0 && col < mapGrid.getColumnCount() && 
                    row >= 0 && row < mapGrid.getRowCount()) {
                    if (isTileBlocked(col, row)) {
                         System.out.println("Collision avec tuile bloquante: col=" + col + ", row=" + row);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Checks if the tile at the given grid coordinates is blocked (impassable).
     *
     * @param col The column index of the tile.
     * @param row The row index of the tile.
     * @return true if the tile is blocked, false otherwise.
     */
    private boolean isTileBlocked(int col, int row) {
        Node node = getCellNodeAt(mapGrid, row, col);
        if (node instanceof StackPane) {
            StackPane cell = (StackPane) node;
            Object tileObj = cell.getProperties().get("tile");
            if (tileObj instanceof Tile) {
                return !((Tile) tileObj).isPassable();
            }
        }
        return false;
    }

    /**
     * Updates the visual position of the HeroView.
     */
    private void updateViewPosition() {
        heroView.setPosition(worldX, worldY);
    }

    /**
     * Retrieves the Node (expected to be a StackPane) at a specific row and column in a GridPane.
     *
     * @param grid The GridPane to search within.
     * @param row  The row index.
     * @param col  The column index.
     * @return The Node at the specified location, or null if not found.
     */
    private Node getCellNodeAt(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);
            if (nodeRow == null) nodeRow = 0;
            if (nodeCol == null) nodeCol = 0;
            if (nodeRow == row && nodeCol == col) {
                return node;
            }
        }
        return null;
    }

    /**
     * Gets the current world X coordinate of the hero.
     *
     * @return The world X coordinate.
     */
    public double getWorldX() {
        return worldX;
    }

    /**
     * Gets the current world Y coordinate of the hero.
     *
     * @return The world Y coordinate.
     */
    public double getWorldY() {
        return worldY;
    }
}