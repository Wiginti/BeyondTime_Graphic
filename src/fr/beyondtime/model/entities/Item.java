package fr.beyondtime.model.entities;

public abstract class Item {
    private String name;
    private String description;
    private boolean isUsable;
    private boolean isStackable;
    private int maxStackSize;
    private String imagePath;

    public Item(String name, String description, String imagePath) {
        this.name = name;
        this.description = description;
        this.isUsable = isUsable;
        this.isStackable = isStackable;
        this.maxStackSize = maxStackSize;
        this.imagePath = imagePath;
    }
    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUsable() {
        return isUsable;
    }

    public boolean isStackable() {
        return isStackable;
    }

    public int getMaxStackSize() {
        return maxStackSize;
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
