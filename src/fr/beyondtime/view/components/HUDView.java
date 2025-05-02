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
import javafx.scene.text.Text;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class HUDView extends AnchorPane {

    private HBox healthBar;
    private HBox inventoryBar;
    private ImageView[] heartSlots;
    private int selectedSlot = -1;
    private Text swordStatusText;
    private ProgressBar staminaBar;
    private final int maxHearts;
    private final int inventorySlots;

    private static final String FULL_HEART_PATH = "/fr/beyondtime/resources/hearts/full.png";
    private static final String HALF_HEART_PATH = "/fr/beyondtime/resources/hearts/half.png";
    private static final String EMPTY_HEART_PATH = "/fr/beyondtime/resources/hearts/empty.png";

    private Image heartFull;
    private Image heartHalf;
    private Image heartEmpty;

    public HUDView(int maxHearts, int inventorySlots) {
        this.maxHearts = maxHearts;
        this.inventorySlots = inventorySlots;
        loadHeartImages();

        // Barre de stamina en haut à droite
        staminaBar = new ProgressBar(1.0);
        staminaBar.setStyle("-fx-accent: green;");
        staminaBar.setPrefWidth(150);
        staminaBar.setMaxWidth(150);
        AnchorPane.setTopAnchor(staminaBar, 10.0);
        AnchorPane.setRightAnchor(staminaBar, 10.0);
        getChildren().add(staminaBar);

        // Barre de vie
        healthBar = buildHealthBar();
        AnchorPane.setTopAnchor(healthBar, 10.0);
        AnchorPane.setLeftAnchor(healthBar, 10.0);
        getChildren().add(healthBar);

        // Inventaire
        inventoryBar = buildInventory();
        AnchorPane.setBottomAnchor(inventoryBar, 10.0);
        AnchorPane.setLeftAnchor(inventoryBar, 10.0);
        getChildren().add(inventoryBar);

        // Status de l'épée
        swordStatusText = new Text("Épée non équipée");
        swordStatusText.setFill(Color.WHITE);
        AnchorPane.setBottomAnchor(swordStatusText, 50.0);
        AnchorPane.setLeftAnchor(swordStatusText, 10.0);
        getChildren().add(swordStatusText);
    }

    private void loadHeartImages() {
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
        ib.setAlignment(Pos.CENTER_LEFT);
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
        if (index < 0 || index >= inventorySlots) return;
        
        if (selectedSlot >= 0) {
            StackPane previousSlot = (StackPane) inventoryBar.getChildren().get(selectedSlot);
            Rectangle previousBg = (Rectangle) previousSlot.getChildren().get(0);
            previousBg.setStroke(Color.WHITE);
            previousBg.setStrokeWidth(1);
        }
        
        selectedSlot = index;
        StackPane currentSlot = (StackPane) inventoryBar.getChildren().get(selectedSlot);
        Rectangle currentBg = (Rectangle) currentSlot.getChildren().get(0);
        currentBg.setStroke(Color.BLUE);
        currentBg.setStrokeWidth(3);
    }

    public void updateSwordStatus(boolean isEquipped) {
        if (swordStatusText != null) {
            swordStatusText.setText(isEquipped ? "Épée équipée" : "Épée non équipée");
            swordStatusText.setFill(isEquipped ? Color.GREEN : Color.WHITE);
        }
    }

    public void updateStamina(double value) {
        staminaBar.setProgress(value);
        if (value < 0.3) {
            staminaBar.setStyle("-fx-accent: red;");
        } else if (value < 0.6) {
            staminaBar.setStyle("-fx-accent: orange;");
        } else {
            staminaBar.setStyle("-fx-accent: green;");
        }
    }
}
