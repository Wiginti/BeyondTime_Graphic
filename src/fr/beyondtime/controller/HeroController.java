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
    private Hero hero; // Modèle du héros
    private HeroView heroView; // Vue graphique du héros
    private Runnable onUpdateCallback; // Callback pour notifier la vue après un déplacement

    private double speed = 2; // Vitesse de déplacement en pixels

    public static final int HERO_WIDTH = 32;
    public static final int HERO_HEIGHT = 32;

    // États des touches directionnelles
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Carte et taille des cellules pour les collisions
    private GridPane mapGrid;
    private int cellSize;

    /**
     * Constructeur du contrôleur du héros
     */
    public HeroController(Hero hero, HeroView heroView, GridPane mapGrid, int cellSize) {
        this.hero = hero;
        this.heroView = heroView;
        this.mapGrid = mapGrid;
        this.cellSize = cellSize;

        // Position initiale du héros : centre de la carte
        int centerCol = mapGrid.getColumnCount() / 2;
        int centerRow = mapGrid.getRowCount() / 2;
        double startX = centerCol * cellSize;
        double startY = centerRow * cellSize;

        hero.setPosition(startX, startY);
        heroView.setPosition(startX, startY);

        System.out.println("HeroController: Initial position - Col: " + centerCol + ", Row: " + centerRow);
        System.out.println("HeroController: World position - X: " + startX + ", Y: " + startY);

        // Boucle d'animation continue pour mise à jour du mouvement
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMovement();
            }
        }.start();
    }

    // Enregistre une fonction de rappel pour les mises à jour (ex : caméra)
    public void setOnUpdate(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    // Gère l'appui des touches directionnelles
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = true;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = true;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = true;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = true;
    }

    // Gère le relâchement des touches directionnelles
    public void handleKeyRelease(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = false;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = false;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = false;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = false;
    }

    /**
     * Met à jour la position du héros selon les touches appuyées et les collisions
     */
    private void updateMovement() {
        double nextWorldX = hero.getX();
        double nextWorldY = hero.getY();

        if (upPressed) nextWorldY -= speed;
        if (downPressed) nextWorldY += speed;
        if (leftPressed) nextWorldX -= speed;
        if (rightPressed) nextWorldX += speed;

        if (!checkCollision(nextWorldX, nextWorldY)) {
            hero.setPosition(nextWorldX, nextWorldY);     // mise à jour modèle
            heroView.setPosition(nextWorldX, nextWorldY); // mise à jour vue

            if (onUpdateCallback != null) {
                onUpdateCallback.run(); // déclenche par exemple une mise à jour de caméra
            }
        }
    }

    /**
     * Vérifie s'il y a une collision à la position donnée (bords + tuiles bloquantes)
     */
    private boolean checkCollision(double nextWorldX, double nextWorldY) {
        double mapWidth = mapGrid.getColumnCount() * cellSize;
        double mapHeight = mapGrid.getRowCount() * cellSize;

        if (nextWorldX < 0 || nextWorldX + HERO_WIDTH >= mapWidth ||
            nextWorldY < 0 || nextWorldY + HERO_HEIGHT >= mapHeight) {
            System.out.println("Collision avec limite map: nextX=" + nextWorldX + ", nextY=" + nextWorldY);
            return true;
        }

        // Calcul des cellules occupées par le héros
        double epsilon = 0.0001;
        int leftCol = (int) (nextWorldX / cellSize);
        int rightCol = (int) ((nextWorldX + HERO_WIDTH - epsilon) / cellSize);
        int topRow = (int) (nextWorldY / cellSize);
        int bottomRow = (int) ((nextWorldY + HERO_HEIGHT - epsilon) / cellSize);

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
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

    // Indique si une tuile est bloquante
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

    // Récupère la cellule de la grille aux coordonnées spécifiées
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

    // Expose la position X du modèle pour la caméra
    public double getWorldX() {
        return hero.getX();
    }

    // Expose la position Y du modèle pour la caméra
    public double getWorldY() {
        return hero.getY();
    }
}