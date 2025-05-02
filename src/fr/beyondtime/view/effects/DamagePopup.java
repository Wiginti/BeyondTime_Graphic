package fr.beyondtime.view.effects;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class DamagePopup extends Group {
    private static final int MAX_POPUPS = 5; // Limite le nombre de popups simultanés
    private static int activePopups = 0;
    
    public DamagePopup(int damage, double x, double y) {
        // Vérifier si on peut créer un nouveau popup
        if (activePopups >= MAX_POPUPS) {
            return;
        }
        
        Text text = new Text(String.valueOf(damage));
        text.setFont(Font.font("Arial Bold", 20));
        text.setFill(Color.RED);
        text.setStroke(Color.WHITE);
        text.setStrokeWidth(1);
        
        // Centrer le texte
        text.setX(-text.getLayoutBounds().getWidth() / 2);
        
        // Ajouter le texte au groupe
        getChildren().add(text);
        
        // Positionner le popup
        setTranslateX(x);
        setTranslateY(y);
        
        // Animation de montée
        TranslateTransition rise = new TranslateTransition(Duration.millis(1000), this);
        rise.setByY(-30); // Monte de 30 pixels
        
        // Animation de fondu
        FadeTransition fade = new FadeTransition(Duration.millis(1000), this);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        
        // Combiner les animations
        ParallelTransition animation = new ParallelTransition(rise, fade);
        animation.setOnFinished(event -> {
            // Supprimer le popup une fois l'animation terminée
            if (getParent() instanceof Group) {
                ((Group) getParent()).getChildren().remove(this);
                activePopups--;
            }
        });
        
        // Incrémenter le compteur de popups actifs
        activePopups++;
        
        // Démarrer l'animation
        animation.play();
    }
} 