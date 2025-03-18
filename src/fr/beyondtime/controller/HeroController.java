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

public class HeroController {
    private Hero hero;
    private HeroView heroView;

    // Position actuelle du héros
    private double x;
    private double y;
    private double speed = 5; // Vitesse de base

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private GridPane mapGrid;
    private int cellSize;

    /**
     * Constructeur qui reçoit également la map et la taille des cellules pour gérer les collisions.
     */
    public HeroController(Hero hero, HeroView heroView, GridPane mapGrid, int cellSize) {
        this.hero = hero;
        this.heroView = heroView;
        this.mapGrid = mapGrid;
        this.cellSize = cellSize;
        this.x = heroView.getLayoutX();
        this.y = heroView.getLayoutY();

        // AnimationTimer pour gérer le mouvement continu
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMovement();
            }
        }.start();
    }

    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = true;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = true;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = true;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = true;
    }

    public void handleKeyRelease(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = false;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = false;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = false;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = false;
    }

    private void updateMovement() {
        double nextX = x;
        double nextY = y;

        if (upPressed) nextY -= speed;
        if (downPressed) nextY += speed;
        if (leftPressed) nextX -= speed;
        if (rightPressed) nextX += speed;

        // Vérification de collision avec toute la hitbox du héros
        if (!checkCollision(nextX, nextY)) {
            x = nextX;
            y = nextY;
            updateView();
        }
    }

    private boolean checkCollision(double nextX, double nextY) {
        return isTileBlocked(nextX, nextY) ||
                isTileBlocked(nextX + cellSize -1 , nextY) ||
                isTileBlocked(nextX, nextY + cellSize -1 ) ||
                isTileBlocked(nextX + cellSize-1 , nextY + cellSize-1 );
    }
    private boolean isTileBlocked(double x, double y) {
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);

        if (col < 0 || row < 0 || col >= mapGrid.getColumnCount() || row >= mapGrid.getRowCount()) {
            return true; // Empêcher de sortir des limites
        }

        Node node = getCellAt(mapGrid, row, col);
        if (node instanceof StackPane) {
            StackPane cell = (StackPane) node;
            Object tileObj = cell.getProperties().get("tile");
            if (tileObj instanceof Tile) {
                Tile tile = (Tile) tileObj;
                return !tile.isPassable();
            }
        }
        return false;
    }

    private void updateView() {
        heroView.setPosition(x, y);
    }

    private Node getCellAt(GridPane grid, int row, int col) {
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
}