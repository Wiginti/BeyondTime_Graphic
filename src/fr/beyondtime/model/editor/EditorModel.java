package fr.beyondtime.model.editor;

import fr.beyondtime.model.map.Tile;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditorModel {
    public enum TileType {
        NORMAL, OBSTACLE, SLOW, POISON, SPAWNER, EXIT
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
        rootAssets = new File("assets").getAbsoluteFile();
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

    public String getRelativeAssetPath(File assetFile) {
        try {
            String rootPath = rootAssets.getCanonicalPath(); // chemin absolu normalisé
            String filePath = assetFile.getCanonicalPath(); // idem pour le fichier
            if (filePath.startsWith(rootPath)) {
                String relative = filePath.substring(rootPath.length());
                relative = relative.replace(File.separatorChar, '/'); // unifie les séparateurs
                if (relative.startsWith("/")) {
                    relative = relative.substring(1);
                }
                return relative;
            } else {
                System.err.println("⚠️ Asset file not in root: " + filePath + " vs root: " + rootPath);
                return assetFile.getName(); // fallback
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la résolution du chemin relatif : " + e.getMessage());
            return assetFile.getName();
        }
    }


    public void setCellAsNormal(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 0, false);
    }

    public void setCellAsObstacle(StackPane cell) {
        updateTileProperty(cell, false, 1.0, 0, false);
    }

    public void setCellAsSlowZone(StackPane cell) {
        updateTileProperty(cell, true, 0.5, 0, false);
    }

    public void setCellAsPoison(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 10, false);
    }

    public void setCellAsSpawner(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 0, false);
        cell.getProperties().put("spawner", true);
    }

    public void setCellAsItem(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 0, false);
        cell.getProperties().put("item", true);
    }

    public void setCellAsExit(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 0, true);
    }

    private void updateTileProperty(StackPane cell, boolean passable, double speed, int damage, boolean isExit) {
        Tile tile = new Tile(passable, speed, damage, isExit);
        cell.getProperties().put("tile", tile);
    }

    public boolean isSpawnerCell(StackPane cell) {
        Object prop = cell.getProperties().get("spawner");
        return prop instanceof Boolean && (Boolean) prop;
    }

    public void markAsSpawner(StackPane cell) {
        cell.getProperties().put("spawner", true);
    }

    public void unmarkSpawner(StackPane cell) {
        cell.getProperties().remove("spawner");
    }

    public boolean isItemCell(StackPane cell) {
        Object prop = cell.getProperties().get("item");
        return prop instanceof Boolean && (Boolean) prop;
    }

    public void markAsItem(StackPane cell) {
        cell.getProperties().put("item", true);
    }

    public void unmarkItem(StackPane cell) {
        cell.getProperties().remove("item");
    }
}
