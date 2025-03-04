package fr.beyondtime.view;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;

import java.util.List;

public class HUDView extends AnchorPane {

    private HBox healthBar;
    private HBox inventoryBar;
    private ImageView[] heartSlots;

    // Chemins d'accès aux assets de cœur (à adapter selon l'emplacement de vos fichiers)
    private final Image heartFull;
    private final Image heartHalf;
    private final Image heartEmpty;

    private final int maxHearts;
    private final int inventorySlots;

    public HUDView(int maxHearts, int inventorySlots) {
        this.maxHearts = maxHearts;
        this.inventorySlots = inventorySlots;

        // Chargement des assets de cœurs
        heartFull = new Image(getClass().getResourceAsStream("/fr/beyondtime/resources/hearts/full.png"));
        heartHalf = new Image(getClass().getResourceAsStream("/fr/beyondtime/resources/hearts/half.png"));
        heartEmpty = new Image(getClass().getResourceAsStream("/fr/beyondtime/resources/hearts/empty.png"));

        buildHUD();
    }

    private void buildHUD() {
        // Construction de la barre de vie (health bar)
        healthBar = new HBox(5);
        heartSlots = new ImageView[maxHearts];
        for (int i = 0; i < maxHearts; i++) {
            ImageView heartView = new ImageView(heartFull);
            heartView.setFitWidth(30);
            heartView.setFitHeight(30);
            heartSlots[i] = heartView;
            healthBar.getChildren().add(heartView);
        }

        // Construction de la barre d'inventaire
        inventoryBar = new HBox(10);
        for (int i = 0; i < inventorySlots; i++) {
            StackPane slot = new StackPane();
            slot.setPrefSize(40, 40);
            Rectangle bg = new Rectangle(40, 40);
            bg.setFill(Color.TRANSPARENT);
            bg.setStroke(Color.WHITE);
            slot.getChildren().add(bg);
            inventoryBar.getChildren().add(slot);
        }

        // Disposer le HUD dans un VBox et le positionner en haut de l'écran
        VBox hudContainer = new VBox(10);
        hudContainer.getChildren().addAll(healthBar, inventoryBar);
        hudContainer.setPadding(new Insets(10));

        // On peut aussi ajouter un titre ou d'autres informations
        Label hudTitle = new Label("HUD");
        hudTitle.setTextFill(Color.WHITE);
        hudContainer.getChildren().add(0, hudTitle);

        // Positionner le conteneur HUD en haut à gauche
        setTopAnchor(hudContainer, 0.0);
        setLeftAnchor(hudContainer, 0.0);
        getChildren().add(hudContainer);
    }

    /**
     * Met à jour l'affichage de la barre de vie.
     * @param health La santé actuelle exprimée en unités (exemple : 3.5 pour 3 cœurs pleins et un demi-cœur).
     * On suppose ici que la santé maximale est égale au nombre de cœurs.
     */
    public void updateHealth(double health) {
        for (int i = 0; i < maxHearts; i++) {
            double heartValue = health - i;
            if (heartValue >= 1) {
                heartSlots[i].setImage(heartFull);
            } else if (heartValue >= 0.5) {
                heartSlots[i].setImage(heartHalf);
            } else {
                heartSlots[i].setImage(heartEmpty);
            }
        }
    }

    /**
     * Met à jour l'affichage de l'inventaire en plaçant des items dans les slots.
     * @param items Une liste d'images représentant les items à afficher.
     * Les items seront affichés dans l'ordre dans les slots disponibles.
     */
    public void updateInventory(List<Image> items) {
        for (int i = 0; i < inventorySlots; i++) {
            StackPane slot = (StackPane) inventoryBar.getChildren().get(i);
            // On conserve le fond du slot (premier enfant) et on supprime d'éventuels items précédents
            if (slot.getChildren().size() > 1) {
                slot.getChildren().remove(1, slot.getChildren().size());
            }
            if (i < items.size()) {
                ImageView itemView = new ImageView(items.get(i));
                itemView.setFitWidth(30);
                itemView.setFitHeight(30);
                slot.getChildren().add(itemView);
            }
        }
    }
}