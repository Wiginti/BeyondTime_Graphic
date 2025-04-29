package fr.beyondtime.view.components;

import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

public class MapView {
    private GridPane gridPane;

    public MapView(String imagePath) {
        gridPane = new GridPane();
        setBackground(imagePath);
    }

    private void setBackground(String imagePath) {
        Image backgroundImage = new Image(imagePath);
        BackgroundImage background = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(100, 100, true, true, true, false)
        );
        gridPane.setBackground(new Background(background));
    }

    public GridPane getGridPane() {
        return gridPane;
    }
}