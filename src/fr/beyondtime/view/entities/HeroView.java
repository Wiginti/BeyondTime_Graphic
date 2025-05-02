package fr.beyondtime.view.entities;

import fr.beyondtime.util.ImageLoader;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class HeroView extends EntityView {

    private static final String HERO_IMAGE_PATH = "/fr/beyondtime/resources/hero.png";
    private static Image heroImage;
    private ImageView imageView;

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
        imageView = getImageView();
        imageView.setTranslateX(-heroImage.getWidth() / 2);
        imageView.setTranslateY(-heroImage.getHeight() / 2);

        // Réduction d'échelle (50%)
        this.setScaleX(0.5);
        this.setScaleY(0.5);

        System.out.println("HeroView created with centered and scaled image.");
    }
    
    public void playHitEffect() {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(-0.5); // rouge

        imageView.setEffect(colorAdjust);

        FadeTransition fade = new FadeTransition(Duration.millis(100), imageView);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);
        fade.play();

        PauseTransition pause = new PauseTransition(Duration.millis(150));
        pause.setOnFinished(e -> imageView.setEffect(null));
        pause.play();
    }

}
