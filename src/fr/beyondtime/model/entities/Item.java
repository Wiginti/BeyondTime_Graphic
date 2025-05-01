package fr.beyondtime.model.entities;

public abstract class Item {
    private String name;
    private String description;
    private String imagePath;

    public Item(String name, String description, String imagePath) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
    }
    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

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
