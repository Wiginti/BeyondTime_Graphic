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
import java.nio.file.Files;

public class MapManager {

    private static final String SAVE_DIR = "saved_map";

    public static void saveMap(GridPane grid, int rows, int columns, String levelName) {
        try {
            File saveDir = new File(SAVE_DIR);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = levelName + "_" + timestamp + ".map";
            File file = new File(saveDir, fileName);

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(rows + "," + columns);

                for (int row = 0; row < rows; row++) {
                    StringBuilder line = new StringBuilder();
                    for (int col = 0; col < columns; col++) {
                        StackPane cell = null;
                        for (Node node : grid.getChildren()) {
                            Integer nodeRow = GridPane.getRowIndex(node);
                            Integer nodeCol = GridPane.getColumnIndex(node);
                            if (nodeRow == null) nodeRow = 0;
                            if (nodeCol == null) nodeCol = 0;
                            if (nodeRow == row && nodeCol == col && node instanceof StackPane) {
                                cell = (StackPane) node;
                                break;
                            }
                        }

                        if (cell != null) {
                            Tile tile = (Tile) cell.getProperties().get("tile");
                            String imagePath = (String) cell.getUserData();
                            boolean isSpawner = cell.getProperties().get("isSpawner") != null && (boolean) cell.getProperties().get("isSpawner");
                            boolean isExit = cell.getProperties().get("isExit") != null && (boolean) cell.getProperties().get("isExit");
                            
                            line.append("|").append(tile.isPassable()).append(";")
                                .append(tile.getSlowdownFactor()).append(";")
                                .append(isSpawner).append(";")
                                .append(isExit);
                            
                            if (imagePath != null) {
                                line.append(";").append(imagePath);
                            }
                        } else {
                            line.append("|true;1.0;false;false"); // Valeurs par défaut
                        }
                    }
                    writer.println(line.toString());
                }
            }

            System.out.println("Carte sauvegardée avec succès dans : " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la sauvegarde de la carte : " + e.getMessage());
        }
    }

    public static GridPane loadMapFromFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) return null;

            String[] dimensions = lines.get(0).split(",");
            int rows = Integer.parseInt(dimensions[0]);
            int columns = Integer.parseInt(dimensions[1]);

            GridPane grid = new GridPane();
            grid.setHgap(0);
            grid.setVgap(0);

            for (int row = 0; row < rows; row++) {
                String[] cells = lines.get(row + 1).split("\\|");
                for (int col = 0; col < columns; col++) {
                    if (col + 1 < cells.length) {
                        String[] properties = cells[col + 1].split(";");
                        boolean passable = Boolean.parseBoolean(properties[0]);
                        double slowdown = Double.parseDouble(properties[1]);
                        boolean isSpawner = properties.length > 2 && Boolean.parseBoolean(properties[2]);
                        boolean isExit = properties.length > 3 && Boolean.parseBoolean(properties[3]);
                        String imagePath = properties.length > 4 ? properties[4] : null;

                        StackPane cell = new StackPane();
                        cell.setPrefSize(50, 50);

                        Rectangle background = new Rectangle(50, 50);
                        background.setFill(Color.LIGHTGRAY);
                        background.setStroke(Color.BLACK);
                        cell.getChildren().add(background);

                        if (imagePath != null) {
                            try {
                                Image image = new Image(imagePath);
                                ImageView imageView = new ImageView(image);
                                imageView.setFitWidth(50);
                                imageView.setFitHeight(50);
                                cell.getChildren().add(imageView);
                                cell.setUserData(imagePath);
                            } catch (Exception e) {
                                System.err.println("Erreur lors du chargement de l'image : " + imagePath);
                            }
                        }

                        Tile tile = new Tile(passable, slowdown, 0, isExit);
                        cell.getProperties().put("tile", tile);
                        
                        if (isSpawner) {
                            cell.getProperties().put("isSpawner", true);
                        }
                        
                        if (isExit) {
                            cell.getProperties().put("isExit", true);
                        }

                        grid.add(cell, col, row);
                    }
                }
            }

            return grid;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la carte : " + e.getMessage());
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
                    GameState gameState = new GameState(levelName);
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
        
        String prefix = levelName;
        
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
