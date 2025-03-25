package fr.beyondtime.model;

public class ButtonModel {
    private String text;
    private double layoutX;
    private double layoutY;

    public ButtonModel() {
        // Initialisation avec les valeurs souhaitées
        this.text = "Quitter";
        this.layoutX = 720;
        this.layoutY = 10;
    }

    public String getText() {
        return text;
    }

    public double getLayoutX() {
        return layoutX;
    }

    public double getLayoutY() {
        return layoutY;
    }
}
