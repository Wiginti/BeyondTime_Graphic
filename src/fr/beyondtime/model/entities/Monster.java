package fr.beyondtime.model.entities;

public class Monster extends Entity {
    private final int spawnX;
    private final int spawnY;
    private final int damage;
    private boolean alive;

    private double x;
    private double y;

    public Monster(int spawnX, int spawnY, int health, int damage) {
        super(health, "Monster");
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.damage = damage;
        this.alive = true;

        this.x = spawnX * 50; // Initialiser position réelle
        this.y = spawnY * 50;
    }

    public void die() {
        this.alive = false;
    }

    public void respawn() {
        this.setHealth(super.getHealth());
        this.alive = true;
        this.x = spawnX * 50;
        this.y = spawnY * 50;
    }

    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }
    public int getDamage() { return damage; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
