package fr.beyondtime.view.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MonsterView extends EntityView {

    private static final int TILE_SIZE = 50;

    public MonsterView() {
        super(new Image(MonsterView.class.getResourceAsStream("/fr/beyondtime/resources/monster.png")));
        ImageView imageView = getImageView();
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        imageView.setPreserveRatio(true);
    }

    public void updatePosition(int gridX, int gridY) {
        setLayoutX(gridX * TILE_SIZE);
        setLayoutY(gridY * TILE_SIZE);
    }

    public void hide() {
        setVisible(false);
    }

    public void show() {
        setVisible(true);
    }
}
