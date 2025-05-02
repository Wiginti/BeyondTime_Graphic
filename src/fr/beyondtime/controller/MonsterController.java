package fr.beyondtime.controller;

import fr.beyondtime.model.entities.Monster;
import fr.beyondtime.view.entities.MonsterView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import fr.beyondtime.view.effects.DamagePopup;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import fr.beyondtime.model.map.Tile;

import java.util.Random;

public class MonsterController {

    private Monster monster;
    private final MonsterView monsterView;
    private final HeroController heroController;
    private final Group cameraGroup;
    private final GridPane mapGrid;
    private final int cellSize = 50;
    
    private double randomDx = 0;
    private double randomDy = 0;
    private long lastDirectionChange = 0;
    private static final long DIRECTION_CHANGE_INTERVAL = 2000; // en ms
    private static final double BASE_SPEED = 0.8; // Vitesse de base pour suivre le héros
    private static final double RANDOM_SPEED = 0.5; // Vitesse de base pour le mouvement aléatoire
    
    private final int mapRows;
    private final int mapCols;

    private Timeline attackLoop;
    private final Random random = new Random();

    public MonsterController(Monster monster, MonsterView monsterView, HeroController heroController, int mapRows, int mapCols) {
        this.monster = monster;
        this.monsterView = monsterView;
        this.heroController = heroController;
        this.mapRows = mapRows;
        this.mapCols = mapCols;
        this.cameraGroup = (Group) monsterView.getParent();
        this.mapGrid = heroController.getMapGrid(); // Accéder à la grille via le HeroController

        attackLoop = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            if (monster.isAlive() && isHeroNearby()) {
                heroController.takeDamage(monster.getDamage());
                if (heroController.getHero().getHealth() <= 0) {
                    attackLoop.stop();
                }
            }
        }));
        attackLoop.setCycleCount(Timeline.INDEFINITE);
        attackLoop.play();
    }

    public void startRespawnTimer(StackPane spawnCell, Group cameraGroup) {
        int delayMinutes = 1 + random.nextInt(15);
        int delayMillis = delayMinutes * 60 * 1000;

        Timeline respawn = new Timeline(new KeyFrame(Duration.millis(delayMillis), event -> {
            if (!monster.isAlive()) {
                monster = new Monster(monster.getSpawnX(), monster.getSpawnY(), 50, monster.getDamage());
                monster.setAlive(true);
                monsterView.show();
                monsterView.updatePosition(monster.getSpawnX() * 50, monster.getSpawnY() * 50);
                attackLoop.play();
            }
        }));
        respawn.setCycleCount(1);
        respawn.play();
    }

    private boolean isHeroNearby() {
        double mx = monster.getX();
        double my = monster.getY();
        double dx = mx - heroController.getWorldX();
        double dy = my - heroController.getWorldY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < 45; // Réduit la portée d'attaque du monstre (était 60 avant)
    }
    
    public void takeDamage(int damage) {
        monster.setHealth(monster.getHealth() - damage);
        
        // Créer et ajouter le popup de dégâts
        DamagePopup popup = new DamagePopup(damage, monster.getX(), monster.getY());
        cameraGroup.getChildren().add(popup);
        
        // Animation pour faire disparaître le popup
        Timeline fadeOutTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            cameraGroup.getChildren().remove(popup);
        }));
        fadeOutTimeline.setCycleCount(1);
        fadeOutTimeline.play();
        
        if (monster.getHealth() <= 0) {
            // Le monstre est mort
            monster.setAlive(false);
            monsterView.setVisible(false);
            attackLoop.stop();
            if (heroController != null && heroController.getGameController() != null) {
                heroController.getGameController().incrementMonstersKilled();
            }
        } else {
            // Effet visuel quand le monstre prend des dégâts mais ne meurt pas
            monsterView.playHitEffect();
        }
    }
    
    private Node getCellNodeAt(int row, int col) {
        for (Node node : mapGrid.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);
            if (nodeRow == null) nodeRow = 0;
            if (nodeCol == null) nodeCol = 0;
            if (nodeRow == row && nodeCol == col) return node;
        }
        return null;
    }

    private boolean isTileBlocked(double x, double y) {
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);
        
        Node node = getCellNodeAt(row, col);
        if (node instanceof StackPane) {
            StackPane cell = (StackPane) node;
            Object tileObj = cell.getProperties().get("tile");
            if (tileObj instanceof Tile) {
                return !((Tile) tileObj).isPassable();
            }
        }
        return false;
    }

    private double getTileSlowdown(double x, double y) {
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);
        
        Node node = getCellNodeAt(row, col);
        if (node instanceof StackPane) {
            StackPane cell = (StackPane) node;
            Object tileObj = cell.getProperties().get("tile");
            if (tileObj instanceof Tile) {
                return ((Tile) tileObj).getSlowdownFactor();
            }
        }
        return 1.0; // Pas de ralentissement par défaut
    }
    
    public void update() {
        if (!monster.isAlive()) return;

        double monsterX = monster.getX();
        double monsterY = monster.getY();
        double heroX = heroController.getWorldX();
        double heroY = heroController.getWorldY();

        double dx = heroX - monsterX;
        double dy = heroY - monsterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Appliquer le facteur de ralentissement de la case actuelle
        double slowdownFactor = getTileSlowdown(monsterX, monsterY);
        
        double nextX = monsterX;
        double nextY = monsterY;

        if (distance < 200) {
            // Suivre le héros
            dx /= distance;
            dy /= distance;
            nextX = monsterX + dx * BASE_SPEED * slowdownFactor;
            nextY = monsterY + dy * BASE_SPEED * slowdownFactor;
        } else {
            // Mouvement aléatoire
            long now = System.currentTimeMillis();
            if (now - lastDirectionChange > DIRECTION_CHANGE_INTERVAL) {
                double angle = Math.random() * 2 * Math.PI;
                randomDx = Math.cos(angle);
                randomDy = Math.sin(angle);
                lastDirectionChange = now;
            }
            nextX = monsterX + randomDx * RANDOM_SPEED * slowdownFactor;
            nextY = monsterY + randomDy * RANDOM_SPEED * slowdownFactor;
        }
        
        // Vérifier les collisions avec les obstacles
        if (!isTileBlocked(nextX, nextY)) {
            // Limites de la carte
            nextX = Math.max(0, Math.min(nextX, (mapCols - 1) * cellSize));
            nextY = Math.max(0, Math.min(nextY, (mapRows - 1) * cellSize));
            
            monster.setPosition(nextX, nextY);
            monsterView.updatePosition(nextX, nextY);
        }
    }

    public Monster getMonster() { return monster; }
    public double getX() {
        return monster.getX();
    }

    public double getY() {
        return monster.getY();
    }
}