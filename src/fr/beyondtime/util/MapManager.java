package fr.beyondtime.util;

import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.screens.GameScreen;
import fr.beyondtime.model.game.GameState;
import fr.beyondtime.model.map.GameMap;
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
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapManager {

    private static final String SAVE_DIR = "saved_map";

    public static void saveMap(GridPane grid, int rows, int columns, String levelName) {
        String[][] mapData = new String[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                mapData[i][j] = "";
            }
        }

        for (Node node : grid.getChildren()) {
            if (node instanceof StackPane cell) {
                Integer col = GridPane.getColumnIndex(cell);
                Integer row = GridPane.getRowIndex(cell);
                if (col == null) col = 0;
                if (row == null) row = 0;

                // Chemin de l'asset
                Object assetData = cell.getUserData();
                String assetPath = assetData != null ? assetData.toString() : "";

                // Données de la tuile
                Tile tile = (Tile) cell.getProperties().get("tile");
                String tileData = (tile != null) ? (tile.isPassable() + ";" + tile.getSlowdownFactor()) : "true;1.0";

                // Spawner ?
                boolean isSpawner = cell.getProperties().getOrDefault("isSpawner", false).equals(true);
                String extra = isSpawner ? "|SPAWNER" : "";

                mapData[row][col] = assetPath + "|" + tileData + extra;
            }
        }

        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                showAlert("Erreur de sauvegarde", "Impossible de créer le dossier : " + SAVE_DIR, Alert.AlertType.ERROR);
                return;
            }
        }

        String fileName = levelName.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".map";
        File saveFile = new File(saveDir, fileName);

        try (PrintWriter out = new PrintWriter(new FileWriter(saveFile))) {
            out.println(rows + "," + columns);
            for (String[] rowData : mapData) {
                out.println(String.join(",", rowData));
            }
            showAlert("Sauvegarde réussie", "Map sauvegardée dans : " + saveFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Erreur de sauvegarde", "Impossible de sauvegarder la map.", Alert.AlertType.ERROR);
            e.printStackTrace();
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

                    if (parts.length > 2 && parts[2].equals("SPAWNER")) {
                        cell.getProperties().put("isSpawner", true);
                    }

                    if (!assetPath.isEmpty()) {
                        File imgFile = new File("assets", assetPath);
                        if (imgFile.exists()) {
                            ImageView assetView = new ImageView(new Image(imgFile.toURI().toString()));
                            assetView.setFitWidth(cellSize);
                            assetView.setFitHeight(cellSize);
                            assetView.setPreserveRatio(true);
                            cell.getChildren().add(assetView);
                            cell.setUserData(assetPath);
                        }
                    }

                    grid.add(cell, col, row);
                }
            }
            return grid;
        } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            showAlert("Erreur", "Erreur lors du chargement de la map: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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
            File selectedFile = Arrays.stream(maps)
                .filter(file -> file.getName().equals(selectedFileName))
                .findFirst()
                .orElse(null);
            if (selectedFile != null) {
                GridPane grid = loadMapFromFile(selectedFile);
                if (grid != null) {
                    GameState gameState = new GameState();
                    GameMap gameMap = new GameMap(grid);
                    gameState.setMap(gameMap);
                    new GameScreen(stage, gameState);
                }
            }
        });
    }
    
    private static File[] getMapFilesForLevel(String levelName) {
        File saveDir = new File("saved_map");
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
