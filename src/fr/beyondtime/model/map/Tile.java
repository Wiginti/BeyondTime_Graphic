package fr.beyondtime.model.map;

public class Tile {
    private boolean passable;
    private double slowdownFactor;

    /**
     * @param passable true si la cellule est franchissable, false sinon.
     * @param slowdownFactor facteur appliqué à la vitesse (1.0 = vitesse normale, < 1.0 = ralentissement)
     */
    public Tile(boolean passable, double slowdownFactor) {
        this.passable = passable;
        this.slowdownFactor = slowdownFactor;
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

    @Override
    public String toString() {
        return "Tile[passable=" + passable + ", slowdownFactor=" + slowdownFactor + "]";
    }
}