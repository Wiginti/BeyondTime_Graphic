package fr.beyondtime.util;

import fr.beyondtime.model.map.Tile;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class MapLoader {

    public static File[] getMapFilesForLevel(String levelName) {
        File saveDir = new File("saved_maps");
        if (!saveDir.exists()) {
            return new File[0];
        }
        String prefix = levelName.replaceAll("\\s+", "_");
        File[] matchingFiles = saveDir.listFiles((dir, name) -> name.startsWith(prefix));
        if (matchingFiles == null) {
            return new File[0];
        }
        Arrays.sort(matchingFiles, (f1, f2) ->
                Long.compare(extractTimestamp(f2.getName()), extractTimestamp(f1.getName()))
        );
        return matchingFiles;
    }

    public static GridPane loadMapFromFile(File file) {
        System.out.println("Loading map from file: " + file.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String firstLine = reader.readLine();
            if (firstLine == null) return null;
            firstLine = firstLine.trim();
            String[] dims = firstLine.split(",");
            int rows = Integer.parseInt(dims[0].trim());
            int cols = Integer.parseInt(dims[1].trim());
            System.out.println("Dimensions read: " + rows + "x" + cols);
            GridPane grid = new GridPane();
            grid.setHgap(0);
            grid.setVgap(0);
            grid.setPadding(new Insets(0));
            int cellSize = 50;
            for (int row = 0; row < rows; row++) {
                String line = reader.readLine();
                if (line == null) break;
                line = line.trim();
                String[] cellData = line.split(",", -1);
                for (int col = 0; col < cols; col++) {
                    StackPane cell = new StackPane();
                    cell.setPrefSize(cellSize, cellSize);
                    Rectangle background = new Rectangle(cellSize, cellSize);
                    background.setFill(Color.LIGHTGRAY);
                    background.setStroke(Color.BLACK);
                    cell.getChildren().add(background);

                    String cellValue = cellData[col].trim();
                    String assetPath = "";
                    boolean passable = true;
                    double slowdown = 1.0;

                    if (!cellValue.isEmpty()) {
                        // Format attendu : assetPath|passable;slowdownFactor
                        String[] parts = cellValue.split("\\|");
                        if (parts.length == 2) {
                            assetPath = parts[0];
                            String[] tileParts = parts[1].split(";");
                            if (tileParts.length == 2) {
                                passable = Boolean.parseBoolean(tileParts[0]);
                                slowdown = Double.parseDouble(tileParts[1]);
                            }
                        } else {
                            // Au cas où le format ne correspondrait pas, on traite toute la chaîne comme assetPath
                            assetPath = cellValue;
                        }
                    }

                    // Appliquer les propriétés du Tile sur la cellule
                    cell.getProperties().put("tile", new Tile(passable, slowdown));

                    // Si un asset est défini, le charger et l'afficher
                    if (!assetPath.isEmpty()) {
                        System.out.println("Loading asset at cell (" + row + "," + col + "): " + assetPath);
                        try {
                            Image image = new Image(MapLoader.class.getResourceAsStream(assetPath));
                            if (image.isError()) {
                                System.out.println("Error loading image: " + assetPath);
                            }
                            ImageView assetView = new ImageView(image);
                            assetView.setFitWidth(cellSize);
                            assetView.setFitHeight(cellSize);
                            cell.getChildren().add(assetView);
                            cell.setUserData(assetPath);
                        } catch (Exception ex) {
                            System.out.println("Exception loading asset: " + assetPath + " - " + ex.getMessage());
                        }
                    }

                    grid.add(cell, col, row);
                }
            }
            return grid;
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors du chargement de la map.");
            alert.showAndWait();
            return null;
        }
    }

    private static long extractTimestamp(String fileName) {
        try {
            int underscoreIndex = fileName.lastIndexOf('_');
            int dotIndex = fileName.lastIndexOf('.');
            if (underscoreIndex >= 0 && dotIndex > underscoreIndex) {
                String tsStr = fileName.substring(underscoreIndex + 1, dotIndex);
                return Long.parseLong(tsStr);
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }
}