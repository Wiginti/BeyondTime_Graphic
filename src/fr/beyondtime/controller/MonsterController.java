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

    private Timeline attackLoop;
    private final Random random = new Random();

    public MonsterController(Monster monster, MonsterView monsterView, HeroController heroController) {
        this.monster = monster;
        this.monsterView = monsterView;
        this.heroController = heroController;

        attackLoop = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            if (monster.isAlive() && isHeroNearby()) {
                heroController.takeDamage(monster.getDamage());
                System.out.println(monster.getName() + " attaque le héros ! Vie : " + heroController.getHero().getHealth());
                if (heroController.getHero().getHealth() <= 0) {
                    System.out.println("Le héros est mort.");
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

                System.out.println("Monstre réapparu en (" + monster.getSpawnX() + ", " + monster.getSpawnY() + ")");
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
    
    public void takeDamage(int amount) {
        if (!monster.isAlive()) return;

        int newHealth = monster.getHealth() - amount;
        if (newHealth <= 0) {
            monster.die();
            monsterView.hide();
        } else {
            monster.setHealth(newHealth);
            monsterView.playHitEffect();
        }
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

        if (distance < 200) {
            double step = 1.0;
            dx /= distance;
            dy /= distance;

            monsterX += dx * step;
            monsterY += dy * step;

            monster.setPosition(monsterX, monsterY);
            monsterView.updatePosition(monsterX, monsterY);

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