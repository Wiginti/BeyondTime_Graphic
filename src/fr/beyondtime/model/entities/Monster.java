package fr.beyondtime.model.entities;

public class Monster extends Entity {
    private final int spawnX;
    private final int spawnY;
    private final int damage;
    private boolean alive;

    public Monster(int spawnX, int spawnY, int health, int damage) {
        super(health, "Monster");
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.damage = damage;
        this.alive = true;
    }

    public void die() {
        this.alive = false;
    }

    public void respawn() {
        this.setHealth(super.getHealth());
        this.alive = true;
    }

    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }
    public int getDamage() { return damage; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
}