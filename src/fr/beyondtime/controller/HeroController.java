package fr.beyondtime.controller;

import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.entities.Item;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.components.HUDView;
import fr.beyondtime.view.entities.HeroView;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.List;

public class HeroController {
    private Hero hero;
    private HeroView heroView;
    private Runnable onUpdateCallback;
    private HUDView hudView;

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

        if (code == KeyCode.DIGIT1) useInventoryItem(0);
        if (code == KeyCode.DIGIT2) useInventoryItem(1);
        if (code == KeyCode.DIGIT3) useInventoryItem(2);
        if (code == KeyCode.DIGIT4) useInventoryItem(3);
        if (code == KeyCode.DIGIT5) useInventoryItem(4);
    }

    private void useInventoryItem(int index) {
        if (hero != null && hero.getBag() != null) {
            List<Item> items = hero.getBag().getItems();
            if (index >= 0 && index < items.size()) {
                Item item = items.get(index);
                if (item != null) {
                    item.use(hero);
                    if (hudView != null) {
                        hudView.selectSlot(index);
                    }
                }
            }
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

        if (upPressed) nextWorldY -= speed;
        if (downPressed) nextWorldY += speed;
        if (leftPressed) nextWorldX -= speed;
        if (rightPressed) nextWorldX += speed;

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
}