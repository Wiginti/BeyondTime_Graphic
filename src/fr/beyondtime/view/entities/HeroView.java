package fr.beyondtime.view.entities;

import fr.beyondtime.util.ImageLoader;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.io.File;

public class HeroView extends EntityView {
	
    private static final String HERO_IMAGE_PATH = "/hero.png";
    private static Image heroImage;

    static {
        System.out.println("Static block HeroView: Loading image...");
        heroImage = ImageLoader.loadImage(HERO_IMAGE_PATH);
        if (heroImage == null) {
             System.err.println("Hero image is null after static initialization! Check resource path and build configuration.");
        }
    }

    // Le constructeur n'attend plus cellSize
    public HeroView() {
        // Passe seulement l'image au constructeur parent
        super(heroImage);
        if (heroImage == null) {
             System.err.println("HeroView Constructor: Attempting to use a null image!");
        }
        System.out.println("HeroView created.");
    }
}