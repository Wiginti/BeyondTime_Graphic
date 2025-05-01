package fr.beyondtime.view.components;

import fr.beyondtime.util.ImageLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.List;
import java.io.InputStream;

public class HUDView extends AnchorPane {

    private HBox healthBar;
    private HBox inventoryBar;
    private ImageView[] heartSlots;
    private int selectedSlot = -1; // Aucun slot sélectionné par défaut

    // Chemins corrects des images de coeurs
    private static final String FULL_HEART_PATH = "/fr/beyondtime/resources/hearts/full.png";
    private static final String HALF_HEART_PATH = "/fr/beyondtime/resources/hearts/half.png";
    private static final String EMPTY_HEART_PATH = "/fr/beyondtime/resources/hearts/empty.png";

    private Image heartFull;
    private Image heartHalf;
    private Image heartEmpty;

    private final int maxHearts;
    private final int inventorySlots;

    public HUDView(int maxHearts, int inventorySlots) {
        this.maxHearts = maxHearts;
        this.inventorySlots = inventorySlots;
        loadHeartImages();
        buildHUD();
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void loadHeartImages() {
        // Utiliser ImageLoader avec les chemins corrigés
        heartFull = ImageLoader.loadImage(FULL_HEART_PATH);
        heartHalf = ImageLoader.loadImage(HALF_HEART_PATH);
        heartEmpty = ImageLoader.loadImage(EMPTY_HEART_PATH);

        if (heartFull == null || heartHalf == null || heartEmpty == null) {
             System.err.println("HUDView Error: One or more heart images failed to load. Check paths:");
             System.err.println("  Full: " + FULL_HEART_PATH + (heartFull == null ? " (Failed)" : " (OK)"));
             System.err.println("  Half: " + HALF_HEART_PATH + (heartHalf == null ? " (Failed)" : " (OK)"));
             System.err.println("  Empty: " + EMPTY_HEART_PATH + (heartEmpty == null ? " (Failed)" : " (OK)"));
        }
    }

    private void buildHUD() {
        healthBar = buildHealthBar();
        inventoryBar = buildInventory();

        // Conteneur pour la barre de vie en haut
        HBox healthContainer = new HBox(healthBar);
        healthContainer.setPadding(new Insets(10));
        healthContainer.setAlignment(Pos.TOP_LEFT);

        // Conteneur pour l'inventaire en bas
        HBox inventoryContainer = new HBox(inventoryBar);
        inventoryContainer.setPadding(new Insets(10));
        inventoryContainer.setAlignment(Pos.CENTER);
        inventoryContainer.setPrefHeight(60);

        // Filler pour occuper l'espace restant entre le top et le bottom
        Region filler = new Region();
        VBox.setVgrow(filler, Priority.ALWAYS);

        // Utilisation d'un VBox pour empiler verticalement :
        // - La barre de vie en haut
        // - Le filler qui pousse l'inventaire vers le bas
        VBox vbox = new VBox();
        vbox.getChildren().addAll(healthContainer, filler, inventoryContainer);

        // Ancrer le VBox sur toute la surface du HUDView
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox, 0.0);
        AnchorPane.setLeftAnchor(vbox, 0.0);
        AnchorPane.setRightAnchor(vbox, 0.0);

        getChildren().add(vbox);
    }

    public HBox buildHealthBar() {
        HBox hb = new HBox(5);
        heartSlots = new ImageView[maxHearts];
        for (int i = 0; i < maxHearts; i++) {
            ImageView heart = new ImageView(heartFull);
            if (heartFull == null) { 
                 heart.setImage(null); 
                 Rectangle placeholder = new Rectangle(30, 30, Color.PINK); 
                 StackPane sp = new StackPane(placeholder, heart); 
                 hb.getChildren().add(sp);
            } else {
                 hb.getChildren().add(heart);
            }
            heart.setFitWidth(30);
            heart.setFitHeight(30);
            heartSlots[i] = heart;
        }
        return hb;
    }

    public HBox buildInventory() {
        HBox ib = new HBox(10);
        ib.setAlignment(Pos.CENTER);
        for (int i = 0; i < inventorySlots; i++) {
            StackPane slot = new StackPane();
            slot.setPrefSize(40, 40);
            Rectangle bg = new Rectangle(40, 40);
            bg.setFill(Color.TRANSPARENT);
            bg.setStroke(Color.WHITE);
            slot.getChildren().add(bg);
            ib.getChildren().add(slot);
        }
        return ib;
    }

    public void updateHealth(double health) {
        if (heartSlots == null || heartFull == null || heartHalf == null || heartEmpty == null) return;
        
        for (int i = 0; i < maxHearts; i++) {
            if (heartSlots[i] == null) continue;
            double val = health - i;
            if (val >= 1) {
                heartSlots[i].setImage(heartFull);
            } else if (val >= 0.5) {
                heartSlots[i].setImage(heartHalf);
            } else {
                heartSlots[i].setImage(heartEmpty);
            }
        }
    }

    public void updateInventory(List<Image> items) {
        if (inventoryBar == null || items == null) return;
        for (int i = 0; i < inventorySlots; i++) {
            StackPane slot = (StackPane) inventoryBar.getChildren().get(i);
            if (slot.getChildren().size() > 1) {
                slot.getChildren().remove(1, slot.getChildren().size());
            }
            if (i < items.size() && items.get(i) != null) {
                ImageView item = new ImageView(items.get(i));
                item.setFitWidth(30);
                item.setFitHeight(30);
                slot.getChildren().add(item);
            }
        }
    }

    public void selectSlot(int index) {
        System.out.println("Tentative de sélection du slot " + index);
        if (index < 0 || index >= inventorySlots) {
            System.out.println("Index invalide : " + index);
            return;
        }
        
        // Réinitialiser la sélection précédente
        if (selectedSlot >= 0) {
            System.out.println("Réinitialisation de la sélection précédente du slot " + selectedSlot);
            StackPane previousSlot = (StackPane) inventoryBar.getChildren().get(selectedSlot);
            Rectangle previousBg = (Rectangle) previousSlot.getChildren().get(0);
            previousBg.setStroke(Color.WHITE);
            previousBg.setStrokeWidth(1);
        }
        
        // Mettre à jour la nouvelle sélection
        selectedSlot = index;
        System.out.println("Nouvelle sélection du slot " + selectedSlot);
        StackPane currentSlot = (StackPane) inventoryBar.getChildren().get(selectedSlot);
        Rectangle currentBg = (Rectangle) currentSlot.getChildren().get(0);
        currentBg.setStroke(Color.BLUE);
        currentBg.setStrokeWidth(3); // Augmenter l'épaisseur de la bordure pour mieux voir la sélection
    }
}
