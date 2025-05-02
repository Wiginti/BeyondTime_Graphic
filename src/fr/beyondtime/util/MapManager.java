package fr.beyondtime.util;

import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.screens.GameScreen;
import fr.beyondtime.model.game.GameState;
import fr.beyondtime.model.map.GameMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;

public class MapManager {

    private static final String SAVE_DIR = "saved_map";

    public static File saveMap(GridPane grid, int rows, int columns, String levelName) {
        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        String fileName = levelName + "_" + System.currentTimeMillis() + ".map";
        File saveFile = new File(saveDir, fileName);
        return saveMap(grid, rows, columns, saveFile);
    }

    public static File saveMap(GridPane grid, int rows, int columns, File saveFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile))) {
            writer.println(rows + "," + columns);
            
            System.out.println("[DEBUG] Sauvegarde de la carte: " + saveFile.getName());
            System.out.println("[DEBUG] Dimensions: " + rows + "x" + columns);
            
            for (int row = 0; row < rows; row++) {
                writer.print("|");
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
                        boolean isPassable = tile != null && tile.isPassable();
                        double slowdown = tile != null ? tile.getSlowdownFactor() : 1.0;
                        int damage = tile != null ? tile.getDamage() : 0;
                        boolean isSpawner = cell.getProperties().get("isSpawner") != null;
                        boolean isExit = tile != null && tile.isExit();
                        boolean isStart = tile != null && tile.isStart();
                        
                        // Debug pour les cases avec des dégâts
                        if (damage > 0) {
                            System.out.println("[DEBUG] Sauvegarde case poison - Position: (" + row + "," + col + 
                                            "), Dégâts: " + damage + 
                                            ", Slowdown: " + slowdown);
                        }
                        
                        // Récupérer le chemin de l'image s'il existe
                        String imagePath = (String) cell.getUserData();
                        
                        writer.print(isPassable + ";" + slowdown + ";" + damage + ";" + 
                                   isSpawner + ";" + isExit + ";" + isStart +
                                   (imagePath != null ? ";" + imagePath : ""));
                    } else {
                        writer.print("true;1.0;0;false;false;false");
                    }
                    writer.print("|");
                }
                writer.println();
            }
            System.out.println("Carte sauvegardée avec succès dans : " + saveFile.getAbsolutePath());
            return saveFile;
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la carte : " + e.getMessage());
            return null;
        }
    }

    public static GridPane loadMapFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Lire les dimensions
            String[] dimensions = reader.readLine().split(",");
            int rows = Integer.parseInt(dimensions[0]);
            int cols = Integer.parseInt(dimensions[1]);
            
            GridPane grid = new GridPane();
            grid.setHgap(0);
            grid.setVgap(0);
            
            // Lire chaque ligne
            for (int row = 0; row < rows; row++) {
                String line = reader.readLine();
                String[] cells = line.substring(1, line.length() - 1).split("\\|");
                
                for (int col = 0; col < cols; col++) {
                    StackPane cell = new StackPane();
                    cell.setPrefSize(50, 50);
                    
                    // Fond de base
                    Rectangle background = new Rectangle(50, 50);
                    background.setFill(Color.LIGHTGRAY);
                    background.setStroke(Color.BLACK);
                    cell.getChildren().add(background);
                    
                    // Charger les propriétés de la tuile
                    String[] properties = cells[col].split(";");
                    boolean passable = Boolean.parseBoolean(properties[0]);
                    double slowdown = Double.parseDouble(properties[1]);
                    int damage = Integer.parseInt(properties[2]);
                    boolean isSpawner = Boolean.parseBoolean(properties[3]);
                    boolean isExit = Boolean.parseBoolean(properties[4]);
                    boolean isStart = Boolean.parseBoolean(properties[5]);
                    
                    // Créer la tuile avec les propriétés
                    Tile tile = new Tile(passable, slowdown, damage, isExit, isStart);
                    cell.getProperties().put("tile", tile);
                    
                    // Ajouter l'image de fond si spécifiée
                    if (properties.length > 6 && !properties[6].isEmpty()) {
                        try {
                            String imagePath = properties[6];
                            Image img = null;
                            File localFile = new File("assets/" + imagePath);
                            
                            if (localFile.exists()) {
                                img = new Image(localFile.toURI().toString());
                            } else {
                                String resourcePath = "/fr/beyondtime/resources/" + imagePath;
                                img = new Image(MapManager.class.getResourceAsStream(resourcePath));
                            }
                            
                            if (img != null && !img.isError()) {
                                ImageView iv = new ImageView(img);
                                iv.setFitWidth(50);
                                iv.setFitHeight(50);
                                cell.getChildren().add(iv);
                                cell.setUserData(imagePath);
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement de l'image: " + properties[6]);
                            e.printStackTrace();
                        }
                    }
                    
                    // Ajouter des overlays visuels pour les effets spéciaux
                    if (damage > 0) {  // Case de poison
                        Rectangle poisonOverlay = new Rectangle(50, 50);
                        poisonOverlay.setFill(Color.PURPLE);
                        poisonOverlay.setOpacity(0.3);
                        cell.getChildren().add(poisonOverlay);
                    }
                    if (slowdown < 1.0) {  // Case de ralentissement
                        Rectangle slowOverlay = new Rectangle(50, 50);
                        slowOverlay.setFill(Color.BLUE);
                        slowOverlay.setOpacity(0.3);
                        cell.getChildren().add(slowOverlay);
                    }
                    if (isExit) {  // Case de sortie
                        Rectangle exitOverlay = new Rectangle(50, 50);
                        exitOverlay.setFill(Color.GREEN);
                        exitOverlay.setOpacity(0.3);
                        cell.getChildren().add(exitOverlay);
                        
                        // Ajouter un symbole de portail/sortie
                        try {
                            Image exitImage = new Image("file:assets/portail.png");
                            ImageView exitView = new ImageView(exitImage);
                            exitView.setFitWidth(40);  // Un peu plus petit que la case
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
                    
                    grid.add(cell, col, row);
                }
            }
            
            return grid;
            
        } catch (IOException | NumberFormatException e) {
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

        Stage mapSelectStage = new Stage();
        mapSelectStage.initModality(Modality.APPLICATION_MODAL);
        mapSelectStage.initOwner(stage);
        mapSelectStage.setTitle("Sélection de map - " + levelName);

        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #1a2a3d, #2a3a4d);
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 2;
            """);

        Text titleText = new Text("Sélectionnez une map pour " + levelName);
        titleText.setStyle("""
            -fx-fill: #e0e0e0;
            -fx-font-size: 24;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, #000000, 2, 0.3, 0, 1);
            """);

        VBox mapsBox = new VBox(20);
        mapsBox.setAlignment(Pos.CENTER);
        mapsBox.setPadding(new Insets(20, 0, 20, 0));
        mapsBox.setStyle("-fx-background-color: rgba(26, 42, 61, 0.6); -fx-background-radius: 5;");
        mapsBox.setMinWidth(400);

        for (File map : maps) {
            String mapName = map.getName();
            Button mapButton = createMapButton(mapName);
            final File selectedMap = map;
            
            mapButton.setOnAction(e -> {
                GridPane grid = loadMapFromFile(selectedMap);
                if (grid != null) {
                    mapSelectStage.close();
                    GameState gameState = new GameState(levelName);
                    GameMap gameMap = new GameMap(grid);
                    gameState.setMap(gameMap);
                    new GameScreen(stage, gameState);
                }
            });
            
            mapsBox.getChildren().add(mapButton);
        }

        Button closeButton = new Button("Fermer");
        closeButton.setStyle("""
            -fx-background-color: #2a3a4d;
            -fx-text-fill: #e0e0e0;
            -fx-font-size: 16;
            -fx-padding: 10 20;
            -fx-background-radius: 5;
            -fx-border-radius: 5;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 1;
            -fx-cursor: hand;
            -fx-min-width: 120;
            """);

        final String baseCloseStyle = closeButton.getStyle();
        final String hoverCloseStyle = baseCloseStyle + "-fx-background-color: #3a4a5d;";

        closeButton.setOnMouseEntered(e -> closeButton.setStyle(hoverCloseStyle));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(baseCloseStyle));
        closeButton.setOnAction(e -> mapSelectStage.close());

        layout.getChildren().addAll(titleText, mapsBox, closeButton);

        Scene scene = new Scene(layout, 500, 600);
        scene.setFill(null);
        mapSelectStage.setScene(scene);
        mapSelectStage.show();
    }

    private static Button createMapButton(String text) {
        Button button = new Button(text);
        
        final String baseStyle = """
            -fx-background-color: #2a3a4d;
            -fx-text-fill: #e0e0e0;
            -fx-font-size: 18;
            -fx-padding: 15 30;
            -fx-min-width: 350;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-width: 2;
            -fx-border-color: #4a5a6d;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, #000000, 8, 0.4, 0, 0);
            """;

        button.setStyle(baseStyle);

        final String hoverStyle = baseStyle + """
            -fx-scale-x: 1.03;
            -fx-scale-y: 1.03;
            -fx-effect: dropshadow(gaussian, #000000, 15, 0.6, 0, 0);
            -fx-background-color: #3a4a5d;
            """;

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    private static File[] getMapFilesForLevel(String levelName) {
        File saveDir = new File("saved_map");
        if (!saveDir.exists()) return new File[0];
        
        String prefix;
        switch (levelName.toLowerCase()) {
            case "préhistoire":
            case "prehistoric":
            case "prehistoric era":
                prefix = "Préhistoire";
                break;
            case "égypte antique":
            case "ancient egypt":
            case "egypt":
                prefix = "Égypte Antique";
                break;
            case "2nde guerre mondiale":
            case "world war 2":
            case "ww2":
                prefix = "2nde Guerre Mondiale";
                break;
            default:
                prefix = levelName;
        }
        
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

    private static void addOverlay(StackPane cell, Color color) {
        Rectangle overlay = new Rectangle(50, 50);
        overlay.setFill(color);
        overlay.setOpacity(0.4);
        cell.getChildren().add(overlay);
    }
} 
