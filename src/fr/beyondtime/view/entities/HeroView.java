package fr.beyondtime.view.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class HeroView extends Pane {
    private ImageView heroImageView;

    public HeroView() {
        heroImageView = new ImageView();
        // Chargement de l'image du héros depuis les ressources
        Image heroImage = new Image(getClass().getResourceAsStream("/fr/beyondtime/assets/hero.png"));        heroImageView.setImage(heroImage);
        heroImageView.setFitWidth(50);
        heroImageView.setFitHeight(50);
        getChildren().add(heroImageView);
    }

    // Permet de positionner la vue du héros à la position souhaitée
    public void setPosition(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);
    }

    public double getHeroWidth() {
        return heroImageView.getFitWidth();
    }

    public double getHeroHeight() {
        return heroImageView.getFitHeight();
    }
}