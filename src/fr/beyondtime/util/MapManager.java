package fr.beyondtime.util;

import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.GameView;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapManager {

    private static final String SAVE_DIR = "saved_maps";

    public static void saveMap(GridPane grid, int rows, int columns, String levelName) {
        String[][] mapData = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                mapData[i][j] = "";
            }
        }
        for (Node node : grid.getChildren()) {
            if (node instanceof StackPane) {
                Integer col = GridPane.getColumnIndex(node);
                Integer row = GridPane.getRowIndex(node);
                if (col == null) col = 0;
                if (row == null) row = 0;
                Object assetData = ((StackPane) node).getUserData();
                String assetPath = assetData != null ? assetData.toString() : "";
                Tile tile = (Tile) ((StackPane) node).getProperties().get("tile");
                String tileData = (tile != null) ? (tile.isPassable() + ";" + tile.getSlowdownFactor()) : "true;1.0";
                mapData[row][col] = assetPath + "|" + tileData;
            }
        }
        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) saveDir.mkdirs();
        String fileName = levelName.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".map";
        File saveFile = new File(saveDir, fileName);
        try (PrintWriter out = new PrintWriter(new FileWriter(saveFile))) {
            out.println(rows + "," + columns);
            for (String[] row : mapData) {
                out.println(String.join(",", row));
            }
            showAlert("Sauvegarde réussie", "Map sauvegardée dans : " + saveFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Erreur de sauvegarde", "Impossible de sauvegarder la map.", Alert.AlertType.ERROR);
        }
    }

    public static GridPane loadMapFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String[] dims = reader.readLine().trim().split(",");
            int rows = Integer.parseInt(dims[0].trim());
            int cols = Integer.parseInt(dims[1].trim());
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(0));
            int cellSize = 50;
            for (int row = 0; row < rows; row++) {
                String[] cellData = reader.readLine().trim().split(",", -1);
                for (int col = 0; col < cols; col++) {
                    StackPane cell = new StackPane();
                    cell.setPrefSize(cellSize, cellSize);
                    Rectangle background = new Rectangle(cellSize, cellSize, Color.LIGHTGRAY);
                    background.setStroke(Color.BLACK);
                    cell.getChildren().add(background);
                    String[] parts = cellData[col].split("\\|");
                    String assetPath = parts.length > 0 ? parts[0] : "";
                    boolean passable = parts.length > 1 ? Boolean.parseBoolean(parts[1].split(";")[0]) : true;
                    double slowdown = parts.length > 1 ? Double.parseDouble(parts[1].split(";")[1]) : 1.0;
                    cell.getProperties().put("tile", new Tile(passable, slowdown));
                    if (!assetPath.isEmpty()) {
                        ImageView assetView = new ImageView(new Image(MapManager.class.getResourceAsStream(assetPath)));
                        assetView.setFitWidth(cellSize);
                        assetView.setFitHeight(cellSize);
                        cell.getChildren().add(assetView);
                        cell.setUserData(assetPath);
                    }
                    grid.add(cell, col, row);
                }
            }
            return grid;
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la map.", Alert.AlertType.ERROR);
            return null;
        }
    }

    public static void selectAndLoadMap(Stage stage, String levelName) {
        File[] maps = getMapFilesForLevel(levelName);
        if (maps.length == 0) {
            showAlert("Chargement", "Aucune map sauvegardée pour " + levelName, Alert.AlertType.INFORMATION);
            return;
        }
        List<String> choices = Arrays.stream(maps).map(File::getName).collect(Collectors.toList());
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Sélection de map");
        dialog.setHeaderText("Choisissez la map à ouvrir pour " + levelName);
        dialog.setContentText("Map :");
        dialog.showAndWait().ifPresent(selectedFileName -> {
            File selectedFile = Arrays.stream(maps).filter(file -> file.getName().equals(selectedFileName)).findFirst().orElse(null);
            if (selectedFile != null) {
                GridPane grid = loadMapFromFile(selectedFile);
                if (grid != null) {
                    new GameView(stage, grid);
                }
            }
        });
    }

    private static File[] getMapFilesForLevel(String levelName) {
        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) return new File[0];
        String prefix = levelName.replaceAll("\\s+", "_");
        File[] matchingFiles = saveDir.listFiles((dir, name) -> name.startsWith(prefix));
        if (matchingFiles == null) return new File[0];
        Arrays.sort(matchingFiles, (f1, f2) -> Long.compare(extractTimestamp(f2.getName()), extractTimestamp(f1.getName())));
        return matchingFiles;
    }

    private static long extractTimestamp(String fileName) {
        try {
            int underscoreIndex = fileName.lastIndexOf('_');
            int dotIndex = fileName.lastIndexOf('.');
            if (underscoreIndex >= 0 && dotIndex > underscoreIndex) {
                return Long.parseLong(fileName.substring(underscoreIndex + 1, dotIndex));
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private static void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
