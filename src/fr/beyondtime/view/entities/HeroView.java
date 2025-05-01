package fr.beyondtime.view.entities;

import fr.beyondtime.util.ImageLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HeroView extends EntityView {

    private static final String HERO_IMAGE_PATH = "/fr/beyondtime/resources/hero.png";
    private static Image heroImage;

    static {
        System.out.println("Static block HeroView: Loading image...");
        heroImage = ImageLoader.loadImage(HERO_IMAGE_PATH);
        if (heroImage == null) {
            System.err.println("Hero image is null! Check path: " + HERO_IMAGE_PATH);
        }
    }

    public HeroView() {
        super(heroImage);
        if (heroImage == null) {
            System.err.println("HeroView: null image used!");
            return;
        }

        // Centrage de l'image
        ImageView iv = getImageView();
        iv.setTranslateX(-heroImage.getWidth() / 2);
        iv.setTranslateY(-heroImage.getHeight() / 2);

        // Réduction d'échelle (50%)
        this.setScaleX(0.5);
        this.setScaleY(0.5);

        System.out.println("HeroView created with centered and scaled image.");
    }
}
