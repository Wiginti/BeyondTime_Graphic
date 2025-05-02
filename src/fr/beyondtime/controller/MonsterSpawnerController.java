package fr.beyondtime.controller;

import fr.beyondtime.model.entities.Monster;
import fr.beyondtime.view.entities.MonsterView;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonsterSpawnerController {

    private final List<MonsterController> activeControllers = new ArrayList<>();
    @SuppressWarnings("unused")
    private final Random random = new Random();

    public List<Node> spawnMonsters(GridPane mapGrid, Group cameraGroup, HeroController heroController, int cellSize) {
        List<Node> views = new ArrayList<>();

        for (Node node : mapGrid.getChildren()) {
            if (node instanceof StackPane cell) {
                Object isSpawner = cell.getProperties().get("isSpawner");
                if (isSpawner instanceof Boolean && (Boolean) isSpawner) {

                    Integer row = GridPane.getRowIndex(cell);
                    Integer col = GridPane.getColumnIndex(cell);
                    if (row == null) row = 0;
                    if (col == null) col = 0;

                    Monster monster = new Monster(col, row, 50, 10);
                    MonsterView view = new MonsterView();
                    view.updatePosition(col, row);

                    cameraGroup.getChildren().add(view);

                    MonsterController controller = new MonsterController(monster, view, heroController);
                    controller.startRespawnTimer(cell, cameraGroup);

                    activeControllers.add(controller);
                    views.add(view);
                    
                    monster.setPosition(col * cellSize, row * cellSize);
                    view.updatePosition(col * cellSize, row * cellSize);


                }
            }
        }

        return views;
    }

    public List<MonsterController> getActiveControllers() {
        return activeControllers;
    }
}
