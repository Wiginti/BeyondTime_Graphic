package fr.beyondtime.view.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class EntityView extends Pane {
	
	private ImageView entityImageView;
	
    public EntityView(Image entityImage) {
        entityImageView = new ImageView();
        // Chargement de l'image de l'entité depuis les ressources
        entityImageView.setImage(entityImage);
        entityImageView.setFitWidth(50);
        entityImageView.setFitHeight(50);
        getChildren().add(entityImageView);
    }

    // Permet de positionner la vue d'une Entity à la position souhaitée
    public void setPosition(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);
    }
    
    public double getEntityWidth() {
        return entityImageView.getFitWidth();
    }

    public double getEntityHeight() {
        return entityImageView.getFitHeight();
    }
	
}
