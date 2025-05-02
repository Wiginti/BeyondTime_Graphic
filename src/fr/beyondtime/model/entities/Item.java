package fr.beyondtime.model.entities;

/**
 * Classe représentant un item abstrait dans le jeu.
 */
public abstract class Item {
	/** Nom de l'objet. */
    private String name;
    /** Description de l'objet. */
    private String description;
    /** Path de l'image */
    private String imagePath;

    /** Constructeur de l'item */
    /**
     * 
     * @param name
     * @param description
     * @param imagePath
     */
    public Item(String name, String description, String imagePath) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
    }
    // Getters
    /** Retourne le nom de l'item. */
    public String getName() {
        return name;
    }

    /** Retourne la description de l'item. */
    public String getDescription() {
        return description;
    }

    /** Retourne le path de l'item. */
    public String getImagePath() {
        return imagePath;
    }

    // Méthode abstraite pour l'utilisation de l'item
    public abstract void use(Hero hero);

    @Override
    public String toString() {
        return name;
    }
}
