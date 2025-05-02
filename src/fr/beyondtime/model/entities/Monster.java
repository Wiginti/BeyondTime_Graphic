package fr.beyondtime.model.entities;

/**
 * Classe représentant un monstre.
 */
public class Monster extends Entity {
    private final int spawnX;
    private final int spawnY;
    private final int damage;
    private boolean alive;

    private double x;
    private double y;
    
    /**
     * 
     * @param spawnX
     * @param spawnY
     * @param health
     * @param damage
     */
    public Monster(int spawnX, int spawnY, int health, int damage) {
        super(health, "Monster");
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.damage = damage;
        this.alive = true;

        this.x = spawnX * 50; // Initialiser position réelle
        this.y = spawnY * 50;
    }
    
    /** Faire mourir le monstre */
    public void die() {
        this.alive = false;
    }

    /** Faire respawn le monstre */
    public void respawn() {
        this.setHealth(super.getHealth());
        this.alive = true;
        this.x = spawnX * 50;
        this.y = spawnY * 50;
    }

    /**
     * 
     * @return spawnX
     */
    public int getSpawnX() { return spawnX; }
    
    /**
     * 
     * @return spawnY
     */
    public int getSpawnY() { return spawnY; }
    
    /**
     * 
     * @return damage
     */
    public int getDamage() { return damage; }
    
    /**
     * 
     * @return isAlive
     */
    public boolean isAlive() { return alive; }
    
    /**Fixer la mort ou la vie du monstre
    /**
     * 
     * @param alive
     */
    public void setAlive(boolean alive) { this.alive = alive; }

    /**
     * 
     * @return x
     */
    public double getX() { return x; }
    /**
     * 
     * @return y
     */
    public double getY() { return y; }
    
    /** Placer le monstre à une position x, y */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
