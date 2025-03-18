package fr.beyondtime.view;

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

public class HUDView extends AnchorPane {

    private HBox healthBar;
    private HBox inventoryBar;
    private ImageView[] heartSlots;

    // Chargement des assets de cœurs
    private final Image heartFull = new Image(getClass().getResourceAsStream("/fr/beyondtime/resources/hearts/full.png"));
    private final Image heartHalf = new Image(getClass().getResourceAsStream("/fr/beyondtime/resources/hearts/half.png"));
    private final Image heartEmpty = new Image(getClass().getResourceAsStream("/fr/beyondtime/resources/hearts/empty.png"));

    private final int maxHearts;
    private final int inventorySlots;

    public HUDView(int maxHearts, int inventorySlots) {
        this.maxHearts = maxHearts;
        this.inventorySlots = inventorySlots;
        buildHUD();
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
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
            heart.setFitWidth(30);
            heart.setFitHeight(30);
            heartSlots[i] = heart;
            hb.getChildren().add(heart);
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
        for (int i = 0; i < maxHearts; i++) {
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
        for (int i = 0; i < inventorySlots; i++) {
            StackPane slot = (StackPane) inventoryBar.getChildren().get(i);
            if (slot.getChildren().size() > 1) {
                slot.getChildren().remove(1, slot.getChildren().size());
            }
            if (i < items.size()) {
                ImageView item = new ImageView(items.get(i));
                item.setFitWidth(30);
                item.setFitHeight(30);
                slot.getChildren().add(item);
            }
        }
    }
}
