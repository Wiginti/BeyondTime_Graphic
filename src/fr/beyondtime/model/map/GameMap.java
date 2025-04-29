package fr.beyondtime.model.map;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameMap {
    private GridPane mapGrid;
    private int cellSize;
    private Tile[][] tiles;

    public GameMap(int width, int height, int cellSize) {
        this.cellSize = cellSize;
        this.tiles = new Tile[height][width];
        initializeMap();
    }

    public GameMap(GridPane grid) {
        this.mapGrid = grid;
        this.cellSize = 50; // Taille par défaut
        // Déterminer la taille de la grille
        int maxRow = 0;
        int maxCol = 0;
        for (javafx.scene.Node node : grid.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);
            if (row != null) maxRow = Math.max(maxRow, row);
            if (col != null) maxCol = Math.max(maxCol, col);
        }
        this.tiles = new Tile[maxRow + 1][maxCol + 1];
        
        // Initialiser les tuiles à partir de la grille
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof StackPane) {
                Integer row = GridPane.getRowIndex(node);
                Integer col = GridPane.getColumnIndex(node);
                if (row == null) row = 0;
                if (col == null) col = 0;
                
                Tile tile = (Tile) ((StackPane) node).getProperties().get("tile");
                if (tile != null) {
                    tiles[row][col] = tile;
                } else {
                    tiles[row][col] = new Tile(true, 1.0, 0);
                }
            }
        }
    }

    private void initializeMap() {
        mapGrid = new GridPane();
        // Par défaut, toutes les tuiles sont passables
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[0].length; col++) {
                tiles[row][col] = new Tile(true, 1.0, 0);
            }
        }
    }

    public GridPane getMapGrid() {
        return mapGrid;
    }

    public int getCellSize() {
        return cellSize;
    }

    public Tile getTileAt(int row, int col) {
        if (row >= 0 && row < tiles.length && col >= 0 && col < tiles[0].length) {
            return tiles[row][col];
        }
        return null;
    }

    public void setTile(int row, int col, Tile tile) {
        if (row >= 0 && row < tiles.length && col >= 0 && col < tiles[0].length) {
            tiles[row][col] = tile;
        }
    }

    public int getWidth() {
        return tiles[0].length;
    }

    public int getHeight() {
        return tiles.length;
    }
} 