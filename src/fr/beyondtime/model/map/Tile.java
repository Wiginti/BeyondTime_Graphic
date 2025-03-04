package fr.beyondtime.model.map;

public class Tile {
    private boolean passable;
    private double slowdownFactor;
    private double damage; // Nouvelle propriété : dégâts infligés par la case

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
     * @param damage dégâts infligés lorsqu’un joueur la traverse (0 = aucun dégât)
     */
    public Tile(boolean passable, double slowdownFactor, double damage) {
        this.passable = passable;
        this.slowdownFactor = slowdownFactor;
        this.damage = damage;
    }

    public boolean isPassable() {
        return passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public double getSlowdownFactor() {
        return slowdownFactor;
    }

    public void setSlowdownFactor(double slowdownFactor) {
        this.slowdownFactor = slowdownFactor;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public String toString() {
        return "Tile[passable=" + passable + ", slowdownFactor=" + slowdownFactor + ", damage=" + damage + "]";
    }
}