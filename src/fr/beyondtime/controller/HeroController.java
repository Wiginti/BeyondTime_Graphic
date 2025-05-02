package fr.beyondtime.controller;

import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.entities.Item;
import fr.beyondtime.model.entities.Potion;
import fr.beyondtime.model.entities.Sword;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.components.HUDView;
import fr.beyondtime.view.entities.HeroView;
import fr.beyondtime.controller.game.GameController;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;

import java.util.List;
import java.util.ArrayList;

public class HeroController {
    private Hero hero;
    private HeroView heroView;
    private Runnable onUpdateCallback;
    private List<MonsterController> monsters;
    private HUDView hudView;
    private int selectedSlot = -1; // Aucun slot sélectionné par défaut
    private GameController gameController;

    private double speed = 2;

    public static final int HERO_WIDTH = 32;
    public static final int HERO_HEIGHT = 32;

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private GridPane mapGrid;
    private int cellSize;

    public HeroController(Hero hero, HeroView heroView, GridPane mapGrid, int cellSize, HUDView hudView) {
        this.hero = hero;
        this.heroView = heroView;
        this.mapGrid = mapGrid;
        this.cellSize = cellSize;
        this.hudView = hudView;
        this.monsters = new ArrayList<>();

        int centerCol = mapGrid.getColumnCount() / 2;
        int centerRow = mapGrid.getRowCount() / 2;
        double startX = centerCol * cellSize;
        double startY = centerRow * cellSize;

        hero.setPosition(startX, startY);
        heroView.setPosition(startX, startY);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMovement();
            }
        }.start();
    }

    public void setOnUpdate(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = true;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = true;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = true;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = true;

        // Sélection des slots avec les touches 1-5
        if (code == KeyCode.DIGIT1) selectSlot(0);
        if (code == KeyCode.DIGIT2) selectSlot(1);
        if (code == KeyCode.DIGIT3) selectSlot(2);
        if (code == KeyCode.DIGIT4) selectSlot(3);
        if (code == KeyCode.DIGIT5) selectSlot(4);

        // Utilisation de l'item sélectionné avec F
        if (code == KeyCode.F) useSelectedItem();
    }

    private void selectSlot(int index) {
        if (index >= 0 && index < 5) {
            selectedSlot = index;
            if (hudView != null) {
                hudView.selectSlot(index);
            }
            List<Item> items = hero.getBag().getItems();
            if (index < items.size()) {
                Item item = items.get(index);
                System.out.println("Slot " + (index + 1) + " sélectionné - Item: " + (item != null ? item.getName() : "vide"));
            } else {
                System.out.println("Slot " + (index + 1) + " sélectionné - vide");
            }
        }
    }

    private void useSelectedItem() {
        if (selectedSlot >= 0 && hero != null && hero.getBag() != null) {
            List<Item> items = hero.getBag().getItems();
            System.out.println("Tentative d'utilisation du slot " + (selectedSlot + 1));
            if (selectedSlot < items.size()) {
                Item item = items.get(selectedSlot);
                if (item != null) {
                    System.out.println("Utilisation de l'item: " + item.getName());
                    
                    if (item instanceof Potion) {
                        // Pour les potions, on les utilise et on les retire
                        item.use(hero);
                        hero.getBag().removeItem(item);
                        System.out.println("Potion utilisée et retirée de l'inventaire");
                        updateHUD();
                    } else if (item instanceof Sword) {
                        // Pour l'épée, on la sélectionne/désélectionne
                        item.use(hero);
                        // Mise à jour du HUD pour afficher l'état de sélection
                        updateHUD();
                        // Mise à jour du texte d'état de l'épée
                        hudView.updateSwordStatus(((Sword) item).isSelected());
                    }
                } else {
                    System.out.println("Le slot " + (selectedSlot + 1) + " est vide");
                }
            } else {
                System.out.println("Le slot " + (selectedSlot + 1) + " est hors limites");
            }
        } else {
            System.out.println("Aucun slot sélectionné ou héros/inventaire non initialisé");
        }
    }

    private void updateHUD() {
        if (hudView != null) {
        	
            double currentHealth = hero.getHealth();
            hudView.updateHealth(currentHealth);
        	
            List<Item> items = hero.getBag().getItems();
            List<Image> itemImages = new ArrayList<>();
            for (Item item : items) {
                try {
                    Image image = new Image(item.getImagePath());
                    itemImages.add(image);
                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement de l'image pour " + item.getName());
                }
            }
            hudView.updateInventory(itemImages);
        }
    }

    public void handleKeyRelease(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = false;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = false;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = false;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = false;
    }

    private void updateMovement() {
        double nextWorldX = hero.getX();
        double nextWorldY = hero.getY();

        if (upPressed) {
            nextWorldY -= speed;
            heroView.updateSprite("up");
        }
        if (downPressed) {
            nextWorldY += speed;
            heroView.updateSprite("down");
        }
        if (leftPressed) {
            nextWorldX -= speed;
            heroView.updateSprite("left");
        }
        if (rightPressed) {
            nextWorldX += speed;
            heroView.updateSprite("right");
        }

        if (!checkCollision(nextWorldX, nextWorldY)) {
            hero.setPosition(nextWorldX, nextWorldY);
            heroView.setPosition(nextWorldX, nextWorldY);
            if (onUpdateCallback != null) onUpdateCallback.run();
        }
    }

    private boolean checkCollision(double nextWorldX, double nextWorldY) {
        double mapWidth = mapGrid.getColumnCount() * cellSize;
        double mapHeight = mapGrid.getRowCount() * cellSize;

        if (nextWorldX < 0 || nextWorldX + HERO_WIDTH >= mapWidth ||
            nextWorldY < 0 || nextWorldY + HERO_HEIGHT >= mapHeight) {
            return true;
        }

        double epsilon = 0.0001;
        int leftCol = (int) (nextWorldX / cellSize);
        int rightCol = (int) ((nextWorldX + HERO_WIDTH - epsilon) / cellSize);
        int topRow = (int) (nextWorldY / cellSize);
        int bottomRow = (int) ((nextWorldY + HERO_HEIGHT - epsilon) / cellSize);

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (col >= 0 && col < mapGrid.getColumnCount() &&
                    row >= 0 && row < mapGrid.getRowCount()) {
                    if (isTileBlocked(col, row)) return true;
                }
            }
        }
        return false;
    }

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

    private Node getCellNodeAt(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);
            if (nodeRow == null) nodeRow = 0;
            if (nodeCol == null) nodeCol = 0;
            if (nodeRow == row && nodeCol == col) return node;
        }
        return null;
    }

    public double getWorldX() {
        return hero.getX();
    }

    public double getWorldY() {
        return hero.getY();
    }
    
    public void takeDamage(int amount) {
        hero.removeHealth(amount);
        double proportion = (double) hero.getHealth() / (double) Hero.DEFAULT_HEALTH;
        double heartValue = proportion * 10; // car MAX_HEARTS_DISPLAY = 10
        hudView.updateHealth(heartValue);
        heroView.playHitEffect();
    }
    
    public Hero getHero() {
        return this.hero;
    }
    
    public void setMonsters(List<MonsterController> monsters) {
        this.monsters = monsters;
    }

    public void attackNearbyMonsters() {
        for (MonsterController monsterController : monsters) {
            double dx = monsterController.getX() - hero.getX();
            double dy = monsterController.getY() - hero.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < 60) {
                int damage = hero.getDamage(); // ou plus si épée équipée
                monsterController.takeDamage(damage);
            }
        }
    }
    
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public GameController getGameController() {
        return gameController;
    }
}