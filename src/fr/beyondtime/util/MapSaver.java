package fr.beyondtime.util;

import fr.beyondtime.model.map.Tile;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MapSaver {

    public static void saveMap(GridPane grid, int rows, int columns, String levelName) {
        String[][] mapData = new String[rows][columns];
        // Initialiser toutes les cases avec une chaîne vide
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                mapData[i][j] = "";
            }
        }
        // Pour chaque cellule de la grille, on sauvegarde le chemin de l'asset (le cas échéant) et les propriétés du Tile
        for (Node node : grid.getChildren()){
            if (node instanceof StackPane){
                Integer col = GridPane.getColumnIndex(node);
                Integer row = GridPane.getRowIndex(node);
                if (col == null) col = 0;
                if (row == null) row = 0;
                // Récupérer l'asset (userData)
                Object assetData = ((StackPane) node).getUserData();
                String assetPath = assetData != null ? assetData.toString() : "";
                // Récupérer les propriétés du Tile (passable et slowdownFactor)
                Tile tile = (Tile) ((StackPane) node).getProperties().get("tile");
                String tileData = (tile != null)
                        ? (tile.isPassable() + ";" + tile.getSlowdownFactor())
                        : "true;1.0";
                // Format : assetPath|passable;slowdownFactor
                mapData[row][col] = assetPath + "|" + tileData;
            }
        }
        File saveDir = new File("saved_maps");
        if (!saveDir.exists()){
            saveDir.mkdirs();
        }
        String fileName = levelName.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".map";
        File saveFile = new File(saveDir, fileName);
        try (PrintWriter out = new PrintWriter(new FileWriter(saveFile))) {
            // Écrire la première ligne avec les dimensions
            out.println(rows + "," + columns);
            // Écrire chaque ligne de la grille
            for (int i = 0; i < rows; i++){
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < columns; j++){
                    sb.append(mapData[i][j]);
                    if (j < columns - 1){
                        sb.append(",");
                    }
                }
                out.println(sb.toString());
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sauvegarde réussie");
            alert.setHeaderText(null);
            alert.setContentText("Map sauvegardée dans : " + saveFile.getAbsolutePath());
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de sauvegarde");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de sauvegarder la map.");
            alert.showAndWait();
        }
    }
}