package fr.beyondtime.view.entities;

import fr.beyondtime.util.ImageLoader;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class HeroView extends EntityView {

    private static final String HERO_DOWN_PATH = "/fr/beyondtime/resources/hero.png";
    private static final String HERO_LEFT_PATH = "/fr/beyondtime/resources/hero_left.png";
    private static final String HERO_RIGHT_PATH = "/fr/beyondtime/resources/hero_right.png";
    private static final String HERO_UP_PATH = "/fr/beyondtime/resources/hero_up.png";
    
    private static Image heroDownImage;
    private static Image heroLeftImage;
    private static Image heroRightImage;
    private static Image heroUpImage;
    private ImageView imageView;

    static {
        System.out.println("Static block HeroView: Loading images...");
        heroDownImage = ImageLoader.loadImage(HERO_DOWN_PATH);
        heroLeftImage = ImageLoader.loadImage(HERO_LEFT_PATH);
        heroRightImage = ImageLoader.loadImage(HERO_RIGHT_PATH);
        heroUpImage = ImageLoader.loadImage(HERO_UP_PATH);
        
        if (heroDownImage == null) {
            System.err.println("Hero down image is null! Check path: " + HERO_DOWN_PATH);
        }
        if (heroLeftImage == null) {
            System.err.println("Hero left image is null! Check path: " + HERO_LEFT_PATH);
        }
        if (heroRightImage == null) {
            System.err.println("Hero right image is null! Check path: " + HERO_RIGHT_PATH);
        }
        if (heroUpImage == null) {
            System.err.println("Hero up image is null! Check path: " + HERO_UP_PATH);
        }
    }

    public HeroView() {
        super(heroDownImage); // Start with down-facing sprite
        if (heroDownImage == null) {
            System.err.println("HeroView: null image used!");
            return;
        }

        // Centrage de l'image
        imageView = getImageView();
        imageView.setTranslateX(-heroDownImage.getWidth() / 2);
        imageView.setTranslateY(-heroDownImage.getHeight() / 2);

        // Réduction d'échelle (50%)
        this.setScaleX(0.5);
        this.setScaleY(0.5);

        System.out.println("HeroView created with centered and scaled image.");
    }
    
    public void updateSprite(String direction) {
        Image newImage = switch (direction.toLowerCase()) {
            case "left" -> heroLeftImage;
            case "right" -> heroRightImage;
            case "up" -> heroUpImage;
            default -> heroDownImage;
        };
        
        if (newImage != null) {
            imageView.setImage(newImage);
        }
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
