package fr.beyondtime.model.map;

public class Tile {
    private final boolean passable;
    private final double slowdownFactor;
    private final int damage;
    private final boolean isExit;

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
    }

    public Tile(boolean passable, double slowdownFactor, int damage, boolean isExit) {
        this.passable = passable;
        this.slowdownFactor = slowdownFactor;
        this.damage = damage;
        this.isExit = isExit;
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

    @Override
    public String toString() {
        return "Tile[passable=" + passable + ", slowdownFactor=" + slowdownFactor + ", damage=" + damage + "]";
    }
}