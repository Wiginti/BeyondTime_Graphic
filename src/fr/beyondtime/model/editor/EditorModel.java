package fr.beyondtime.model.editor;

import fr.beyondtime.model.map.Tile;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EditorModel {
    public enum TileType {
        NORMAL, OBSTACLE, SLOW, POISON
    }

    private GridPane mapGrid;
    private int gridRows;
    private int gridColumns;
    private int cellSize = 50;
    private boolean eraserMode = false;
    private TileType currentTileType;
    private Image selectedAssetImage;
    private String selectedAssetPath;
    private File currentDirectory;
    private File rootAssets;

    private List<String> savedMaps = new ArrayList<>();

    public EditorModel() {
        // Initialisation du répertoire des assets (use relative path)
        rootAssets = new File("src/fr/beyondtime/assets").getAbsoluteFile();
        currentDirectory = rootAssets;
        if (!rootAssets.exists() || !rootAssets.isDirectory()) {
            System.err.println("Warning: Root assets directory not found at: " + rootAssets.getPath());
        }
    }

    public void setMapGrid(GridPane grid) {
        this.mapGrid = grid;
    }

    public GridPane getMapGrid() {
        return mapGrid;
    }

    public void setGridRows(int rows) {
        this.gridRows = rows;
    }

    public int getGridRows() {
        return gridRows;
    }

    public void setGridColumns(int columns) {
        this.gridColumns = columns;
    }

    public int getGridColumns() {
        return gridColumns;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setEraserMode(boolean mode) {
        this.eraserMode = mode;
    }

    public boolean isEraserMode() {
        return eraserMode;
    }

    public void setCurrentTileType(TileType type) {
        this.currentTileType = type;
    }

    public TileType getCurrentTileType() {
        return currentTileType;
    }

    public void setSelectedAssetImage(Image image) {
        this.selectedAssetImage = image;
    }

    public Image getSelectedAssetImage() {
        return selectedAssetImage;
    }

    public void setSelectedAssetPath(String path) {
        this.selectedAssetPath = path;
    }

    public String getSelectedAssetPath() {
        return selectedAssetPath;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(File directory) {
        this.currentDirectory = directory;
    }

    public File getRootAssets() {
        return rootAssets;
    }

    public List<String> getSavedMaps() {
        return savedMaps;
    }

    // Method to load image from an absolute asset path
    public Image loadImageFromAssetPath(String absolutePath) throws IOException {
         File imgFile = new File(absolutePath);
         if (!imgFile.exists() || !imgFile.isFile()) {
             System.err.println("Image file not found: " + absolutePath);
             return null;
         }
         try (InputStream is = new FileInputStream(imgFile)) {
             return new Image(is);
         } catch (IOException e) {
              System.err.println("IOException loading image: " + absolutePath);
              throw e; // Re-throw exception to be caught by caller
         }
     }
    
     // Method to get asset path relative to the rootAssets directory
     public String getRelativeAssetPath(File assetFile) {
         String rootPath = rootAssets.getPath();
         String filePath = assetFile.getPath();
         if (filePath.startsWith(rootPath)) {
             String relative = filePath.substring(rootPath.length());
             // Ensure consistent separator
             relative = relative.replace(File.separatorChar, '/'); 
             // Remove leading separator if present
             if (relative.startsWith("/")) {
                 relative = relative.substring(1);
             }
             return relative;
         } else {
             // File is not within the root asset directory - this shouldn't happen
             System.err.println("Warning: Asset file " + filePath + " is not within root " + rootPath);
             return assetFile.getName(); // Fallback to just the name
         }
     }

    // Méthodes pour modifier les cellules
    public void setCellAsNormal(StackPane cell) {
        updateCellAppearance(cell, Color.LIGHTGRAY, true, 1.0, 0);
    }

    public void setCellAsObstacle(StackPane cell) {
        updateCellAppearance(cell, Color.DARKGRAY, false, 1.0, 0);
    }

    public void setCellAsSlowZone(StackPane cell) {
        updateCellAppearance(cell, Color.YELLOW, true, 0.5, 0);
    }

    public void setCellAsPoison(StackPane cell) {
        updateCellAppearance(cell, Color.GREEN, true, 1.0, 10);
    }

    private void updateCellAppearance(StackPane cell, Color color, boolean passable, double speed, int damage) {
        cell.getChildren().clear();
        Rectangle background = new Rectangle(cellSize, cellSize);
        background.setFill(color);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
        cell.getProperties().put("tile", new Tile(passable, speed, damage));
    }
}