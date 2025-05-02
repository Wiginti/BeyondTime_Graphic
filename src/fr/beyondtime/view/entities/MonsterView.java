package fr.beyondtime.view.entities;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class MonsterView extends EntityView {

    private static final int TILE_SIZE = 50;
    private final ImageView imageView;

    public MonsterView() {
        super(new Image(MonsterView.class.getResourceAsStream("/fr/beyondtime/resources/monster.png")));
        imageView = getImageView();
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        imageView.setPreserveRatio(true);
    }

    public void updatePosition(double x, double y) {
        setTranslateX(x);
        setTranslateY(y);
    }


    public void hide() {
        setVisible(false);
    }

    public void show() {
        setVisible(true);
    }
    
    public void playHitEffect() {
        // Appliquer une teinte rouge directement sur le sprite
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(-0.5); // teinte rougeâtre
        imageView.setEffect(colorAdjust);

        // Lancer un effet de clignotement léger
        FadeTransition fade = new FadeTransition(Duration.millis(100), imageView);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);
        fade.play();

        // Enlever l'effet rouge après un court délai
        PauseTransition pause = new PauseTransition(Duration.millis(150));
        pause.setOnFinished(e -> imageView.setEffect(null));
        pause.play();
    }

}
