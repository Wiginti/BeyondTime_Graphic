package fr.beyondtime.model;

/**
 * Modèle pour un bouton (par exemple sauvegarder, annuler).
 */
public class ButtonModel {
    private String text;
    private double layoutX;
    private double layoutY;
    
    /** Constructeur du modèle de bouton. */
    public ButtonModel() {
        // Initialisation avec les valeurs souhaitées
        this.text = "Quitter";
        this.layoutX = 720;
        this.layoutY = 10;
    }
    
    /**
     * 
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @return LayoutX
     */
    public double getLayoutX() {
        return layoutX;
    }

    /**
     * 
     * @return LayoutY
     */
    public double getLayoutY() {
        return layoutY;
    }
}
