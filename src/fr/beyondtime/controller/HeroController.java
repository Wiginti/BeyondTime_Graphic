package fr.beyondtime.controller;

import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.entities.HeroView;
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
        // Position initiale (on peut l'ajuster)
        this.x = heroView.getLayoutX();
        this.y = heroView.getLayoutY();
    }

    public void handleKeyEvent(KeyEvent event) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyCode code = event.getCode();
            double nextX = x;
            double nextY = y;

            switch (code) {
                case UP:
                case W:
                    nextY -= speed;
                    break;
                case DOWN:
                case S:
                    nextY += speed;
                    break;
                case LEFT:
                case A:
                    nextX -= speed;
                    break;
                case RIGHT:
                case D:
                    nextX += speed;
                    break;
                default:
                    break;
            }

            // Calculer l'indice de la cellule visée par le déplacement
            int col = (int)(nextX / cellSize);
            int row = (int)(nextY / cellSize);

            // Vérification des limites de la grille
            if (col < 0 || row < 0 || col >= mapGrid.getColumnCount() || row >= mapGrid.getRowCount()) {
                return;
            }

            // Récupération de la cellule
            Node node = getCellAt(mapGrid, row, col);
            if (node != null && node instanceof StackPane) {
                StackPane cell = (StackPane) node;
                Object tileObj = cell.getProperties().get("tile");
                if (tileObj instanceof Tile) {
                    Tile tile = (Tile) tileObj;
                    if (!tile.isPassable()) {
                        // Collision : la cellule est un obstacle.
                        System.out.println("Collision : cellule non franchissable");
                        return;
                    } else {
                        // Appliquer le facteur de ralentissement
                        double effectiveSpeed = speed * tile.getSlowdownFactor();
                        // Réajuster la position en fonction de la direction
                        if (code == KeyCode.UP || code == KeyCode.W) {
                            nextY = y - effectiveSpeed;
                        } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                            nextY = y + effectiveSpeed;
                        } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                            nextX = x - effectiveSpeed;
                        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                            nextX = x + effectiveSpeed;
                        }
                    }
                }
            }
            x = nextX;
            y = nextY;
            updateView();
        }
    }

    private void updateView() {
        heroView.setPosition(x, y);
    }

    // Méthode utilitaire pour récupérer la cellule de la grille à une position donnée
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