package fr.beyondtime.controller;

import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.entities.Item;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.entities.HeroView;
import fr.beyondtime.view.components.HUDView;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.util.List;

public class HeroController {
<<<<<<< HEAD
    private Hero hero; // Modèle du héros
    private HeroView heroView; // Vue graphique du héros
    private Runnable onUpdateCallback; // Callback pour notifier la vue après un déplacement
=======
    private Hero hero;
    private HeroView heroView;
    private Runnable onUpdateCallback;
    private HUDView hudView;
>>>>>>> 793e9b7a269256777aa00a4b133bc73b76aa62cd

    private double speed = 2; // Vitesse de déplacement en pixels

    public static final int HERO_WIDTH = 32;
    public static final int HERO_HEIGHT = 32;

    // États des touches directionnelles
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Carte et taille des cellules pour les collisions
    private GridPane mapGrid;
    private int cellSize;

    /**
<<<<<<< HEAD
     * Constructeur du contrôleur du héros
=======
     * Constructs a HeroController.
     *
     * @param hero     The Hero data model.
     * @param heroView The Hero visual representation.
     * @param mapGrid  The GridPane representing the map for collision checks.
     * @param cellSize The size of each cell in the mapGrid.
     * @param hudView  The HUDView for updating the selected slot.
>>>>>>> 793e9b7a269256777aa00a4b133bc73b76aa62cd
     */
    public HeroController(Hero hero, HeroView heroView, GridPane mapGrid, int cellSize, HUDView hudView) {
        this.hero = hero;
        this.heroView = heroView;
        this.mapGrid = mapGrid;
        this.cellSize = cellSize;
<<<<<<< HEAD

        // Position initiale du héros : centre de la carte
=======
        this.hudView = hudView;
        
        // Initialiser la position du héros au centre de la carte
>>>>>>> 793e9b7a269256777aa00a4b133bc73b76aa62cd
        int centerCol = mapGrid.getColumnCount() / 2;
        int centerRow = mapGrid.getRowCount() / 2;
        double startX = centerCol * cellSize;
        double startY = centerRow * cellSize;

        hero.setPosition(startX, startY);
        heroView.setPosition(startX, startY);

        System.out.println("HeroController: Initial position - Col: " + centerCol + ", Row: " + centerRow);
        System.out.println("HeroController: World position - X: " + startX + ", Y: " + startY);

        // Boucle d'animation continue pour mise à jour du mouvement
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMovement();
            }
        }.start();
    }

    // Enregistre une fonction de rappel pour les mises à jour (ex : caméra)
    public void setOnUpdate(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    // Gère l'appui des touches directionnelles
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        System.out.println("Touche pressée : " + code);
        
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = true;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = true;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = true;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = true;
        
        // Gestion des touches numériques AZERTY pour l'inventaire
        if (code == KeyCode.DIGIT1) {
            System.out.println("Touche 1 (&) détectée");
            useInventoryItem(0);
        }
        if (code == KeyCode.DIGIT2) {
            System.out.println("Touche 2 (é) détectée");
            useInventoryItem(1);
        }
        if (code == KeyCode.DIGIT3) {
            System.out.println("Touche 3 (\") détectée");
            useInventoryItem(2);
        }
        if (code == KeyCode.DIGIT4) {
            System.out.println("Touche 4 (') détectée");
            useInventoryItem(3);
        }
        if (code == KeyCode.DIGIT5) {
            System.out.println("Touche 5 (() détectée");
            useInventoryItem(4);
        }
    }

    /**
     * Utilise l'item à l'index spécifié dans l'inventaire du héros.
     * @param index L'index de l'item à utiliser (0-4)
     */
    private void useInventoryItem(int index) {
        System.out.println("Tentative d'utilisation du slot " + index);
        if (hero != null && hero.getBag() != null) {
            List<Item> items = hero.getBag().getItems();
            System.out.println("Contenu de l'inventaire : " + items.size() + " items");
            for (int i = 0; i < items.size(); i++) {
                System.out.println("Slot " + i + " : " + (items.get(i) != null ? items.get(i).getName() : "vide"));
            }
            
            if (index >= 0 && index < items.size()) {
                Item item = items.get(index);
                if (item != null) {
                    System.out.println("Item trouvé dans le slot " + index + " : " + item.getName());
                    item.use(hero);
                    if (hudView != null) {
                        System.out.println("Appel de selectSlot pour le slot " + index);
                        hudView.selectSlot(index);
                    } else {
                        System.out.println("HUDView est null !");
                    }
                } else {
                    System.out.println("Aucun item dans le slot " + index);
                }
            } else {
                System.out.println("Index invalide ou pas d'items dans l'inventaire");
            }
        } else {
            System.out.println("Hero ou Bag est null");
        }
    }

    // Gère le relâchement des touches directionnelles
    public void handleKeyRelease(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = false;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = false;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = false;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = false;
    }

    /**
     * Met à jour la position du héros selon les touches appuyées et les collisions
     */
    private void updateMovement() {
        double nextWorldX = hero.getX();
        double nextWorldY = hero.getY();

        if (upPressed) nextWorldY -= speed;
        if (downPressed) nextWorldY += speed;
        if (leftPressed) nextWorldX -= speed;
        if (rightPressed) nextWorldX += speed;

        if (!checkCollision(nextWorldX, nextWorldY)) {
            hero.setPosition(nextWorldX, nextWorldY);     // mise à jour modèle
            heroView.setPosition(nextWorldX, nextWorldY); // mise à jour vue

            if (onUpdateCallback != null) {
                onUpdateCallback.run(); // déclenche par exemple une mise à jour de caméra
            }
        }
    }

    /**
     * Vérifie s'il y a une collision à la position donnée (bords + tuiles bloquantes)
     */
    private boolean checkCollision(double nextWorldX, double nextWorldY) {
        double mapWidth = mapGrid.getColumnCount() * cellSize;
        double mapHeight = mapGrid.getRowCount() * cellSize;

        if (nextWorldX < 0 || nextWorldX + HERO_WIDTH >= mapWidth ||
            nextWorldY < 0 || nextWorldY + HERO_HEIGHT >= mapHeight) {
            System.out.println("Collision avec limite map: nextX=" + nextWorldX + ", nextY=" + nextWorldY);
            return true;
        }

        // Calcul des cellules occupées par le héros
        double epsilon = 0.0001;
        int leftCol = (int) (nextWorldX / cellSize);
        int rightCol = (int) ((nextWorldX + HERO_WIDTH - epsilon) / cellSize);
        int topRow = (int) (nextWorldY / cellSize);
        int bottomRow = (int) ((nextWorldY + HERO_HEIGHT - epsilon) / cellSize);

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (col >= 0 && col < mapGrid.getColumnCount() &&
                    row >= 0 && row < mapGrid.getRowCount()) {
                    if (isTileBlocked(col, row)) {
                        System.out.println("Collision avec tuile bloquante: col=" + col + ", row=" + row);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // Indique si une tuile est bloquante
    private boolean isTileBlocked(int col, int row) {
        Node node = getCellNodeAt(mapGrid, row, col);
        if (node instanceof StackPane) {
            StackPane cell = (StackPane) node;
            Object tileObj = cell.getProperties().get("tile");
            if (tileObj instanceof Tile) {
                return !((Tile) tileObj).isPassable();
            }
        }
        return false;
    }

    // Récupère la cellule de la grille aux coordonnées spécifiées
    private Node getCellNodeAt(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);
            if (nodeRow == null) nodeRow = 0;
            if (nodeCol == null) nodeCol = 0;
            if (nodeRow == row && nodeCol == col) {
                return node;
            }
        }
        return null;
    }

    // Expose la position X du modèle pour la caméra
    public double getWorldX() {
        return hero.getX();
    }

    // Expose la position Y du modèle pour la caméra
    public double getWorldY() {
        return hero.getY();
    }
}