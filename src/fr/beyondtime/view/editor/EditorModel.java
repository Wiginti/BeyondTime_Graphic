package fr.beyondtime.view.editor;

import fr.beyondtime.model.map.Tile;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorModel {
    private GridPane mapGrid;
    private int cellSize = 50;
    private int gridRows;
    private int gridColumns;
    
    private File rootAssets;
    private File currentDirectory;
    
    private Image selectedAssetImage = null;
    private String selectedAssetPath = null;
    private boolean eraserMode = false;
    
    public enum TileType { NORMAL, OBSTACLE, SLOW, POISON }
    private TileType currentTileType = null;
    
    private List<String> savedMaps = new ArrayList<>();

    public EditorModel() {
        try {
            rootAssets = new File(getClass().getResource("/fr/beyondtime/assets").toURI());
            currentDirectory = rootAssets;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters and Setters
    public GridPane getMapGrid() { return mapGrid; }
    public void setMapGrid(GridPane mapGrid) { this.mapGrid = mapGrid; }
    
    public int getCellSize() { return cellSize; }
    
    public int getGridRows() { return gridRows; }
    public void setGridRows(int gridRows) { this.gridRows = gridRows; }
    
    public int getGridColumns() { return gridColumns; }
    public void setGridColumns(int gridColumns) { this.gridColumns = gridColumns; }
    
    public File getCurrentDirectory() { return currentDirectory; }
    public void setCurrentDirectory(File currentDirectory) { this.currentDirectory = currentDirectory; }
    
    public Image getSelectedAssetImage() { return selectedAssetImage; }
    public void setSelectedAssetImage(Image selectedAssetImage) { this.selectedAssetImage = selectedAssetImage; }
    
    public String getSelectedAssetPath() { return selectedAssetPath; }
    public void setSelectedAssetPath(String selectedAssetPath) { this.selectedAssetPath = selectedAssetPath; }
    
    public boolean isEraserMode() { return eraserMode; }
    public void setEraserMode(boolean eraserMode) { this.eraserMode = eraserMode; }
    
    public TileType getCurrentTileType() { return currentTileType; }
    public void setCurrentTileType(TileType currentTileType) { this.currentTileType = currentTileType; }
    
    public File getRootAssets() { return rootAssets; }
    
    public List<String> getSavedMaps() { return savedMaps; }

    // Tile modification methods
    public void setCellAsNormal(StackPane cell) {
        cell.getProperties().put("tile", new Tile(true, 1.0, 0));
        updateCellBackground(cell, Color.LIGHTGRAY);
    }

    public void setCellAsObstacle(StackPane cell) {
        cell.getProperties().put("tile", new Tile(false, 0, 0));
        updateCellBackground(cell, Color.RED);
    }

    public void setCellAsSlowZone(StackPane cell) {
        cell.getProperties().put("tile", new Tile(true, 0.5, 0));
        updateCellBackground(cell, Color.ORANGE);
    }

    public void setCellAsPoison(StackPane cell) {
        cell.getProperties().put("tile", new Tile(true, 1.0, 10));
        updateCellBackground(cell, Color.PURPLE);
    }

    private void updateCellBackground(StackPane cell, Color color) {
        for (javafx.scene.Node node : cell.getChildren()) {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setFill(color);
            }
        }
    }
}