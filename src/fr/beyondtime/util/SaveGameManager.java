package fr.beyondtime.util;

import fr.beyondtime.model.game.GameState;
import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.map.GameMap;
import fr.beyondtime.model.map.Tile;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manages saving and loading game states.
 * Handles serialization of game data including hero stats, inventory, and map state.
 */
public class SaveGameManager {
    private static final String SAVE_DIR = "game_saves";
    private static final String SAVE_EXTENSION = ".sav";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /**
     * Creates a new save file with the current game state.
     * @param gameState The current game state to save
     * @param saveName The name to give this save (optional, will use timestamp if null)
     * @return The path to the created save file, or null if saving failed
     */
    public static Path saveGame(GameState gameState, String saveName) {
        try {
            System.out.println("[DEBUG] Début de la sauvegarde...");
            
            // Create saves directory if it doesn't exist
            Path saveDir = Paths.get(SAVE_DIR);
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
                System.out.println("[DEBUG] Création du répertoire de sauvegarde: " + saveDir);
            }

            // Generate filename
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String filename = (saveName != null ? saveName + "_" : "") + timestamp + SAVE_EXTENSION;
            Path savePath = saveDir.resolve(filename);
            System.out.println("[DEBUG] Fichier de sauvegarde: " + savePath);

            // Write game state to file
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(savePath))) {
                // Write game metadata
                System.out.println("[DEBUG] Sauvegarde des métadonnées...");
                writer.println("[METADATA]");
                writer.println("level=" + gameState.getCurrentLevel());
                writer.println("timestamp=" + timestamp);
                writer.println();

                // Write hero data
                System.out.println("[DEBUG] Sauvegarde des données du héros...");
                writer.println("[HERO_DATA]");
                Hero hero = gameState.getHero();
                writer.println("health=" + hero.getHealth());
                writer.println("x=" + hero.getX());
                writer.println("y=" + hero.getY());
                writer.println();

                // Write map data
                System.out.println("[DEBUG] Sauvegarde de la carte...");
                writer.println("[MAP_DATA]");
                GameMap map = gameState.getMap();
                GridPane mapGrid = map.getMapGrid();
                int rows = (int) mapGrid.getRowCount();
                int cols = (int) mapGrid.getColumnCount();
                System.out.println("[DEBUG] Dimensions de la carte: " + rows + "x" + cols);
                writer.println("rows=" + rows);
                writer.println("columns=" + cols);

                // Save each tile's data
                int tileCount = 0;
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        StackPane cell = null;
                        for (javafx.scene.Node node : mapGrid.getChildren()) {
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
                            tileCount++;
                            Tile tile = (Tile) cell.getProperties().get("tile");
                            boolean isPassable = tile != null && tile.isPassable();
                            double slowdown = tile != null ? tile.getSlowdownFactor() : 1.0;
                            int damage = tile != null ? tile.getDamage() : 0;
                            boolean isSpawner = cell.getProperties().get("isSpawner") != null;
                            boolean isExit = tile != null && tile.isExit();
                            boolean isStart = tile != null && tile.isStart();
                            String imagePath = (String) cell.getUserData();
                            writer.println(row + "," + col + ":" + 
                                         isPassable + ";" + 
                                         slowdown + ";" + 
                                         damage + ";" + 
                                         isSpawner + ";" + 
                                         isExit + ";" + 
                                         isStart + ";" +
                                         (imagePath != null ? imagePath : ""));
                        }
                    }
                }
                System.out.println("[DEBUG] Nombre de tuiles sauvegardées: " + tileCount);

                System.out.println("[DEBUG] Sauvegarde terminée avec succès!");
                return savePath;
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads a game state from a save file.
     * @param savePath Path to the save file
     * @return The loaded GameState, or null if loading failed
     */
    public static GameState loadGame(Path savePath) {
        try {
            System.out.println("[DEBUG] Début du chargement de la sauvegarde: " + savePath);
            
            Properties props = new Properties();
            Map<String, String> sections = new HashMap<>();
            String currentSection = null;
            StringBuilder sectionContent = new StringBuilder();

            // Read file and separate into sections
            System.out.println("[DEBUG] Lecture du fichier de sauvegarde...");
            List<String> fileLines = Files.readAllLines(savePath);
            System.out.println("[DEBUG] Nombre de lignes lues: " + fileLines.size());
            
            for (String line : fileLines) {
                if (line.trim().isEmpty()) continue;
                
                if (line.startsWith("[") && line.endsWith("]")) {
                    if (currentSection != null) {
                        sections.put(currentSection, sectionContent.toString());
                        System.out.println("[DEBUG] Section lue: " + currentSection);
                        sectionContent.setLength(0);
                    }
                    currentSection = line.substring(1, line.length() - 1);
                } else {
                    sectionContent.append(line).append("\n");
                }
            }
            if (currentSection != null) {
                sections.put(currentSection, sectionContent.toString());
                System.out.println("[DEBUG] Dernière section lue: " + currentSection);
            }

            // Parse metadata section
            System.out.println("[DEBUG] Chargement des métadonnées...");
            props.load(new StringReader(sections.get("METADATA")));
            String level = props.getProperty("level");
            System.out.println("[DEBUG] Niveau chargé: " + level);
            
            // Create new game state
            GameState gameState = new GameState(level);
            
            // Parse hero data
            System.out.println("[DEBUG] Chargement des données du héros...");
            props.clear();
            props.load(new StringReader(sections.get("HERO_DATA")));
            Hero hero = gameState.getHero();
            hero.setHealth(Integer.parseInt(props.getProperty("health")));
            hero.setPosition(
                Double.parseDouble(props.getProperty("x")),
                Double.parseDouble(props.getProperty("y"))
            );
            System.out.println("[DEBUG] Position du héros: " + props.getProperty("x") + "," + props.getProperty("y"));

            // Parse map data
            System.out.println("[DEBUG] Chargement de la carte...");
            String[] mapLines = sections.get("MAP_DATA").split("\n");
            props.clear();
            props.load(new StringReader(mapLines[0] + "\n" + mapLines[1]));
            int rows = Integer.parseInt(props.getProperty("rows"));
            int cols = Integer.parseInt(props.getProperty("columns"));
            System.out.println("[DEBUG] Dimensions de la carte: " + rows + "x" + cols);

            // Create and initialize the map
            GameMap map = new GameMap(rows, cols, 50); // 50 is the cell size
            GridPane mapGrid = map.getMapGrid();
            System.out.println("[DEBUG] Nouvelle grille créée");

            // Load tile data
            int tileCount = 0;
            for (int i = 2; i < mapLines.length; i++) {
                String line = mapLines[i].trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(":");
                String[] coords = parts[0].split(",");
                String[] tileProperties = parts[1].split(";");

                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);

                boolean isPassable = Boolean.parseBoolean(tileProperties[0]);
                double slowdown = Double.parseDouble(tileProperties[1]);
                int damage = Integer.parseInt(tileProperties[2]);
                boolean isSpawner = Boolean.parseBoolean(tileProperties[3]);
                boolean isExit = Boolean.parseBoolean(tileProperties[4]);
                boolean isStart = Boolean.parseBoolean(tileProperties[5]);
                String imagePath = tileProperties.length > 6 ? tileProperties[6] : null;

                StackPane cell = new StackPane();
                cell.setPrefSize(50, 50);

                // Fond de base
                Rectangle background = new Rectangle(50, 50);
                background.setFill(Color.LIGHTGRAY);
                background.setStroke(Color.BLACK);
                cell.getChildren().add(background);

                // Créer la tuile
                Tile tile = new Tile(isPassable, slowdown, damage, isExit, isStart);
                cell.getProperties().put("tile", tile);

                // Charger l'image si elle existe
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        Image img = null;
                        File localFile = new File("assets/" + imagePath);
                        
                        if (localFile.exists()) {
                            img = new Image(localFile.toURI().toString());
                        } else {
                            String resourcePath = "/fr/beyondtime/resources/" + imagePath;
                            img = new Image(SaveGameManager.class.getResourceAsStream(resourcePath));
                        }
                        
                        if (img != null && !img.isError()) {
                            ImageView iv = new ImageView(img);
                            iv.setFitWidth(50);
                            iv.setFitHeight(50);
                            cell.getChildren().add(iv);
                            cell.setUserData(imagePath);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image: " + imagePath);
                        e.printStackTrace();
                    }
                }

                // Ajouter des overlays visuels
                if (damage > 0) {
                    Rectangle poisonOverlay = new Rectangle(50, 50);
                    poisonOverlay.setFill(Color.PURPLE);
                    poisonOverlay.setOpacity(0.3);
                    cell.getChildren().add(poisonOverlay);
                }
                if (slowdown < 1.0) {
                    Rectangle slowOverlay = new Rectangle(50, 50);
                    slowOverlay.setFill(Color.BLUE);
                    slowOverlay.setOpacity(0.3);
                    cell.getChildren().add(slowOverlay);
                }
                if (isExit) {
                    Rectangle exitOverlay = new Rectangle(50, 50);
                    exitOverlay.setFill(Color.GREEN);
                    exitOverlay.setOpacity(0.3);
                    cell.getChildren().add(exitOverlay);
                    
                    try {
                        Image exitImage = new Image("file:assets/portail.png");
                        ImageView exitView = new ImageView(exitImage);
                        exitView.setFitWidth(40);
                        exitView.setFitHeight(40);
                        exitView.setPreserveRatio(true);
                        cell.getChildren().add(exitView);
                    } catch (Exception e) {
                        System.err.println("Impossible de charger l'image du portail");
                    }
                }

                if (isSpawner) {
                    cell.getProperties().put("isSpawner", true);
                }

                mapGrid.add(cell, col, row);
                tileCount++;
            }
            System.out.println("[DEBUG] Nombre de tuiles chargées: " + tileCount);

            gameState.setMap(map);
            System.out.println("[DEBUG] Carte associée au GameState");

            // Note: Les mobs seront réinitialisés dans GameScreen lors de l'initialisation
            System.out.println("[DEBUG] Note: Les mobs seront réinitialisés lors de l'initialisation de GameScreen");

            System.out.println("[DEBUG] Chargement terminé avec succès!");
            
            return gameState;
        } catch (IOException e) {
            System.err.println("[ERROR] Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lists all available save files.
     * @return List of save file paths, sorted by timestamp (newest first)
     */
    public static List<Path> listSaves() {
        try {
            Path saveDir = Paths.get(SAVE_DIR);
            if (!Files.exists(saveDir)) {
                return new ArrayList<>();
            }

            return Files.list(saveDir)
                .filter(path -> path.toString().endsWith(SAVE_EXTENSION))
                .sorted((p1, p2) -> {
                    try {
                        return Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1));
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        } catch (IOException e) {
            System.err.println("Error listing saves: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Deletes a save file.
     * @param savePath Path to the save file to delete
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteSave(Path savePath) {
        try {
            return Files.deleteIfExists(savePath);
        } catch (IOException e) {
            System.err.println("Error deleting save: " + e.getMessage());
            return false;
        }
    }
} 