package fr.beyondtime.controller;

import fr.beyondtime.model.entities.Monster;
import fr.beyondtime.view.entities.MonsterView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Random;

public class MonsterController {

    private Monster monster;
    private final MonsterView monsterView;
    private final HeroController heroController;
    
    private double randomDx = 0;
    private double randomDy = 0;
    private long lastDirectionChange = 0;
    private static final long DIRECTION_CHANGE_INTERVAL = 2000; // en ms
    
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
                monsterView.updatePosition(monster.getSpawnX(), monster.getSpawnY());
            }
        }));
        respawn.setCycleCount(1);
        respawn.play();
    }

    private boolean isHeroNearby() {
        double mx = monster.getX(); // plus spawnX * 50
        double my = monster.getY();
        double dx = mx - heroController.getWorldX();
        double dy = my - heroController.getWorldY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < 60; // ou ajuste selon besoin
    }
    
    public void takeDamage(int damage) {
        monster.setHealth(monster.getHealth() - damage);
        if (monster.getHealth() <= 0) {
            // Le monstre est mort
            monsterView.setVisible(false);
            if (heroController != null && heroController.getGameController() != null) {
                heroController.getGameController().incrementMonstersKilled();
            }
        }
    }
    
    public void update() {
        if (!monster.isAlive()) return;

        double monsterX = monster.getX(); // position réelle
        double monsterY = monster.getY();
        double heroX = heroController.getWorldX();
        double heroY = heroController.getWorldY();

        double dx = heroX - monsterX;
        double dy = heroY - monsterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 200) {
            // Suivre le héros
            double step = 0.8;
            dx /= distance;
            dy /= distance;
            monsterX += dx * step;
            monsterY += dy * step;
        } else {
            // Mouvement aléatoire
            long now = System.currentTimeMillis();
            if (now - lastDirectionChange > DIRECTION_CHANGE_INTERVAL) {
                double angle = Math.random() * 2 * Math.PI;
                randomDx = Math.cos(angle);
                randomDy = Math.sin(angle);
                lastDirectionChange = now;
            }
            double step = 0.5;
            monsterX += randomDx * step;
            monsterY += randomDy * step;
        }
        
        double maxX = (mapCols - 1) * 50;
        double maxY = (mapRows - 1) * 50;

        monsterX = Math.max(0, Math.min(monsterX, maxX));
        monsterY = Math.max(0, Math.min(monsterY, maxY));

        monster.setPosition(monsterX, monsterY); // mise à jour du modèle
        monsterView.updatePosition(monsterX, monsterY); // mise à jour du sprite
    }



    
    public Monster getMonster() { return monster; }
    public double getX() {
        return monster.getX();
    }

    public double getY() {
        return monster.getY();
    }

    
}