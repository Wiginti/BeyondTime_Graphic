package fr.beyondtime.view.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class EntityView extends Pane {
	
	private ImageView entityImageView;

    public EntityView(Image entityImage) {
        if (entityImage == null || entityImage.isError()) {
             if (entityImage == null) {
                 System.err.println("EntityView Error: Provided entityImage is null! Cannot display entity.");
             } else {
                 System.err.println("EntityView Error: Failed to load image. URL: " + (entityImage.getUrl() != null ? entityImage.getUrl() : "N/A"));
                 System.err.println("Exception: " + entityImage.getException());
             }
            entityImageView = new ImageView(); 
        } else {
            entityImageView = new ImageView(entityImage);
            
            entityImageView.setPreserveRatio(true);
            entityImageView.setSmooth(false);
            
            System.out.println("EntityView: Image loaded successfully for " + this.getClass().getSimpleName() + 
                             ", Natural Size: " + entityImage.getWidth() + "x" + entityImage.getHeight());
        }
        
        getChildren().add(entityImageView); 
    }

    public void setPosition(double x, double y) {
        setLayoutX(Math.floor(x));
        setLayoutY(Math.floor(y));
    }
}
