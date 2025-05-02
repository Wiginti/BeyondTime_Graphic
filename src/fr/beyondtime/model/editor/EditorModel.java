package fr.beyondtime.model.editor;

import fr.beyondtime.model.map.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EditorModel {
    public enum TileType {
        NORMAL, OBSTACLE, SLOW, POISON, SPAWNER, EXIT, START
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
    
    // Système d'historique pour l'annulation
    private static class EditorAction {
        final StackPane cell;
        final Tile previousTile;
        final boolean previousSpawner;
        final String previousImagePath;

        EditorAction(StackPane cell, Tile previousTile, boolean previousSpawner, String previousImagePath) {
            this.cell = cell;
            this.previousTile = previousTile;
            this.previousSpawner = previousSpawner;
            this.previousImagePath = previousImagePath;
        }
    }

    private Stack<EditorAction> actionHistory = new Stack<>();

    // Méthode pour sauvegarder l'état d'une cellule avant modification
    public void saveStateBeforeEdit(StackPane cell) {
        Tile currentTile = (Tile) cell.getProperties().get("tile");
        boolean isSpawner = cell.getProperties().get("isSpawner") != null;
        String imagePath = (String) cell.getUserData();
        
        actionHistory.push(new EditorAction(
            cell,
            currentTile != null ? new Tile(
                currentTile.isPassable(),
                currentTile.getSlowdownFactor(),
                currentTile.getDamage(),
                currentTile.isExit(),
                currentTile.isStart()
            ) : null,
            isSpawner,
            imagePath
        ));
    }

    // Méthode pour annuler la dernière action
    public boolean undoLastAction() {
        if (actionHistory.isEmpty()) {
            return false;
        }

        EditorAction lastAction = actionHistory.pop();
        StackPane cell = lastAction.cell;

        // Restaurer l'état précédent de la cellule
        cell.getChildren().clear();  // Effacer tous les éléments visuels

        // Recréer le fond de base
        Rectangle background = new Rectangle(cellSize, cellSize);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);

        // Restaurer l'asset s'il y en avait un
        if (lastAction.previousImagePath != null) {
            File assetFile = new File("assets", lastAction.previousImagePath);
            if (assetFile.exists()) {
                Image img = new Image(assetFile.toURI().toString(), cellSize, cellSize, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(cellSize);
                iv.setFitHeight(cellSize);
                cell.getChildren().add(iv);
            }
            cell.setUserData(lastAction.previousImagePath);
        } else {
            cell.setUserData(null);
        }

        // Restaurer les propriétés de la tuile
        if (lastAction.previousTile != null) {
            cell.getProperties().put("tile", lastAction.previousTile);
            
            // Ajouter l'overlay en fonction des propriétés de la tuile
            if (!lastAction.previousTile.isPassable()) {
                addOverlay(cell, Color.BLACK);
            } else if (lastAction.previousTile.getSlowdownFactor() < 1.0) {
                addOverlay(cell, Color.BLUE);
            } else if (lastAction.previousTile.getDamage() > 0) {
                addOverlay(cell, Color.PURPLE);
            } else if (lastAction.previousTile.isExit()) {
                addOverlay(cell, Color.GREEN);
            } else if (lastAction.previousTile.isStart()) {
                addOverlay(cell, Color.YELLOW);
            }
        } else {
            cell.getProperties().remove("tile");
            cell.getProperties().put("tile", new Tile(true, 1.0, 0));  // Ajouter une tuile par défaut
        }

        // Restaurer le statut de spawner
        if (lastAction.previousSpawner) {
            cell.getProperties().put("isSpawner", true);
            addOverlay(cell, Color.RED);
        } else {
            cell.getProperties().remove("isSpawner");
        }

        return true;
    }

    private void addOverlay(StackPane cell, Color color) {
        Rectangle overlay = new Rectangle(cellSize, cellSize);
        overlay.setFill(color);
        overlay.setOpacity(0.4);
        cell.getChildren().add(overlay);
    }

    // Méthode pour effacer l'historique
    public void clearHistory() {
        actionHistory.clear();
    }

    public int getActionHistorySize() {
        return actionHistory.size();
    }

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
        updateTileProperty(cell, true, 1.0, 0, false, false);
    }

    public void setCellAsObstacle(StackPane cell) {
        updateTileProperty(cell, false, 1.0, 0, false, false);
    }

    public void setCellAsSlowZone(StackPane cell) {
        updateTileProperty(cell, true, 0.5, 0, false, false);
    }

    public void setCellAsPoison(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 10, false, false);
    }

    public void setCellAsSpawner(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 0, false, false);
        cell.getProperties().put("spawner", true);
    }

    public void setCellAsItem(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 0, false, false);
        cell.getProperties().put("item", true);
    }

    public void setCellAsExit(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 0, true, false);
    }

    public void setCellAsStart(StackPane cell) {
        updateTileProperty(cell, true, 1.0, 0, false, true);
        cell.getProperties().put("isStart", true);
    }

    private void updateTileProperty(StackPane cell, boolean passable, double speed, int damage, boolean isExit, boolean isStart) {
        Tile tile = new Tile(passable, speed, damage, isExit, isStart);
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
