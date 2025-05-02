package fr.beyondtime.model.map;

import javafx.scene.paint.Color;

public class Tile {
    private final boolean passable;
    private final double slowdownFactor;
    private final int damage;
    private final boolean isExit;
    private final boolean isStart;

    /**
     * Constructeur pour une case sans dégâts (damage = 0 par défaut).
     */
    public Tile(boolean passable, double slowdownFactor) {
        this(passable, slowdownFactor, 0);
    }

    /**
     * Constructeur complet.
     * @param passable true si la case est franchissable
     * @param slowdownFactor facteur de ralentissement (1.0 = normal)
     * @param damage dégâts infligés lorsqu'un joueur la traverse (0 = aucun dégât)
     */
    public Tile(boolean passable, double slowdownFactor, int damage) {
        this.passable = passable;
        this.slowdownFactor = slowdownFactor;
        this.damage = damage;
        this.isExit = false;
        this.isStart = false;
    }

    public Tile(boolean passable, double slowdownFactor, int damage, boolean isExit) {
        this.passable = passable;
        this.slowdownFactor = slowdownFactor;
        this.damage = damage;
        this.isExit = isExit;
        this.isStart = false;
    }

    public Tile(boolean passable, double slowdownFactor, int damage, boolean isExit, boolean isStart) {
        this.passable = passable;
        this.slowdownFactor = slowdownFactor;
        this.damage = damage;
        this.isExit = isExit;
        this.isStart = isStart;
    }

    public boolean isPassable() {
        return passable;
    }

    public double getSlowdownFactor() {
        return slowdownFactor;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isExit() {
        return isExit;
    }

    public boolean isStart() {
        return isStart;
    }

    public Color getEditorColor() {
        if (!isPassable()) return Color.BLACK;
        if (getSlowdownFactor() < 1.0) return Color.BLUE;
        if (getDamage() > 0) return Color.PURPLE;
        if (isExit()) return Color.GREEN;
        if (isStart()) return Color.YELLOW;
        return Color.TRANSPARENT;
    }

    @Override
    public String toString() {
        return "Tile[passable=" + passable + ", slowdownFactor=" + slowdownFactor + 
               ", damage=" + damage + ", isExit=" + isExit + ", isStart=" + isStart + "]";
    }
}